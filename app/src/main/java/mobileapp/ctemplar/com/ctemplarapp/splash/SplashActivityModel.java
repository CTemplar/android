package mobileapp.ctemplar.com.ctemplarapp.splash;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFirebaseTokenRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivityModel extends ViewModel {

    private UserRepository userRepository;

    private MutableLiveData<String> refreshTokenResponse = new MutableLiveData<>();
    private MutableLiveData<AddFirebaseTokenResponse> addFirebaseTokenResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteFirebaseTokenStatus = new MutableLiveData<>();

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public String getToken() {
        return userRepository.getUserToken();
    }

    public MutableLiveData<String> getRefreshTokenResponse() {
        return refreshTokenResponse;
    }

    public void saveFirebaseToken(String token) {
        userRepository.saveFirebaseToken(token);
    }

    public String getFirebaseToken() {
        return userRepository.getFirebaseToken();
    }

    public MutableLiveData<AddFirebaseTokenResponse> getAddFirebaseTokenResponse() {
        return addFirebaseTokenResponse;
    }

    public MutableLiveData<ResponseStatus> getDeleteFirebaseTokenStatus() {
        return deleteFirebaseTokenStatus;
    }

    public void refreshToken() {
        String username = userRepository.getUsername();
        String password = userRepository.getUserPassword();
        SignInRequest signInRequest = new SignInRequest(
                username, EncodeUtils.generateHash(username, password)
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

    public void addFirebaseToken(String token, String platform) {
        userRepository.addFirebaseToken(new AddFirebaseTokenRequest(token, platform))
                .subscribe(new Observer<AddFirebaseTokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddFirebaseTokenResponse response) {
                        addFirebaseTokenResponse.postValue(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteFirebaseToken(String token) {
        userRepository.deleteFirebaseToken(token)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        deleteFirebaseTokenStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
