package com.ctemplar.app.fdroid.net;

import android.os.Build;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import com.ctemplar.app.fdroid.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

public class RestClient {
    private static final RestClient instance = new RestClient();
    private final RestService services;

    public RestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(logLevel())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GENERAL_GSON))
                .build();

        services = retrofit.create(RestService.class);
    }

    private OkHttpClient logLevel() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentInterceptor())
                .addInterceptor(new HttpTokenInterceptor())
//                .addInterceptor(httpLoggingInterceptor)
                .authenticator(new TokenAuthenticator())
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
                if (tlsSocketFactory.getTrustManager() != null) {
                    client.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager());
                }
            } catch (KeyManagementException e) {
                Timber.e(e);
            } catch (NoSuchAlgorithmException e) {
                Timber.e(e);
            } catch (KeyStoreException e) {
                Timber.e(e);
            }
        }

        return client.build();
    }

    public RestService getRestService() {
        return services;
    }

    public static RestClient instance() {
        return instance;
    }
}
