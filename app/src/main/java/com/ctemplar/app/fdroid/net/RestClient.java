package com.ctemplar.app.fdroid.net;

import com.ctemplar.app.fdroid.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

public class RestClient {
    private final RestService services;

    public RestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GENERAL_GSON))
                .build();

        services = retrofit.create(RestService.class);
    }

    private OkHttpClient okHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder client = OkHttpClientFactory.newClient().newBuilder()
                .addInterceptor(new HttpTokenInterceptor())
                .authenticator(new TokenAuthenticator())
                .addInterceptor(httpLoggingInterceptor);

        return client.build();
    }

    public RestService getRestService() {
        return services;
    }

    public static RestClient instance() {
        return new RestClient();
    }
}
