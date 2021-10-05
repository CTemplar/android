package com.ctemplar.app.fdroid.net;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.services.NotificationService;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.webkit.WebkitProxy;
import timber.log.Timber;

public class ProxyController {
    private static final String TOR_PROXY_HOST = "127.0.0.1";
    private static final int TOR_PROXY_PORT = 8118;

    private final UserStore userStore;
    private final MutableLiveData<RestClient> restClient;
    private final Application application;

    public ProxyController(Application application, UserStore userStore, MutableLiveData<RestClient> restClientLiveData) {
        this.application = application;
        this.userStore = userStore;
        this.restClient = restClientLiveData;
        restClient.setValue(RestClient.instance());
        if (isProxyEnabled(userStore)) {
            enableWebKitProxy();
        }
        if (userStore.isProxyTorEnabled()) {
            OrbotHelper.get(application).init();
        }
    }

    public void enableTorProxy() {
        userStore.setProxyTorEnabled(true);
        userStore.setProxyHttpEnabled(false);
        restClient.setValue(RestClient.instance());
        enableWebKitProxy();
        onProxyChanged();
    }

    public void disableTorProxy() {
        userStore.setProxyTorEnabled(false);
        restClient.setValue(RestClient.instance());
        disableWebKitProxy();
        onProxyChanged();
    }

    public void setHttpProxyIP(String ip) {
        userStore.setProxyIP(ip);
    }

    public void setHttpProxyPort(int port) {
        userStore.setProxyPort(port);
    }

    public void enableHttpProxy() {
        userStore.setProxyHttpEnabled(true);
        userStore.setProxyTorEnabled(false);
        restClient.setValue(RestClient.instance());
        enableWebKitProxy();
        onProxyChanged();
    }

    public void disableHttpProxy() {
        userStore.setProxyHttpEnabled(false);
        restClient.setValue(RestClient.instance());
        disableWebKitProxy();
        onProxyChanged();
    }

    private void enableWebKitProxy() {
        try {
            WebkitProxy.setProxy(ProxyController.class.getName(), application, null,
                    getProxyIP(userStore), getProxyPort(userStore));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void disableWebKitProxy() {
        try {
            WebkitProxy.resetProxy(ProxyController.class.getName(), application);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void onProxyChanged() {
        NotificationService.restart(application);
    }

    public static boolean isProxyEnabled(UserStore userStore) {
        return userStore.isProxyTorEnabled() || userStore.isProxyHttpEnabled();
    }

    public static boolean isProxyProvided(UserStore userStore) {
        return userStore.getProxyIP() != null && userStore.getProxyPort() != 0;
    }

    public static String getProxyIP(UserStore userStore) {
        if (userStore.isProxyTorEnabled()) {
            return TOR_PROXY_HOST;
        } else if (userStore.isProxyHttpEnabled()) {
            return userStore.getProxyIP();
        }
        return null;
    }

    public static int getProxyPort(UserStore userStore) {
        if (userStore.isProxyTorEnabled()) {
            return TOR_PROXY_PORT;
        } else if (userStore.isProxyHttpEnabled()) {
            return userStore.getProxyPort();
        }
        return 0;
    }
}
