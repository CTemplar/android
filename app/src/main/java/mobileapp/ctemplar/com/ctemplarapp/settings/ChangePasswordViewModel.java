package mobileapp.ctemplar.com.ctemplarapp.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
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

    LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    void changePassword(String oldPassword, String password, boolean resetKeys) {
        String username = userRepository.getUsername();

        String oldPasswordHash = EncodeUtils.generateHash(username, oldPassword);
        String passwordHash = EncodeUtils.generateHash(username, password);

        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                oldPasswordHash, passwordHash, passwordHash, resetKeys
        );

        EncodeUtils.generateMailboxKeys(username, oldPassword, password, resetKeys, mailboxEntities)
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

    void logout() {
        if (userRepository != null) {
            userRepository.logout();
        }
        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }
}
