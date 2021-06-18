package com.ctemplar.app.fdroid.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.main.MainActivityActions;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.MailboxKey;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import okhttp3.ResponseBody;

public class ChangePasswordViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final List<MailboxEntity> mailboxEntities;
    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<MainActivityActions> actions = new MutableLiveData<>();

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

    public String getUserPassword() {
        return userRepository.getUserPassword();
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
                .flatMap((Function<List<MailboxKey>, Single<ResponseBody>>) mailboxKeys -> {
                    changePasswordRequest.setMailboxesKeys(mailboxKeys);
                    return userRepository.changePassword(changePasswordRequest);
                })
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull ResponseBody responseBody) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
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
