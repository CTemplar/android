package mobileapp.ctemplar.com.ctemplarapp.net;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import timber.log.Timber;

public class OkHttpClientFactory {
    public static OkHttpClient newClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new UserAgentInterceptor());

        UserStore userStore = CTemplarApp.getUserStore();
        if (ProxyController.isProxyEnabled(userStore)) {
            String proxyIP = ProxyController.getProxyIP(userStore);
            int proxyPort = ProxyController.getProxyPort(userStore);
            if (proxyIP != null && proxyPort != 0) {
                InetSocketAddress inetSocketAddress;
                try {
                    inetSocketAddress = new InetSocketAddress(proxyIP, proxyPort);
                } catch (Exception e) {
                    inetSocketAddress = null;
                    ToastUtils.showToast(CTemplarApp.getInstance(), R.string.fail_proxy_init);
                    Timber.e(e, "OkHttpClientFactory");
                }
                if (inetSocketAddress != null) {
                    builder.proxy(new Proxy(Proxy.Type.HTTP, inetSocketAddress));
                }
            } else {
                ToastUtils.showToast(CTemplarApp.getInstance(), R.string.fill_proxy_fields);
            }
        }
        return builder.build();
    }
}
