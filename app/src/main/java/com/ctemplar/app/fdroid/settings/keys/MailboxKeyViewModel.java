package com.ctemplar.app.fdroid.settings.keys;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DeleteMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.UpdateMailboxPrimaryKeyRequest;
import com.ctemplar.app.fdroid.net.response.HttpErrorResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeyResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeysResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.repository.AppDatabase;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import com.ctemplar.app.fdroid.repository.enums.KeyType;
import com.ctemplar.app.fdroid.repository.mapper.GeneralizedMailboxKeyMapper;
import com.ctemplar.app.fdroid.repository.mapper.MailboxKeyMapper;
import com.ctemplar.app.fdroid.repository.mapper.MailboxMapper;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import retrofit2.Response;
import timber.log.Timber;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;
import static com.ctemplar.app.fdroid.utils.RxUtils.callAsync;
import static com.ctemplar.app.fdroid.utils.RxUtils.callAsyncWithResult;

public class MailboxKeyViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final AppDatabase appDatabase;

    private final MutableLiveData<ResponseStatus> mailboxesResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> mailboxKeysResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> mailboxPrimaryResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deleteMailboxKeyResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<String> deleteMailboxErrorResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> addMailboxKeyResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<String> addMailboxKeyErrorResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> importMailboxKeyResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<String> importMailboxKeyErrorResponse = new MutableLiveData<>();

    public MailboxKeyViewModel() {
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

    public MutableLiveData<ResponseStatus> getDeleteMailboxKeyResponseStatus() {
        return deleteMailboxKeyResponseStatus;
    }

    public MutableLiveData<String> getDeleteMailboxErrorResponse() {
        return deleteMailboxErrorResponse;
    }

    public MutableLiveData<ResponseStatus> getAddMailboxKeyResponseStatus() {
        return addMailboxKeyResponseStatus;
    }

    public MutableLiveData<String> getAddMailboxKeyErrorResponse() {
        return addMailboxKeyErrorResponse;
    }

    public MutableLiveData<ResponseStatus> getImportMailboxKeyResponseStatus() {
        return importMailboxKeyResponseStatus;
    }

    public MutableLiveData<String> getImportMailboxKeyErrorResponse() {
        return importMailboxKeyErrorResponse;
    }

    public Map<MailboxEntity, List<GeneralizedMailboxKey>> getMailboxKeyMap() {
        List<MailboxEntity> mailboxes = getAllMailboxes();
        if (mailboxes == null) {
            return new HashMap<>();
        }
        Map<MailboxEntity, List<GeneralizedMailboxKey>> result = new LinkedHashMap<>();
        List<MailboxKeyEntity> keys = getMailboxKeys();
        for (MailboxEntity mailbox : mailboxes) {
            List<GeneralizedMailboxKey> keyList = new ArrayList<>();
            keyList.add(GeneralizedMailboxKeyMapper.map(mailbox));
            for (MailboxKeyEntity key : keys) {
                if (key.getMailbox() == mailbox.getId()) {
                    keyList.add(GeneralizedMailboxKeyMapper.map(key));
                }
            }
            result.put(mailbox, keyList);
        }
        return result;
    }

    public void getMailboxes(int limit, int offset) {
        callAsync(
                () -> updateMailboxesSync(limit, offset, false),
                () -> mailboxesResponseStatus.postValue(ResponseStatus.RESPONSE_NEXT),
                (e) -> {
                    Timber.e(e);
                    mailboxesResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                }
        );
    }

    public void getMailboxKeys(int limit, int offset) {
        callAsync(
                () -> updateMailboxKeysSync(limit, offset),
                () -> mailboxKeysResponseStatus.postValue(ResponseStatus.RESPONSE_NEXT),
                (e) -> {
                    Timber.e(e);
                    mailboxKeysResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                }
        );
    }

    public void createMailboxKey(long mailboxId, String address, KeyType keyType, String password) {
        final String username = userRepository.getUsername();

        EncodeUtils.generateKeys(address, password, KeyType.ECC.equals(keyType))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<PGPKeyEntity, Single<Response<MailboxKeyResponse>>>) pgpKeyEntity -> {
                    CreateMailboxKeyRequest request = new CreateMailboxKeyRequest(
                            pgpKeyEntity.getPrivateKey(),
                            pgpKeyEntity.getPublicKey(),
                            pgpKeyEntity.getFingerprint(),
                            EncodeUtils.generateHash(username, password),
                            keyType, mailboxId
                    );
                    return userRepository.createMailboxKey(request);
                })
                .subscribe(new SingleObserver<Response<MailboxKeyResponse>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Response<MailboxKeyResponse> response) {
                        if (response.isSuccessful()) {
                            addMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                            userRepository.saveMailboxKey(MailboxKeyMapper.map(response.body()));
                            return;
                        }

                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                HttpErrorResponse httpErrorResponse = GENERAL_GSON.fromJson(
                                        errorBody, HttpErrorResponse.class);
                                addMailboxKeyErrorResponse.postValue(httpErrorResponse.getError()
                                        .getError());
                            } catch (IOException | JsonSyntaxException e) {
                                Timber.e(e, "Can't parse error");
                            }
                        }
                        addMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        addMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }
                });
    }

    public void importMailboxKey(long mailboxId, String privateKey, KeyType keyType, String oldPassword, String password) {
        final String username = userRepository.getUsername();
    }


    public void updateMailboxPrimaryKey(long mailboxId, long mailboxKeyId) {
        callAsync(
                () -> updateMailboxPrimaryKeySync(mailboxId, mailboxKeyId),
                () -> mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE),
                e -> {
                    Timber.e(e);
                    mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                }
        );
    }

    public void deleteMailboxKey(long id, String password) {
        final String username = userRepository.getUsername();
        callAsyncWithResult(
                () -> deleteMailboxKeySync(id, EncodeUtils.generateHash(username, password)),
                (Response<Void> voidResponse) -> {
                    if (!voidResponse.isSuccessful()) {
                        if (voidResponse.errorBody() != null) {
                            try {
                                String errorBody = voidResponse.errorBody().string();
                                HttpErrorResponse httpErrorResponse = GENERAL_GSON.fromJson(
                                        errorBody, HttpErrorResponse.class);
                                deleteMailboxErrorResponse.postValue(
                                        httpErrorResponse.getError().getError());
                            } catch (IOException | JsonSyntaxException e) {
                                Timber.e(e, "Can't parse error");
                            }
                        }
                        deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        return;
                    }
                    deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                },
                e -> {
                    Timber.e(e);
                    deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                }
        );
    }


    private void updateMailboxesSync(int limit, int offset) {
        updateMailboxesSync(limit, offset, true);
    }

    private void updateMailboxesSync(int limit, int offset, boolean updateMailboxKeys) {
        MailboxesResponse mailboxesResponse = userRepository.getMailboxes(limit, offset).blockingFirst();
        userRepository.saveMailboxes(MailboxMapper.map(mailboxesResponse.getMailboxesList()));
        if (updateMailboxKeys) {
            updateMailboxKeysSync(20, 0);
        }
    }

    private MailboxKeysResponse updateMailboxKeysSync(int limit, int offset) {
        MailboxKeysResponse mailboxKeysResponse = userRepository.getMailboxKeys(limit, offset).blockingGet();
        userRepository.saveMailboxKeys(MailboxKeyMapper.map(mailboxKeysResponse.getResults()));
        return mailboxKeysResponse;
    }


    public Response<Void> deleteMailboxKeySync(long id, String passwordHash) {
        Response<Void> voidResponse = userRepository.deleteMailboxKey(id,
                new DeleteMailboxKeyRequest(passwordHash)).blockingGet();
        if (!voidResponse.isSuccessful()) {
            return voidResponse;
        }
        try {
            updateMailboxesSync(20, 0, true);
        } catch (Throwable e) {
            Timber.e(e);
        }
        return voidResponse;
    }


    @SuppressLint("CheckResult")
    private void updateMailboxPrimaryKeySync(long mailboxId, long mailboxKeyId) {
        //noinspection ResultOfMethodCallIgnored
        userRepository.updateMailboxPrimaryKey(new UpdateMailboxPrimaryKeyRequest(
                mailboxId, mailboxKeyId)).blockingGet();
        updateMailboxesSync(20, 0);
    }
}
