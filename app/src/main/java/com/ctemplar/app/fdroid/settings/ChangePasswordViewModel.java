package com.ctemplar.app.fdroid.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.main.MainActivityActions;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.MailboxKey;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
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

    LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void changePassword(String oldPassword, String password, boolean resetKeys) {
        String username = userRepository.getUsername();

        String oldPasswordHash = EncodeUtils.generateHash(username, oldPassword);
        String passwordHash = EncodeUtils.generateHash(username, password);

        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                oldPasswordHash, passwordHash, passwordHash, resetKeys
        );

        EncodeUtils.generateMailboxKeys(mailboxEntities, oldPassword, password, resetKeys)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap((Function<List<MailboxKey>, Observable<ResponseBody>>) mailboxKeys -> {
                    changePasswordRequest.setMailboxesKeys(mailboxKeys);
                    return userRepository.changePassword(changePasswordRequest);
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

    public void logout() {
        if (userRepository != null) {
            userRepository.clearData();
        }
        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }
}
