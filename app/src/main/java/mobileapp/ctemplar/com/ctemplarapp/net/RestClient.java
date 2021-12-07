package mobileapp.ctemplar.com.ctemplarapp.net;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestClient {
    private final RestService services;
    private final UserStore userStore;

    public RestClient() {
        userStore = CTemplarApp.getUserStore();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ProxyController.getBaseUrl(userStore))
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

        OkHttpClient.Builder client = OkHttpClientFactory.newClient(userStore).newBuilder()
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
