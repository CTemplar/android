package com.ctemplar.app.fdroid.settings.mailboxes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import com.ctemplar.app.fdroid.net.request.CheckUsernameRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DefaultMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.EnabledMailboxRequest;
import com.ctemplar.app.fdroid.net.response.CheckUsernameResponse;
import com.ctemplar.app.fdroid.net.response.domains.DomainsResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxResponse;
import com.ctemplar.app.fdroid.repository.MailboxDao;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.mapper.MailboxMapper;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class MailboxesViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MailboxDao mailboxDao;

    private final MutableLiveData<ResponseStatus> defaultMailboxResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> enabledMailboxResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<DomainsResponse> domainsResponse = new MutableLiveData<>();
    private final MutableLiveData<CheckUsernameResponse> checkUsernameResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> createMailboxResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> checkUsernameStatus = new MutableLiveData<>();

    public MailboxesViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    }

    public List<MailboxEntity> getMailboxes() {
        return mailboxDao.getAll();
    }

    public LiveData<ResponseStatus> defaultMailboxResponseStatus() {
        return defaultMailboxResponseStatus;
    }

    public LiveData<ResponseStatus> enabledMailboxResponseStatus() {
        return enabledMailboxResponseStatus;
    }

    public LiveData<ResponseStatus> createMailboxResponseStatus() {
        return createMailboxResponseStatus;
    }

    public MutableLiveData<CheckUsernameResponse> getCheckUsernameResponse() {
        return checkUsernameResponse;
    }

    public LiveData<ResponseStatus> getCheckUsernameStatus() {
        return checkUsernameStatus;
    }

    MutableLiveData<DomainsResponse> getDomainsResponse() {
        return domainsResponse;
    }

    void updateDefaultMailbox(final long lastSelectedMailboxId, final long mailboxId) {
        userRepository.updateDefaultMailbox(mailboxId, new DefaultMailboxRequest())
                .subscribe(new Observer<MailboxResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MailboxResponse mailboxResponse) {
                        mailboxDao.setDefault(lastSelectedMailboxId, false);
                        mailboxDao.setDefault(mailboxId, true);
                        defaultMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        defaultMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void updateEnabledMailbox(final long mailboxId, final boolean isEnabled) {
        userRepository.updateEnabledMailbox(mailboxId, new EnabledMailboxRequest(isEnabled))
                .subscribe(new Observer<MailboxResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MailboxResponse mailboxResponse) {
                        mailboxDao.setEnabled(mailboxId, isEnabled);
                        enabledMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        enabledMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void getDomains() {
        userRepository.getDomains()
                .subscribe(new Observer<DomainsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull DomainsResponse response) {
                        domainsResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void checkUsername(String username) {
        userRepository.checkUsername(new CheckUsernameRequest(username))
                .subscribe(new Observer<CheckUsernameResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull CheckUsernameResponse response) {
                        checkUsernameResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.w(e);
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            if (exception.code() == 429) {
                                checkUsernameStatus.postValue(ResponseStatus.RESPONSE_ERROR_TOO_MANY_REQUESTS);
                            } else {
                                checkUsernameStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                            }
                        } else {
                            checkUsernameStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void createMailbox(final String mailboxEmail) {
        final String userPassword = userRepository.getUserPassword();
        final CreateMailboxRequest request = new CreateMailboxRequest();
        request.setEmail(mailboxEmail);

        EncodeUtils.generateKeys(mailboxEmail, userPassword, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<PGPKeyEntity, Single<Response<MailboxResponse>>>) pgpKeyEntity -> {
                    request.setPrivateKey(pgpKeyEntity.getPrivateKey());
                    request.setPublicKey(pgpKeyEntity.getPublicKey());
                    request.setFingerprint(pgpKeyEntity.getFingerprint());
                    return userRepository.createMailbox(request);
                })
                .subscribe(new SingleObserver<Response<MailboxResponse>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Response<MailboxResponse> mailboxesResultResponse) {
                        if (mailboxesResultResponse.code() == 201) {
                            createMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                            final MailboxResponse mailboxResponse = mailboxesResultResponse.body();
                            userRepository.saveMailbox(MailboxMapper.map(mailboxResponse));
                        } else if (mailboxesResultResponse.code() == 400) {
                            createMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR_PAID);
                        } else {
                            createMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        createMailboxResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }
                });
    }
}