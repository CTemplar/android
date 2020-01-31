package mobileapp.ctemplar.com.ctemplarapp.net;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RestClient {

    private static final RestClient instance = new RestClient();
    private final RestService services;

    public static RestClient instance() {
        return instance;
    }

    public RestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(logLevel())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        services = retrofit.create(RestService.class);
    }

    public RestService getRestService() {
        return services;
    }

    private OkHttpClient logLevel() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        try {
            TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
            if (tlsSocketFactory.getTrustManager() != null) {
                client.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager())
                        .build();
            }
        } catch (KeyManagementException e) {
            Timber.e(e);
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
        } catch (KeyStoreException e) {
            Timber.e(e);
        }
        client.addInterceptor(interceptor)
                .addInterceptor(new HttpTokenInterceptor())
                .authenticator(new TokenAuthenticator())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        return client.build();
    }
}
