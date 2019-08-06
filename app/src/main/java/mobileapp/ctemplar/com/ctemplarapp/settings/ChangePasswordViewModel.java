package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ChangePasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MailboxKey;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import okhttp3.ResponseBody;

public class ChangePasswordViewModel extends ViewModel {

    private UserRepository userRepository;
    private List<MailboxEntity> mailboxEntities;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<MainActivityActions> actions = new MutableLiveData<>();

    public ChangePasswordViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        mailboxEntities = CTemplarApp.getAppDatabase().mailboxDao().getAll();
    }

    void changePassword(String oldPassword, String password, boolean resetKeys) {
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        String userName = userRepository.getUsername();

        changePasswordRequest.setOldPassword(EncodeUtils.encodePassword(userName, oldPassword));
        changePasswordRequest.setPassword(EncodeUtils.encodePassword(userName, password));
        changePasswordRequest.setConfirmPassword(EncodeUtils.encodePassword(userName, password));
        changePasswordRequest.setDeleteData(resetKeys);

        EncodeUtils.generateMailboxKeys(userName, oldPassword, password, resetKeys, mailboxEntities)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<MailboxKey>, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> apply(List<MailboxKey> mailboxKeys) {
                        changePasswordRequest.setMailboxesKeys(mailboxKeys);
                        return userRepository.changePassword(changePasswordRequest);
                    }
                }).subscribe(new Observer<ResponseBody>() {
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

    void logout() {
        if (userRepository != null) {
            userRepository.logout();
        }

        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }

    LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }
}
