package com.ctemplar.app.fdroid;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.ctemplar.app.fdroid.net.RestClient;
import com.ctemplar.app.fdroid.notification.NotificationService;
import com.ctemplar.app.fdroid.notification.NotificationServiceBroadcastReceiver;
import com.ctemplar.app.fdroid.repository.AppDatabase;
import com.ctemplar.app.fdroid.repository.ContactsRepository;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.MessagesRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.UserStoreImpl;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

import java.lang.ref.WeakReference;

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
    private static WeakReference<Activity> currentActivityReference;
    private static NotificationServiceBroadcastReceiver notificationServiceBroadcastReceiver;
    private static final ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            currentActivityReference = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public static MessagesRepository getMessagesRepository() {
        return messagesRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Timber.plant(new Timber.DebugTree());
        installProviders(this);
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        notificationServiceBroadcastReceiver = new NotificationServiceBroadcastReceiver();
        notificationServiceBroadcastReceiver.register(this);
        NotificationService.updateState(this);
    }

    public static boolean isAuthorized() {
        String token = userRepository.getUserToken();
        return EditTextUtils.isNotEmpty(token);
    }

    public static CTemplarApp getInstance() {
        if(instance == null) {
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

    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private static synchronized void installProviders(Application application) {

        if(restClient == null) {
            restClient = RestClient.instance();
        }

        if(userStore == null) {
            userStore = UserStoreImpl.getInstance(application);
        }

        if(userRepository == null) {
            userRepository = UserRepository.getInstance();
        }

        if(appDatabase == null) {
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
}
