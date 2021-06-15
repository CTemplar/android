package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import androidx.lifecycle.LiveData;
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

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.CreateMailboxKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.DeleteMailboxKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.UpdateMailboxPrimaryKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.HttpErrorResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxKeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxKeysResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.AppDatabase;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;
import mobileapp.ctemplar.com.ctemplarapp.repository.mapper.GeneralizedMailboxKeyMapper;
import mobileapp.ctemplar.com.ctemplarapp.repository.mapper.MailboxKeyMapper;
import mobileapp.ctemplar.com.ctemplarapp.repository.mapper.MailboxMapper;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import retrofit2.Response;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

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

    public MailboxKeyViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        appDatabase = CTemplarApp.getAppDatabase();
    }

    public String getUsername() {
        return userRepository.getUsername();
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

    public LiveData<ResponseStatus> deleteMailboxKeyResponseStatus() {
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
        userRepository.getMailboxes(limit, offset)
                .subscribe(new Observer<MailboxesResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MailboxesResponse mailboxesResponse) {
                        if (mailboxesResponse.getTotalCount() > 0) {
                            userRepository.saveMailboxes(
                                    MailboxMapper.map(mailboxesResponse.getMailboxesList()));
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
                            userRepository.saveMailboxKeys(MailboxKeyMapper.map(response.getResults()));
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
                            final MailboxKeyResponse mailboxKey = response.body();
                            userRepository.saveMailboxKey(MailboxKeyMapper.map(mailboxKey));
                            return;
                        }

                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                HttpErrorResponse httpErrorResponse = GENERAL_GSON
                                        .fromJson(errorBody, HttpErrorResponse.class);
                                addMailboxKeyErrorResponse.postValue(
                                        httpErrorResponse.getError().getError());
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

    public void deleteMailboxKey(long id, String passwordHash) {
        List<Disposable> disposables = new ArrayList<>();
        disposables.add(userRepository.deleteMailboxKey(id, new DeleteMailboxKeyRequest(passwordHash))
                .subscribe(voidResponse -> {
                    if (!voidResponse.isSuccessful()) {
                        if (voidResponse.errorBody() != null) {
                            try {
                                String errorBody = voidResponse.errorBody().string();
                                HttpErrorResponse httpErrorResponse = GENERAL_GSON
                                        .fromJson(errorBody, HttpErrorResponse.class);
                                deleteMailboxErrorResponse.postValue(
                                        httpErrorResponse.getError().getError());
                            } catch (IOException | JsonSyntaxException e) {
                                Timber.e(e, "Can't parse error");
                            }
                        }
                        deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        return;
                    }

                    disposables.add(userRepository.getMailboxes(20, 0)
                            .subscribe(mailboxesResponse -> {
                                userRepository.saveMailboxes(MailboxMapper.map(mailboxesResponse.getMailboxesList()));
                                disposables.add(userRepository.getMailboxKeys(20, 0)
                                        .subscribe(response -> {
                                            userRepository.saveMailboxKeys(MailboxKeyMapper.map(response.getResults()));
                                            deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                                            for (Disposable disposable : disposables) {
                                                disposable.dispose();
                                            }
                                        }, e -> {
                                            Timber.e(e);
                                            deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                                            for (Disposable disposable : disposables) {
                                                disposable.dispose();
                                            }
                                        }));
                            }, e -> {
                                Timber.e(e);
                                deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                                for (Disposable disposable : disposables) {
                                    disposable.dispose();
                                }
                            }));
                }, e -> {
                    Timber.e(e);
                    deleteMailboxKeyResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    for (Disposable disposable : disposables) {
                        disposable.dispose();
                    }
                }));
    }

    public void updateMailboxPrimaryKey(long mailboxId, long mailboxKeyId) {
        List<Disposable> disposables = new ArrayList<>();
        disposables.add(userRepository.updateMailboxPrimaryKey(new UpdateMailboxPrimaryKeyRequest(mailboxId, mailboxKeyId))
                .subscribe(voidResponse -> {
                    disposables.add(userRepository.getMailboxes(20, 0)
                            .subscribe(mailboxesResponse -> {
                                userRepository.saveMailboxes(MailboxMapper.map(mailboxesResponse.getMailboxesList()));
                                disposables.add(userRepository.getMailboxKeys(20, 0)
                                        .subscribe(response -> {
                                            userRepository.saveMailboxKeys(MailboxKeyMapper.map(response.getResults()));
                                            mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                                            for (Disposable disposable : disposables) {
                                                disposable.dispose();
                                            }
                                        }, e -> {
                                            Timber.e(e);
                                            mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                                            for (Disposable disposable : disposables) {
                                                disposable.dispose();
                                            }
                                        }));
                            }, e -> {
                                Timber.e(e);
                                mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                                for (Disposable disposable : disposables) {
                                    disposable.dispose();
                                }
                            }));
                }, e -> {
                    Timber.e(e);
                    mailboxPrimaryResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    for (Disposable disposable : disposables) {
                        disposable.dispose();
                    }
                }));
    }
}
