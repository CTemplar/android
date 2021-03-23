package mobileapp.ctemplar.com.ctemplarapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import java.security.Security;

import io.sentry.android.core.SentryAndroid;
import mobileapp.ctemplar.com.ctemplarapp.net.RestClient;
import mobileapp.ctemplar.com.ctemplarapp.repository.AppDatabase;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStoreImpl;
import mobileapp.ctemplar.com.ctemplarapp.splash.PINLockActivity;
import mobileapp.ctemplar.com.ctemplarapp.splash.SplashActivity;
import timber.log.Timber;

public class CTemplarApp extends MultiDexApplication {
    private static CTemplarApp instance = null;
    private static RestClient restClient;
    private static UserStore userStore;
    private static UserRepository userRepository;
    private static MessagesRepository messagesRepository;
    private static ContactsRepository contactsRepository;
    private static ManageFoldersRepository manageFoldersRepository;
    private static AppDatabase appDatabase;

    private final ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NotNull Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NotNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NotNull Activity activity) {
            if (activity instanceof PINLockActivity) {
                return;
            }
            if (activity instanceof SplashActivity) {
                return;
            }
            checkPINLock();
        }

        @Override
        public void onActivityPaused(@NotNull Activity activity) {
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
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static CTemplarApp getInstance() {
        if (instance == null) {
            instance = new CTemplarApp();
        }
        return instance;
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public static UserStore getUserStore() {
        return userStore;
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
        if (restClient == null) {
            restClient = RestClient.instance();
        }
        if (userStore == null) {
            userStore = UserStoreImpl.getInstance(application);
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

    private void checkPINLock() {
        if (!getUserStore().isPINLockEnabled()) {
            return;
        }
        if (userStore.isLocked()) {
            launchLockScreen();
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - userStore.getLastPauseTime() >= getUserStore().getAutoLockTime()) {
            userStore.setLocked(true);
            launchLockScreen();
        }
    }

    private void launchLockScreen() {
        Intent intent = new Intent(this, PINLockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void onUnlocked() {
        userStore.setLastPauseTime(Long.MAX_VALUE);
        userStore.setLocked(false);
    }
}
