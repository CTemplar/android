package mobileapp.ctemplar.com.ctemplarapp.net;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import java.net.Proxy;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.webkit.WebkitProxy;
import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import timber.log.Timber;

public class ProxyController {
    private static final String TOR_PROXY_HOST = "127.0.0.1";
    private static final int TOR_PROXY_PORT = 9050;

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
        userStore.setProxyCustomEnabled(false);
        restClient.setValue(RestClient.instance());
        enableWebKitProxy();
    }

    public void disableTorProxy() {
        userStore.setProxyTorEnabled(false);
        restClient.setValue(RestClient.instance());
        disableWebKitProxy();
    }

    public void setCustomProxyTypeIndex(int proxyTypeIndex) {
        userStore.setProxyTypeIndex(proxyTypeIndex);
    }

    public void setCustomProxyIP(String ip) {
        userStore.setProxyIP(ip);
    }

    public void setCustomProxyPort(int port) {
        userStore.setProxyPort(port);
    }

    public void enableCustomProxy() {
        userStore.setProxyCustomEnabled(true);
        userStore.setProxyTorEnabled(false);
        restClient.setValue(RestClient.instance());
        enableWebKitProxy();
    }

    public void disableCustomProxy() {
        userStore.setProxyCustomEnabled(false);
        restClient.setValue(RestClient.instance());
        disableWebKitProxy();
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

    public static boolean isProxyEnabled(UserStore userStore) {
        return userStore.isProxyTorEnabled() || userStore.isProxyCustomEnabled();
    }

    public static boolean isProxyProvided(UserStore userStore) {
        return userStore.getProxyIP() != null && userStore.getProxyPort() != 0;
    }

    public static Proxy.Type getProxyType(UserStore userStore) {
        if (userStore.isProxyTorEnabled()) {
            return Proxy.Type.SOCKS;
        } else if (userStore.isProxyCustomEnabled()) {
            return userStore.getProxyType();
        }
        return null;
    }

    public static String getProxyIP(UserStore userStore) {
        if (userStore.isProxyTorEnabled()) {
            return TOR_PROXY_HOST;
        }
        if (userStore.isProxyCustomEnabled()) {
            return userStore.getProxyIP();
        }
        return null;
    }

    public static int getProxyPort(UserStore userStore) {
        if (userStore.isProxyTorEnabled()) {
            return TOR_PROXY_PORT;
        } else if (userStore.isProxyCustomEnabled()) {
            return userStore.getProxyPort();
        }
        return 0;
    }

    public static String getBaseUrl(UserStore userStore) {
        return userStore.isProxyTorEnabled()
                ? BuildConfig.BASE_TOR_URL
                : BuildConfig.BASE_URL;
    }
}
