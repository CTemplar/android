package com.ctemplar.app.fdroid;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import io.sentry.android.core.SentryAndroid;

import com.ctemplar.app.fdroid.net.ProxyController;
import com.ctemplar.app.fdroid.net.RestClient;
import com.ctemplar.app.fdroid.repository.AppDatabase;
import com.ctemplar.app.fdroid.repository.ContactsRepository;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.MessagesRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.UserStoreImpl;
import com.ctemplar.app.fdroid.services.NotificationService;
import com.ctemplar.app.fdroid.services.NotificationServiceBroadcastReceiver;
import com.ctemplar.app.fdroid.splash.PINLockActivity;
import com.ctemplar.app.fdroid.splash.SplashActivity;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

import java.lang.ref.WeakReference;
import java.security.Security;

import timber.log.Timber;

public class CTemplarApp extends MultiDexApplication {
    private static CTemplarApp instance = null;
    private static final MutableLiveData<RestClient> restClient = new MutableLiveData<>();
    private static UserStore userStore;
    private static UserRepository userRepository;
    private static MessagesRepository messagesRepository;
    private static ContactsRepository contactsRepository;
    private static ManageFoldersRepository manageFoldersRepository;
    private static AppDatabase appDatabase;
    private static WeakReference<Activity> currentActivityReference;
    private static NotificationServiceBroadcastReceiver notificationServiceBroadcastReceiver;
    private static ProxyController proxyController;

    private final ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NotNull Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NotNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NotNull Activity activity) {
            currentActivityReference = new WeakReference<>(activity);
            if (activity instanceof PINLockActivity) {
                return;
            }
            if (activity instanceof SplashActivity) {
                return;
            }
            checkPINLock(activity);
        }

        @Override
        public void onActivityPaused(@NotNull Activity activity) {
            currentActivityReference = null;
            if (activity instanceof SplashActivity) {
                return;
            }
            if (!userStore.isLocked()) {
                userStore.setLastPauseTime(System.currentTimeMillis());
            }
        }

        @Override
        public void onActivityStopped(@NotNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NotNull Activity activity) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        installProviders(this);
        installDebug(this);
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        notificationServiceBroadcastReceiver = new NotificationServiceBroadcastReceiver();
        notificationServiceBroadcastReceiver.register(this);
        NotificationService.updateState(this);
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static boolean isAuthorized() {
        String token = userRepository.getUserToken();
        return EditTextUtils.isNotEmpty(token);
    }

    public static CTemplarApp getInstance() {
        if (instance == null) {
            instance = new CTemplarApp();
        }
        return instance;
    }

    public static boolean isInForeground() {
        return getCurrentActivity() != null;
    }

    public static Activity getCurrentActivity() {
        if (currentActivityReference == null) {
            return null;
        }
        return currentActivityReference.get();
    }

    public static LiveData<RestClient> getRestClientLiveData() {
        return restClient;
    }

    public static UserStore getUserStore() {
        return userStore;
    }

    public static ProxyController getProxyController() {
        return proxyController;
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static ContactsRepository getContactsRepository() {
        return contactsRepository;
    }

    public static ManageFoldersRepository getManageFoldersRepository() {
        return manageFoldersRepository;
    }

    public static MessagesRepository getMessagesRepository() {
        return messagesRepository;
    }

    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private static synchronized void installProviders(Application application) {
        if (userStore == null) {
            userStore = UserStoreImpl.getInstance(application);
        }
        if (proxyController == null) {
            proxyController = new ProxyController(application, userStore, restClient);
        }
        if (userRepository == null) {
            userRepository = UserRepository.getInstance();
        }
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(application, AppDatabase.class, "database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        if (contactsRepository == null) {
            contactsRepository = new ContactsRepository();
        }
        if (manageFoldersRepository == null) {
            manageFoldersRepository = new ManageFoldersRepository();
        }
        if (messagesRepository == null) {
            messagesRepository = MessagesRepository.getInstance();
        }
    }

    private static void installDebug(Application application) {
        Timber.plant(new Timber.DebugTree());
        if (userStore.isReportBugsEnabled()) {
            SentryAndroid.init(application);
        }
    }

    private void checkPINLock(Activity activity) {
        if (!getUserStore().isPINLockEnabled()) {
            return;
        }
        if (userStore.isLocked()) {
            launchLockScreen(activity);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - userStore.getLastPauseTime() >= getUserStore().getAutoLockTime()) {
            userStore.setLocked(true);
            launchLockScreen(activity);
        }
    }

    private void launchLockScreen(Activity activity) {
        Intent intent = new Intent(this, PINLockActivity.class);
        activity.startActivity(intent);
    }

    public void onUnlocked() {
        userStore.setLastPauseTime(Long.MAX_VALUE);
        userStore.setLocked(false);
    }
}
