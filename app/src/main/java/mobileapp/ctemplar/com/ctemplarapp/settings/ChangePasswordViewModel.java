package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ChangePasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import okhttp3.ResponseBody;

public class ChangePasswordViewModel extends ViewModel {

    private UserRepository userRepository;
    private MailboxEntity currentMailbox;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<MainActivityActions> actions = new MutableLiveData<>();

    public ChangePasswordViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
    }

    public void changePassword(String oldPassword, String password, String confirmPassword) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();

        String userName = userRepository.getUsername();

        changePasswordRequest.setOld_password(EncodeUtils.encodePassword(userName, oldPassword));
        changePasswordRequest.setPassword(EncodeUtils.encodePassword(userName, password));
        changePasswordRequest.setConfirm_password(EncodeUtils.encodePassword(userName, confirmPassword));

        changePasswordRequest.setPrivate_key(currentMailbox.privateKey);
        changePasswordRequest.setPublic_key(currentMailbox.publicKey);
        changePasswordRequest.setFingerprint(currentMailbox.fingerprint);

        userRepository.changePassword(changePasswordRequest)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void logout() {
        if (userRepository != null) {
            userRepository.logout();
        }

        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }

    public LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }
}
