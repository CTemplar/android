package mobileapp.ctemplar.com.ctemplarapp.net;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {
    private static final String USER_AGENT = "User-Agent";

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Request requestWithUserAgent = request.newBuilder()
                .header(USER_AGENT, BuildConfig.USER_AGENT)
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}
