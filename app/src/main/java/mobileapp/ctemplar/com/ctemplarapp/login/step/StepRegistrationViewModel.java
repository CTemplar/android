package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

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
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;

public class StepRegistrationViewModel extends ViewModel {

    UserRepository userRepository = CTemplarApp.getUserRepository();

    SignUpRequest signUpRequest = new SignUpRequest();
    MutableLiveData<StepRegistrationActions> actions = new SingleLiveEvent<>();
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();

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
        EncodeUtils.getPGPKeyObservable(signUpRequest.getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<PGPKeyEntity, Observable<SignUpResponse>>() {
                    @Override
                    public Observable<SignUpResponse> apply(PGPKeyEntity pgpKeyEntity) throws Exception {
                        generatePGPKeys();
                        hashPassword();
                        return userRepository.signUp(signUpRequest);
                    }
                }).subscribe(new Observer<SignUpResponse>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(SignUpResponse signUpResponse) {
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

    public void generatePGPKeys() {
        PGPManager pgpManager = new PGPManager();
        PGPKeyEntity entity = pgpManager.generateKeys(signUpRequest.getUsername(), signUpRequest.getPassword());
        signUpRequest.setPrivateKey(entity.getPrivateKey());
        signUpRequest.setPublicKey(entity.getPublicKey());
        signUpRequest.setFingerprint(entity.getFingerprint());
    }

    public void hashPassword() {
        signUpRequest.setPasswordHashed(EncodeUtils.encodePassword(signUpRequest.getUsername(), signUpRequest.getPassword()));
    }

}
