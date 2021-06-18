package com.ctemplar.app.fdroid.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.DialogState;
import com.ctemplar.app.fdroid.LoginActivityActions;
import com.ctemplar.app.fdroid.SingleLiveEvent;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import com.ctemplar.app.fdroid.net.request.RecoverPasswordRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.response.RecoverPasswordResponse;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.services.NotificationService;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import com.ctemplar.app.fdroid.workers.WorkersHelper;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class LoginActivityViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private RecoverPasswordRequest recoverPasswordRequest;

    private final MutableLiveData<LoginActivityActions> actions = new SingleLiveEvent<>();
    private final MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> addAppTokenStatus = new MutableLiveData<>();

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        userRepository = CTemplarApp.getUserRepository();
    }

    public LiveData<LoginActivityActions> getActionStatus() {
        return actions;
    }

    public void changeAction(LoginActivityActions action) {
        actions.postValue(action);
    }

    public LiveData<DialogState> getDialogState() {
        return dialogState;
    }

    public void showProgressDialog() {
        dialogState.postValue(DialogState.SHOW_PROGRESS_DIALOG);
    }

    public void hideProgressDialog() {
        dialogState.postValue(DialogState.HIDE_PROGRESS_DIALOG);
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void resetResponseStatus() {
        responseStatus.postValue(null);
    }

    public void setRecoveryPassword(String username, String email) {
        recoverPasswordRequest = new RecoverPasswordRequest(username, email);
    }

    public RecoverPasswordRequest getRecoverPasswordRequest() {
        return recoverPasswordRequest;
    }

    public void clearDB() {
        CTemplarApp.getAppDatabase().clearAllTables();
    }

    public void signIn(String username, String password, String otp, boolean keepMeLoggedIn) {
        userRepository.saveUsername(username);
        userRepository.saveUserPassword(password);
        userRepository.saveKeepMeLoggedIn(keepMeLoggedIn);
        final SignInRequest signInRequest = new SignInRequest(username, EncodeUtils.generateHash(username, password));
        signInRequest.setOtp(otp);
        signInRequest.setRememberMe(keepMeLoggedIn);

        userRepository.signIn(signInRequest)
                .subscribe(new Observer<SignInResponse>() {
                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            if (exception.code() == 400) {
                                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_AUTH_FAILED);
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

                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SignInResponse signInResponse) {
                        String token = signInResponse.getToken();
                        boolean is2FA = signInResponse.is2FAEnabled();
                        if (token == null && is2FA) {
                            responseStatus.postValue(ResponseStatus.RESPONSE_WAIT_OTP);
                        } else {
                            userRepository.saveUserToken(signInResponse.getToken());
                            NotificationService.updateState(getApplication());
                            responseStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                            WorkersHelper.setupForceRefreshTokenWork(getApplication());
                        }
                    }
                });
    }

    public void recoverPassword() {
        if (recoverPasswordRequest != null) {
            recoverPassword(recoverPasswordRequest.getUsername(), recoverPasswordRequest.getRecoveryEmail());
        }
    }

    public void recoverPassword(String username, String email) {
        userRepository.recoverPassword(new RecoverPasswordRequest(username, email))
                .subscribe(new Observer<RecoverPasswordResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull RecoverPasswordResponse recoverPasswordResponse) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_RECOVER_PASSWORD);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            if (exception.code() == 400) {
                                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_RECOVER_PASS_FAILED);
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

    public void resetPassword() {
        if (recoverPasswordRequest == null) {
            return;
        }
        String username = recoverPasswordRequest.getUsername();
        String password = recoverPasswordRequest.getPassword();
        String emailAddress = EditTextUtils.formatUserEmail(username);
        userRepository.saveUsername(username);
        userRepository.saveUserPassword(password);
        EncodeUtils.getPGPKeyObservable(emailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<PGPKeyEntity, Single<RecoverPasswordResponse>>) pgpKeyEntity -> {
                    recoverPasswordRequest.setPrivateKey(pgpKeyEntity.getPrivateKey());
                    recoverPasswordRequest.setPublicKey(pgpKeyEntity.getPublicKey());
                    recoverPasswordRequest.setPassword(
                            EncodeUtils.generateHash(username, password)
                    );

                    return userRepository.resetPassword(recoverPasswordRequest);
                })
                .subscribe(new SingleObserver<RecoverPasswordResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull RecoverPasswordResponse recoverPasswordResponse) {
                        userRepository.saveUserToken(recoverPasswordResponse.getToken());
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_NEW_PASSWORD);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            if (exception.code() == 400) {
                                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_CODE_NOT_MATCH);
                            } else {
                                responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                            }
                        } else {
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }
                });
    }
}
