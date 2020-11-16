package com.ctemplar.app.fdroid.login.step;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.SingleLiveEvent;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import com.ctemplar.app.fdroid.net.request.CaptchaVerifyRequest;
import com.ctemplar.app.fdroid.net.request.CheckUsernameRequest;
import com.ctemplar.app.fdroid.net.request.SignUpRequest;
import com.ctemplar.app.fdroid.net.response.CaptchaResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaVerifyResponse;
import com.ctemplar.app.fdroid.net.response.CheckUsernameResponse;
import com.ctemplar.app.fdroid.net.response.HttpErrorResponse;
import com.ctemplar.app.fdroid.net.response.SignUpResponse;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class StepRegistrationViewModel extends ViewModel {
    private final UserRepository userRepository = CTemplarApp.getUserRepository();

    private final SignUpRequest signUpRequest = new SignUpRequest();
    private final MutableLiveData<StepRegistrationActions> actions = new SingleLiveEvent<>();
    private final MutableLiveData<ResponseStatus> responseStatus = new SingleLiveEvent<>();
    private final MutableLiveData<String> responseError = new SingleLiveEvent<>();
    private final MutableLiveData<CaptchaResponse> captchaResponse = new MutableLiveData<>();
    private final MutableLiveData<CaptchaVerifyResponse> captchaVerifyResponse = new MutableLiveData<>();

    public String getUsername() {
        return signUpRequest.getUsername();
    }

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

    public LiveData<String> getResponseError() {
        return responseError;
    }

    public void setPassword(String password) {
        signUpRequest.setPassword(password);
    }

    public void setRecoveryEmail(String email) {
        signUpRequest.setRecoveryEmail(email);
    }

    public void setInviteCode(String inviteCode) {
        signUpRequest.setInviteCode(inviteCode);
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

    public void checkUsername(String username) {
        checkUsername(username, false);
    }

    public void checkUsername(String username, boolean nextStep) {
        userRepository.checkUsername(new CheckUsernameRequest(username))
                .subscribe(new Observer<CheckUsernameResponse>() {

                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull CheckUsernameResponse checkUsernameResponse) {
                        if (checkUsernameResponse.isExists()) {
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_USERNAME_EXISTS);
                        } else {
                            signUpRequest.setUsername(username);
                            if (nextStep) {
                                responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_STEP_USERNAME);
                            } else {
                                responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                            }
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            if (exception.code() == 429) {
                                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_TOO_MANY_REQUESTS);
                            } else {
                                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                            }
                        } else {
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void signUp() {
        String emailAddress = EditTextUtils.formatUserEmail(signUpRequest.getUsername());
        String password = signUpRequest.getPassword();
        EncodeUtils.getPGPKeyObservable(emailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<PGPKeyEntity, Observable<SignUpResponse>>) pgpKeyEntity -> {
                    generatePGPKeys(pgpKeyEntity);
                    hashPassword();
                    return userRepository.signUp(signUpRequest);
                }).subscribe(new Observer<SignUpResponse>() {

            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull SignUpResponse signUpResponse) {
                userRepository.saveUsername(signUpRequest.getUsername());
                userRepository.saveUserToken(signUpResponse.getToken());
                userRepository.saveUserPassword(signUpRequest.getPassword());
                responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_STEP_EMAIL);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                if (e instanceof HttpException) {
                    Response<?> errorResponse = ((HttpException) e).response();
                    if (errorResponse != null && errorResponse.errorBody() != null) {
                        try {
                            String errorBody = errorResponse.errorBody().string();
                            HttpErrorResponse httpErrorResponse = new Gson()
                                    .fromJson(errorBody, HttpErrorResponse.class);
                            responseError.postValue(httpErrorResponse.getError().getError());
                        } catch (IOException ex) {
                            Timber.e(ex, "Can't parse signUp error");
                        }
                    }
                } else {
                    responseError.postValue("Uncaught error");
                }
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull CaptchaResponse response) {
                        captchaResponse.postValue(response);
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

    public void captchaVerify(String key, String value) {
        userRepository.captchaVerify(new CaptchaVerifyRequest(key, value))
                .subscribe(new Observer<CaptchaVerifyResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull CaptchaVerifyResponse response) {
                        captchaVerifyResponse.postValue(response);
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

    private void generatePGPKeys(PGPKeyEntity pgpKeyEntity) {
        signUpRequest.setPrivateKey(pgpKeyEntity.getPrivateKey());
        signUpRequest.setPublicKey(pgpKeyEntity.getPublicKey());
        signUpRequest.setFingerprint(pgpKeyEntity.getFingerprint());
    }

    private void hashPassword() {
        signUpRequest.setPasswordHashed(
                EncodeUtils.generateHash(signUpRequest.getUsername(), signUpRequest.getPassword()));
    }
}
