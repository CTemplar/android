package mobileapp.ctemplar.com.ctemplarapp.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.net.request.TokenRefreshRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class TokenAuthenticator implements Authenticator {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_TYPE = "JWT";

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) {
        String newToken = refreshToken();
        if (newToken == null) {
            Timber.d("Token is null");
            return response.request();
        }

        Timber.d("Token is refreshed: %s", newToken);
        return response.request().newBuilder()
                .header(HEADER_AUTHORIZATION, TOKEN_TYPE + " " + newToken)
                .build();
    }

    private String refreshToken() {
        Timber.d("Refreshing token...");
        UserRepository userRepository = UserRepository.getInstance();
        String oldToken = userRepository.getUserToken();
        SignInResponse signInResponse = new RestClient()
                .getRestService()
                .refreshToken(new TokenRefreshRequest(oldToken))
                .blockingSingle();
        if (signInResponse == null) {
            return null;
        }
        String token = signInResponse.getToken();
        userRepository.saveUserToken(token);
        return token;
    }
}
