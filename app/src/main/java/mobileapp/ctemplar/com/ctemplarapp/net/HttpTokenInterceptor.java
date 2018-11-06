package mobileapp.ctemplar.com.ctemplarapp.net;

import android.text.TextUtils;

import java.io.IOException;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTokenInterceptor implements Interceptor {

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.header(HEADER_ACCEPT, "application/json");

        // TODO add user token when method will be implemented
        String token = CTemplarApp.getUserRepository().getUserToken();
        setAuthHeader(builder, token);

        request = builder.build();
        Response response = chain.proceed(request);

        return response;
    }

    private void setAuthHeader(Request.Builder builder, String token) {
        if(!TextUtils.isEmpty(token))
            builder.header(HEADER_AUTHORIZATION, String.format("JWT %s", token));
    }
}
