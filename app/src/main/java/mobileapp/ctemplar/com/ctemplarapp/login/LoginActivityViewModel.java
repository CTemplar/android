package mobileapp.ctemplar.com.ctemplarapp.login;

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
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFirebaseTokenRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import retrofit2.HttpException;
import timber.log.Timber;

public class LoginActivityViewModel extends ViewModel {

    private UserRepository userRepository;
    private RecoverPasswordRequest recoverPasswordRequest;

    private MutableLiveData<LoginActivityActions> actions = new SingleLiveEvent<>();
    private MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> addFirebaseTokenStatus = new MutableLiveData<>();

    public LoginActivityViewModel() {
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

    public void setRecoveryPassword(String username, String email) {
        recoverPasswordRequest = new RecoverPasswordRequest(username, email);
    }

    public RecoverPasswordRequest getRecoverPasswordRequest() {
        return recoverPasswordRequest;
    }

    public LiveData<ResponseStatus> getAddFirebaseTokenStatus() {
        return addFirebaseTokenStatus;
    }

    public void clearDB() {
        CTemplarApp.getAppDatabase().clearAllTables();
    }

    public void signIn(String username, String password, String otp) {
        userRepository.saveUsername(username);
        userRepository.saveUserPassword(password);
        final SignInRequest signInRequest = new SignInRequest(username, EncodeUtils.generateHash(username, password));
        signInRequest.setOtp(otp);

        userRepository.signIn(signInRequest)
                .subscribe(new Observer<SignInResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException ) {
                            HttpException exception = (HttpException)e;
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
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SignInResponse signInResponse) {
                        String token = signInResponse.getToken();
                        boolean is2FA = signInResponse.is2FAEnabled();
                        if (token == null && is2FA) {
                            responseStatus.postValue(ResponseStatus.RESPONSE_WAIT_OTP);
                        } else {
                            userRepository.saveUserToken(signInResponse.getToken());
                            responseStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                        }
                    }
                });
    }

    public void recoverPassword() {
        if(recoverPasswordRequest != null) {
            recoverPassword(recoverPasswordRequest.getUsername(), recoverPasswordRequest.getEmail());
        }
    }

    public void recoverPassword(String username, String email) {
        userRepository.recoverPassword(new RecoverPasswordRequest(username, email))
                .subscribe(new Observer<RecoverPasswordResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecoverPasswordResponse recoverPasswordResponse) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_RECOVER_PASSWORD);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException ) {
                            HttpException exception = (HttpException)e;
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
        if(recoverPasswordRequest == null) {
            return;
        }
        EncodeUtils.getPGPKeyObservable(recoverPasswordRequest.getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<PGPKeyEntity, Observable<RecoverPasswordResponse>>) pgpKeyEntity -> {

                    recoverPasswordRequest.setPrivateKey(pgpKeyEntity.getPrivateKey());
                    recoverPasswordRequest.setPublicKey(pgpKeyEntity.getPublicKey());
                    recoverPasswordRequest.setPassword(
                            EncodeUtils.generateHash(
                                    recoverPasswordRequest.getUsername(),
                                    recoverPasswordRequest.getPassword()
                            )
                    );

                    return userRepository.resetPassword(recoverPasswordRequest);
                }).subscribe(new Observer<RecoverPasswordResponse>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RecoverPasswordResponse recoverPasswordResponse) {
                userRepository.saveUserToken(recoverPasswordResponse.getToken());
                responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_NEW_PASSWORD);
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException ) {
                    HttpException exception = (HttpException)e;
                    if (exception.code() == 400) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_CODE_NOT_MATCH);
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

    public void addFirebaseToken(String token, String platform) {
        userRepository.addFirebaseToken(new AddFirebaseTokenRequest(token, platform))
                .subscribe(new Observer<AddFirebaseTokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddFirebaseTokenResponse response) {
                        userRepository.saveFirebaseToken(response.getToken());
                        addFirebaseTokenStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                    }

                    @Override
                    public void onError(Throwable e) {
                        addFirebaseTokenStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
