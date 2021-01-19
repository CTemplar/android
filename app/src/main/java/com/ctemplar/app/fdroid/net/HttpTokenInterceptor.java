package com.ctemplar.app.fdroid.net;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTokenInterceptor implements Interceptor {
    private static final String HEADER_ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_TYPE = "JWT";

    @NotNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.header(HEADER_ACCEPT, APPLICATION_JSON);

        String token = CTemplarApp.getUserRepository().getUserToken();
        setAuthHeader(builder, token);

        request = builder.build();
        return chain.proceed(request);
    }

    private void setAuthHeader(Request.Builder builder, String token) {
        if (EditTextUtils.isNotEmpty(token)) {
            builder.header(HEADER_AUTHORIZATION, TOKEN_TYPE + " " + token);
        }
    }
}
