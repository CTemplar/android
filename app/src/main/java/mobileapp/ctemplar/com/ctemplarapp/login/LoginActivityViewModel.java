package mobileapp.ctemplar.com.ctemplarapp.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import retrofit2.HttpException;

public class LoginActivityViewModel extends ViewModel {

    UserRepository userRepository;
    MutableLiveData<LoginActivityActions> actions = new SingleLiveEvent<>();
    MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    RecoverPasswordRequest recoverPasswordRequest;

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

    public void signIn(String username, String password) {
        userRepository.signIn(new SignInRequest(username, EncodeUtils.encodePassword(username, password)))
                .subscribe(new Observer<SignInResponse>() {

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException ) {
                            HttpException exception = (HttpException)e;
                            switch (exception.code()) {
                                case 400:
                                    responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_AUTH_FAILED);
                                    break;
                                default:
                                    responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                                    break;
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
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                        userRepository.saveUserToken(signInResponse.getToken());
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
                            switch (exception.code()) {
                                case 400:
                                    responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_RECOVER_PASS_FAILED);
                                    break;
                                default:
                                    responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                                    break;
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
                .flatMap(new Function<PGPKeyEntity, Observable<RecoverPasswordResponse>>() {
                    @Override
                    public Observable<RecoverPasswordResponse> apply(PGPKeyEntity pgpKeyEntity) throws Exception {

                        recoverPasswordRequest.setPrivateKey(pgpKeyEntity.getPrivateKey());
                        recoverPasswordRequest.setPublicKey(pgpKeyEntity.getPublicKey());
                        recoverPasswordRequest.setPassword(EncodeUtils.encodePassword(recoverPasswordRequest.getUsername(), recoverPasswordRequest.getPassword()));

                        return userRepository.resetPassword(recoverPasswordRequest);
                    }
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
                    switch (exception.code()) {
                        case 400:
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR_CODE_NOT_MATCH);
                            break;
                        default:
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                            break;
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

    public void setRecoveryPassword(String username, String email) {
        recoverPasswordRequest = new RecoverPasswordRequest(username, email);
    }

    public RecoverPasswordRequest getRecoverPasswordRequest() {
        return recoverPasswordRequest;
    }
}
