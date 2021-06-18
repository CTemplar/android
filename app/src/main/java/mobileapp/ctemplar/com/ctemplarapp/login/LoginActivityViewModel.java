package mobileapp.ctemplar.com.ctemplarapp.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFirebaseTokenRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import mobileapp.ctemplar.com.ctemplarapp.workers.WorkersHelper;
import retrofit2.HttpException;
import timber.log.Timber;

public class LoginActivityViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private RecoverPasswordRequest recoverPasswordRequest;

    private final MutableLiveData<LoginActivityActions> actions = new SingleLiveEvent<>();
    private final MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> addFirebaseTokenStatus = new MutableLiveData<>();

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

    public LiveData<ResponseStatus> getAddFirebaseTokenStatus() {
        return addFirebaseTokenStatus;
    }

    public void clearAllTables() {
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
                            responseStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                            userAuthorized();
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

    private void userAuthorized() {
        clearAllTables();
        WorkersHelper.setupForceRefreshTokenWork(getApplication());
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token
                -> addFirebaseToken(token, MainActivityViewModel.ANDROID));
    }

    public void addFirebaseToken(String token, String platform) {
        userRepository.addFirebaseToken(new AddFirebaseTokenRequest(token, platform))
                .subscribe(new Observer<AddFirebaseTokenResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull AddFirebaseTokenResponse response) {
                        userRepository.saveFirebaseToken(response.getToken());
                        addFirebaseTokenStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        addFirebaseTokenStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
