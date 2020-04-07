package com.ctemplar.app.fdroid.splash;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.AddAppTokenRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.response.AddAppTokenResponse;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivityModel extends ViewModel {

    private UserRepository userRepository;

    private MutableLiveData<AddAppTokenResponse> addAppTokenResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteAppTokenStatus = new MutableLiveData<>();

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public String getToken() {
        return userRepository.getUserToken();
    }

    public void saveAppToken(String token) {
        userRepository.saveAppToken(token);
    }

    public String getAppToken() {
        return userRepository.getAppToken();
    }

    public MutableLiveData<AddAppTokenResponse> getAddAppTokenResponse() {
        return addAppTokenResponse;
    }

    public MutableLiveData<ResponseStatus> getDeleteAppTokenStatus() {
        return deleteAppTokenStatus;
    }

    public void addAppToken(String token, String platform) {
        userRepository.addAppToken(new AddAppTokenRequest(token, platform))
                .subscribe(new Observer<AddAppTokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddAppTokenResponse response) {
                        addAppTokenResponse.postValue(response);
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

    public void deleteAppToken(String token) {
        userRepository.deleteAppToken(token)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        deleteAppTokenStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
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
