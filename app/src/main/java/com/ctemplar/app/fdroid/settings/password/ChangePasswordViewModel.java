package com.ctemplar.app.fdroid.settings.password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.main.MainActivityActions;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.MailboxExtraKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.MailboxKeyRequest;
import com.ctemplar.app.fdroid.net.response.HttpErrorResponse;
import com.ctemplar.app.fdroid.repository.MailboxDao;
import com.ctemplar.app.fdroid.repository.MailboxKeyDao;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import com.ctemplar.app.fdroid.utils.EncodeUtils;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;
import static com.ctemplar.app.fdroid.utils.RxUtils.callAsyncWithResult;

public class ChangePasswordViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MailboxDao mailboxDao;
    private final MailboxKeyDao mailboxKeyDao;
    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<String> changePasswordResponseError = new MutableLiveData<>();
    private final MutableLiveData<MainActivityActions> actions = new MutableLiveData<>();

    public ChangePasswordViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
        mailboxKeyDao = CTemplarApp.getAppDatabase().mailboxKeyDao();
    }

    LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public MutableLiveData<String> getChangePasswordResponseError() {
        return changePasswordResponseError;
    }

    public String getUserPassword() {
        return userRepository.getUserPassword();
    }

    public void changePassword(String oldPassword, String password, boolean resetKeys) {
        String username = userRepository.getUsername();
        String currentPassword = userRepository.getUserPassword();
        String oldPasswordHash = EncodeUtils.generateHash(username, oldPassword);
        String passwordHash = EncodeUtils.generateHash(username, password);

        List<MailboxEntity> mailboxEntities = mailboxDao.getAll();

        callAsyncWithResult(
                () -> {
                    List<MailboxKeyRequest> mailboxKeyRequestList = EncodeUtils
                            .regenerateMailboxKeys(mailboxEntities, currentPassword, password, resetKeys);
                    List<MailboxExtraKeyRequest> mailboxExtraKeyRequestList = new ArrayList<>();
                    for (MailboxEntity mailboxEntity : mailboxEntities) {
                        List<MailboxKeyEntity> keyEntities = mailboxKeyDao.getByMailboxId(mailboxEntity.getId());
                        if (keyEntities == null || keyEntities.isEmpty()) {
                            continue;
                        }
                        List<MailboxExtraKeyRequest> extraKeyRequestList = EncodeUtils
                                .regenerateMailboxExtraKeys(mailboxEntity, keyEntities, currentPassword, password, resetKeys);
                        mailboxExtraKeyRequestList.addAll(extraKeyRequestList);
                    }
                    return new ChangePasswordRequest(
                            oldPasswordHash, passwordHash, passwordHash, resetKeys,
                            mailboxKeyRequestList, mailboxExtraKeyRequestList
                    );
                },
                (changePasswordRequest) -> {
                    userRepository.changePassword(changePasswordRequest).subscribe(new SingleObserver<ResponseBody>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NotNull ResponseBody responseBody) {
                            responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Timber.e(e, "Change password request error occurs");
                            Response<?> errorResponse = ((HttpException) e).response();
                            if (errorResponse != null && errorResponse.errorBody() != null) {
                                try {
                                    String errorBody = errorResponse.errorBody().string();
                                    HttpErrorResponse httpErrorResponse = GENERAL_GSON
                                            .fromJson(errorBody, HttpErrorResponse.class);
                                    changePasswordResponseError.postValue(httpErrorResponse
                                            .getError().getError());
                                } catch (IOException | JsonSyntaxException ex) {
                                    Timber.e(ex, "Can't parse changePassword error");
                                }
                            }
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    });
                },
                (e) -> {
                    Timber.e(e, "Change password error occurs");
                    responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                },
                Schedulers.computation()
        );
    }

    public void logout() {
        if (userRepository != null) {
            userRepository.clearData();
        }
        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }
}
