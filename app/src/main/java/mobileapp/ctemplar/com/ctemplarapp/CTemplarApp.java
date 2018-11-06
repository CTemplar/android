package mobileapp.ctemplar.com.ctemplarapp;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import mobileapp.ctemplar.com.ctemplarapp.net.RestClient;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStoreImpl;

public class CTemplarApp extends MultiDexApplication {

    private static CTemplarApp instance = null;
    private static RestClient restClient;
    private static UserStore userStore;
    private static UserRepository userRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        installProviders(this);
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
    }
}
