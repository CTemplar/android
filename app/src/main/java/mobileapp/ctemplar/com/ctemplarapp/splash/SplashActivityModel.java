package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestClient;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import timber.log.Timber;

public class SplashActivityModel extends ViewModel {

    private UserRepository userRepository;

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public String getToken() {
        return userRepository.getUserToken();
    }

    private final MutableLiveData<String> refreshTokenResponse = new MutableLiveData<>();



    public void refreshToken() {
        String username = userRepository.getUsername();
        String password = userRepository.getUserPassword();
        SignInRequest signInRequest = new SignInRequest(
                username, EncodeUtils.encodePassword(username, password)
        );
        userRepository.signIn(signInRequest)
                .subscribe(new Observer<SignInResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SignInResponse signInResponse) {
                        String token = signInResponse.getToken();
                        userRepository.saveUserToken(token);
                        refreshTokenResponse.postValue(token);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public MutableLiveData<String> getRefreshTokenResponse() {
        return refreshTokenResponse;
    }

}