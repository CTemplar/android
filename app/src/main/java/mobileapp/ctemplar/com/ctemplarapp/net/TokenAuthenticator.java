package mobileapp.ctemplar.com.ctemplarapp.net;

import androidx.annotation.NonNull;

import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class TokenAuthenticator implements Authenticator {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TAG = TokenAuthenticator.class.getSimpleName();
    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) {
        // Synchronous call necessarily!
        String newToken = refreshToken();
        if (newToken == null) {
            Timber.d(TAG, "Token is null");
            return response.request();
        }

        Timber.d(TAG, "Token is refreshed: %s", newToken);
        return response.request().newBuilder()
                .header(HEADER_AUTHORIZATION, String.format("JWT %s", newToken))
                .build();
    }

    private String refreshToken() {
        Timber.d(TAG, "Refreshing token...");
        UserRepository userRepository = UserRepository.getInstance();
        String username = userRepository.getUsername();
        String password = userRepository.getUserPassword();
        SignInRequest signInRequest = new SignInRequest(
                username, EncodeUtils.generateHash(username, password)
        );
        SignInResponse signInResponse = new RestClient()
                .getRestService()
                .signIn(signInRequest)
                .blockingSingle();
        if (signInResponse == null) {
            return null;
        }
        String token = signInResponse.getToken();
        userRepository.saveUserToken(token);
        return token;
    }
}
