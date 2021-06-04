package com.ctemplar.app.fdroid.settings.keys;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DeleteMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.UpdateMailboxPrimaryKeyRequest;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeyResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeysResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.repository.AppDatabase;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import retrofit2.Response;
import timber.log.Timber;

public class MailboxViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final AppDatabase appDatabase;

    private final MutableLiveData<ResponseStatus> mailboxesResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> mailboxKeysResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> mailboxPrimaryResponseStatus = new MutableLiveData<>();

    public MailboxViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        appDatabase = CTemplarApp.getAppDatabase();
    }

    public List<MailboxEntity> getAllMailboxes() {
        return appDatabase.mailboxDao().getAll();
    }

    public List<MailboxKeyEntity> getMailboxKeys() {
        return appDatabase.mailboxKeyDao().getAll();
    }

    public MutableLiveData<ResponseStatus> getMailboxesResponseStatus() {
        return mailboxesResponseStatus;
    }

    public MutableLiveData<ResponseStatus> getMailboxKeysResponseStatus() {
        return mailboxKeysResponseStatus;
    }

    public MutableLiveData<ResponseStatus> getMailboxPrimaryResponseStatus() {
        return mailboxPrimaryResponseStatus;
    }

    public Map<MailboxEntity, List<GeneralizedMailboxKey>> getMailboxKeyMap() {
        List<MailboxEntity> mailboxes = getAllMailboxes();
        if (mailboxes == null) {
            return new HashMap<>();
        }
        Map<MailboxEntity, List<GeneralizedMailboxKey>> result = new HashMap<>();
        List<MailboxKeyEntity> keys = getMailboxKeys();
        for (MailboxEntity mailbox : mailboxes) {
            List<GeneralizedMailboxKey> keyList = new ArrayList<>();
            keyList.add(new GeneralizedMailboxKey(
                    -1,
                    mailbox.getPrivateKey(),
                    mailbox.getPublicKey(),
                    mailbox.getFingerprint(),
                    mailbox.getKeyType()
            ));
            for (MailboxKeyEntity key : keys) {
                if (key.getMailbox() == mailbox.getId()) {
                    keyList.add(new GeneralizedMailboxKey(
                            key.getId(),
                            key.getPrivateKey(),
                            key.getPublicKey(),
                            key.getFingerprint(),
                            key.getKeyType())
                    );
                }
            }
            result.put(mailbox, keyList);
        }
        return result;
    }

    public void getMailboxes(int limit, int offset) {
        userRepository.getMailboxes(limit, offset)
                .subscribe(new Observer<MailboxesResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MailboxesResponse mailboxesResponse) {
                        if (mailboxesResponse.getTotalCount() > 0) {
                            userRepository.saveMailboxes(mailboxesResponse.getMailboxesList());
                        }
                        mailboxesResponseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MAILBOXES);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        mailboxesResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMailboxKeys(int limit, int offset) {
        userRepository.getMailboxKeys(limit, offset)
                .subscribe(new SingleObserver<MailboxKeysResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull MailboxKeysResponse response) {
                        if (response.getTotalCount() > 0) {
                            userRepository.saveMailboxKeys(response.getResults());
                        }
                        mailboxKeysResponseStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        mailboxKeysResponseStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                    }
                });
    }

    public void createMailboxKey(CreateMailboxKeyRequest request) {
        userRepository.createMailboxKey(request)
                .subscribe(new SingleObserver<Response<MailboxKeyResponse>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Response<MailboxKeyResponse> mailboxKeyResponseResponse) {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void deleteMailboxKey(long id, DeleteMailboxKeyRequest request) {
        userRepository.deleteMailboxKey(id, request)
                .subscribe(new SingleObserver<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Response<Void> voidResponse) {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void updateMailboxPrimaryKey(UpdateMailboxPrimaryKeyRequest request) {
        userRepository.updateMailboxPrimaryKey(request)
                .subscribe(new SingleObserver<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Response<Void> voidResponse) {
                        mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }
                });
    }
}
