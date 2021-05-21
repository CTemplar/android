package mobileapp.ctemplar.com.ctemplarapp.net;

import java.util.concurrent.TimeUnit;

import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

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

        return client.build();
    }

    public RestService getRestService() {
        return services;
    }

    public static RestClient instance() {
        return instance;
    }
}
