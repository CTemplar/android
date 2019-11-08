package mobileapp.ctemplar.com.ctemplarapp.login.step;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CaptchaVerifyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaVerifyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import timber.log.Timber;

public class StepRegistrationViewModel extends ViewModel {

    private UserRepository userRepository = CTemplarApp.getUserRepository();

    private SignUpRequest signUpRequest = new SignUpRequest();
    private MutableLiveData<StepRegistrationActions> actions = new SingleLiveEvent<>();
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<CaptchaResponse> captchaResponse = new MutableLiveData<>();
    private MutableLiveData<CaptchaVerifyResponse> captchaVerifyResponse = new MutableLiveData<>();

    public void changeAction(StepRegistrationActions action) {
        actions.postValue(action);
    }

    public LiveData<StepRegistrationActions> getAction() {
        return actions;
    }

    public void changeResponseStatus(ResponseStatus status) {
        responseStatus.postValue(status);
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void setPassword(String password) {
        signUpRequest.setPassword(password);
    }

    public void setRecoveryEmail(String email) {
        signUpRequest.setRecoveryEmail(email);
    }

    public void setCaptcha(String captchaKey, String captchaValue) {
        signUpRequest.setCaptchaKey(captchaKey);
        signUpRequest.setCaptchaValue(captchaValue);
    }

    public LiveData<CaptchaResponse> getCaptchaResponse() {
        return captchaResponse;
    }

    public LiveData<CaptchaVerifyResponse> getCaptchaVerifyResponse() {
        return captchaVerifyResponse;
    }

    public void checkUsername(final String username) {
        userRepository.checkUsername(new CheckUsernameRequest(username))
                .subscribe(new Observer<CheckUsernameResponse>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CheckUsernameResponse checkUsernameResponse) {
                        if(checkUsernameResponse.isExists()) {
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_USERNAME_EXISTS);
                        } else {
                            signUpRequest.setUsername(username);
                            responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_STEP_USERNAME);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void signUp() {
        if(signUpRequest == null) {
            return;
        }
        String password = signUpRequest.getPassword();
        EncodeUtils.getPGPKeyObservable(password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<PGPKeyEntity, Observable<SignUpResponse>>) pgpKeyEntity -> {
                    generatePGPKeys(pgpKeyEntity);
                    hashPassword();
                    return userRepository.signUp(signUpRequest);
                }).subscribe(new Observer<SignUpResponse>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(SignUpResponse signUpResponse) {
                userRepository.saveUsername(signUpRequest.getUsername());
                userRepository.saveUserToken(signUpResponse.getToken());
                userRepository.saveUserPassword(signUpRequest.getPassword());
                responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_STEP_EMAIL);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getCaptcha() {
        userRepository.getCaptcha()
                .subscribe(new Observer<CaptchaResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CaptchaResponse response) {
                        captchaResponse.postValue(response);
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

    public void captchaVerify(String key, String value) {
        userRepository.captchaVerify(new CaptchaVerifyRequest(key, value))
                .subscribe(new Observer<CaptchaVerifyResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CaptchaVerifyResponse response) {
                        captchaVerifyResponse.postValue(response);
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

    private void generatePGPKeys(PGPKeyEntity pgpKeyEntity) {
        signUpRequest.setPrivateKey(pgpKeyEntity.getPrivateKey());
        signUpRequest.setPublicKey(pgpKeyEntity.getPublicKey());
        signUpRequest.setFingerprint(pgpKeyEntity.getFingerprint());
    }

    private void hashPassword() {
        signUpRequest.setPasswordHashed(EncodeUtils.generateHash(signUpRequest.getUsername(), signUpRequest.getPassword()));
    }

}
