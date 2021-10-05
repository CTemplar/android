package mobileapp.ctemplar.com.ctemplarapp.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.request.TokenRefreshRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class TokenAuthenticator implements Authenticator {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_TYPE = "JWT";

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        UserRepository userRepository = CTemplarApp.getUserRepository();
        String userToken = userRepository.getUserToken();
        boolean keepMeLoggedIn = userRepository.getKeepMeLoggedIn();
        if (EditTextUtils.isNotEmpty(userToken) && !keepMeLoggedIn) {
            Timber.d("Token expiration auto-logout");
            userRepository.clearData();
            return response.request();
        }

        String newToken = refreshToken(userToken);
        if (TextUtils.isEmpty(newToken)) {
            Timber.d("Refresh token is null");
            userRepository.clearData();
            return response.request();
        }
        userRepository.saveUserToken(newToken);

        Timber.d("Token is refreshed: %s", newToken);
        return response.request().newBuilder()
                .header(HEADER_AUTHORIZATION, TOKEN_TYPE + " " + newToken)
                .build();
    }

    private String refreshToken(String userToken) throws IOException {
        Timber.d("Refreshing token...");
        retrofit2.Response<SignInResponse> refreshResponse = RestClient.instance()
                .getRestService()
                .refreshToken(new TokenRefreshRequest(userToken))
                .execute();
        SignInResponse signInResponse = refreshResponse.body();
        if (signInResponse == null) {
            return null;
        }
        return signInResponse.getToken();
    }
}
