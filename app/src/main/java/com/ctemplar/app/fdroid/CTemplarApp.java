package com.ctemplar.app.fdroid;

import android.app.Application;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.ctemplar.app.fdroid.net.RestClient;
import com.ctemplar.app.fdroid.repository.AppDatabase;
import com.ctemplar.app.fdroid.repository.ContactsRepository;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.MessagesRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.UserStoreImpl;

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

    public static MessagesRepository getMessagesRepository() {
        return messagesRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Timber.plant(new Timber.DebugTree());
        installProviders(this);
    }

    public static CTemplarApp getInstance() {
        if(instance == null) {
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
