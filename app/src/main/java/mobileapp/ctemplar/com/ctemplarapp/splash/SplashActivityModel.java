package mobileapp.ctemplar.com.ctemplarapp.splash;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFirebaseTokenRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivityModel extends ViewModel {
    private final UserRepository userRepository;

    private final MutableLiveData<AddFirebaseTokenResponse> addFirebaseTokenResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deleteFirebaseTokenStatus = new MutableLiveData<>();

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public boolean isAuthorized() {
        return userRepository.isAuthorized();
    }

    public String getUserToken() {
        return userRepository.getUserToken();
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

    public void addFirebaseToken(String token, String platform) {
        userRepository.addFirebaseToken(new AddFirebaseTokenRequest(token, platform))
                .subscribe(new Observer<AddFirebaseTokenResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull AddFirebaseTokenResponse response) {
                        Timber.d("addFirebaseToken is success %s", response.getToken());
                        userRepository.saveFirebaseToken(response.getToken());
                        addFirebaseTokenResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> voidResponse) {
                        Timber.d("deleteFirebaseToken is success %s", token);
                        deleteFirebaseTokenStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
