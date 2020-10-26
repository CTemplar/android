package com.ctemplar.app.fdroid.main;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.DialogState;
import com.ctemplar.app.fdroid.SingleLiveEvent;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.EmptyFolderRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.response.Folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.Mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.net.response.Messages.EmptyFolderResponse;
import com.ctemplar.app.fdroid.net.response.Messages.MessagesResponse;
import com.ctemplar.app.fdroid.net.response.Messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.Myself.MyselfResponse;
import com.ctemplar.app.fdroid.net.response.Myself.MyselfResult;
import com.ctemplar.app.fdroid.net.response.Myself.SettingsResponse;
import com.ctemplar.app.fdroid.net.response.ResponseMessagesData;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.MessagesRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.services.NotificationService;
import com.ctemplar.app.fdroid.services.NotificationServiceListener;
import com.ctemplar.app.fdroid.utils.EncodeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivityViewModel extends AndroidViewModel {
    public static final String ANDROID = "android";

    private UserRepository userRepository;
    private MessagesRepository messagesRepository;
    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<MainActivityActions> actions = new SingleLiveEvent<>();
    private MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseMessagesData> messagesResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseMessagesData> searchMessagesResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> toFolderStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteMessagesStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> emptyFolderStatus = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> unreadFoldersBody = new MutableLiveData<>();
    private MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    private MutableLiveData<String> currentFolder = new MutableLiveData<>();

    private final NotificationServiceListener notificationServiceListener = message
            -> getMessages(10, 0, currentFolder.getValue());

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        userRepository = CTemplarApp.getUserRepository();
        messagesRepository = CTemplarApp.getMessagesRepository();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
        NotificationService.bind(application, notificationServiceListener);
    }

    @Override
    protected void onCleared() {
        NotificationService.unbind(getApplication(), notificationServiceListener);
    }

    public LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public void changeAction(MainActivityActions action) {
        actions.postValue(action);
    }

    public LiveData<DialogState> getDialogStatus() {
        return dialogState;
    }

    public void showProgressDialog() {
        dialogState.postValue(DialogState.SHOW_PROGRESS_DIALOG);
    }

    public void hideProgressDialog() {
        dialogState.postValue(DialogState.HIDE_PROGRESS_DIALOG);
    }

    public LiveData<ResponseStatus> getDeleteMessagesStatus() {
        return deleteMessagesStatus;
    }

    public LiveData<ResponseStatus> getEmptyFolderStatus() {
        return emptyFolderStatus;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public LiveData<ResponseStatus> getToFolderStatus() {
        return toFolderStatus;
    }

    MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    MutableLiveData<ResponseBody> getUnreadFoldersBody() {
        return unreadFoldersBody;
    }

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder.postValue(currentFolder);
    }

    public LiveData<String> getCurrentFolder() {
        return currentFolder;
    }

    public LiveData<ResponseMessagesData> getMessagesResponse() {
        return messagesResponse;
    }

    public LiveData<ResponseMessagesData> getSearchMessagesResponse() {
        return searchMessagesResponse;
    }

    public MutableLiveData<MyselfResponse> getMyselfResponse() {
        return myselfResponse;
    }

    public void logout() {
        if (userRepository == null) {
            return;
        }
        userRepository.signOut(ANDROID, "")
                .doFinally(this::clearUserData)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "logout");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("logout: onComplete");
                    }
                });
        clearUserData();
        NotificationService.updateState(getApplication());
    }

    public void checkUserSession() {
        if (TextUtils.isEmpty(userRepository.getUserToken())) {
            clearUserData();
        }
    }

    public void clearUserData() {
        userRepository.clearData();
        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }

    public void getMessages(int limit, int offset, String folder) {
        if (TextUtils.isEmpty(folder)) {
            return;
        }
        List<MessageEntity> localMessageEntities;
        switch (folder) {
            case MainFolderNames.STARRED:
                localMessageEntities = messagesRepository.getStarredMessages();
                break;
            case MainFolderNames.ALL_MAILS:
                localMessageEntities = messagesRepository.getAllMailsMessages();
                break;
            case MainFolderNames.UNREAD:
                localMessageEntities = messagesRepository.getUnreadMessages();
                break;
            default:
                localMessageEntities = messagesRepository.getMessagesByFolder(folder);
                break;
        }

        Single.fromCallable(() -> MessageProvider.fromMessageEntities(localMessageEntities, false))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new SingleObserver<List<MessageProvider>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull List<MessageProvider> messageProviders) {
                        if (offset == 0) {
                            ResponseMessagesData localMessagesData = new ResponseMessagesData(
                                    messageProviders, offset, folder);
                            if (localMessagesData.messages.size() > 0) {
                                messagesResponse.postValue(localMessagesData);
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Timber.e(e);
                    }
                });

        Observable<MessagesResponse> messagesResponseObservable;
        if (MainFolderNames.STARRED.equals(folder)) {
            messagesResponseObservable = userRepository.getStarredMessagesList(limit, offset);
        } else {
            messagesResponseObservable = userRepository.getMessagesList(limit, offset, folder);
        }
        messagesResponseObservable
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MessagesResponse response) {
                        List<MessagesResult> messages = response.getMessagesList();
                        List<MessageEntity> messageEntities
                                = MessageProvider.fromMessagesResultsToEntities(messages, folder);

                        List<MessageProvider> messageProviders;
                        if (offset == 0) {
                            List<MessageEntity> localEntities;
                            switch (folder) {
                                case MainFolderNames.STARRED:
                                    messagesRepository.deleteStarred();
                                    messagesRepository.saveAllMessagesWithIgnore(messageEntities);
                                    localEntities = messagesRepository.getStarredMessages();
                                    break;
                                case MainFolderNames.UNREAD:
                                    messagesRepository.deleteUnread();
                                    messagesRepository.saveAllMessagesWithIgnore(messageEntities);
                                    localEntities = messagesRepository.getUnreadMessages();
                                    break;
                                case MainFolderNames.ALL_MAILS:
                                    messagesRepository.deleteAllMails();
                                    messagesRepository.saveAllMessagesWithIgnore(messageEntities);
                                    localEntities = messagesRepository.getAllMailsMessages();
                                    break;
                                default:
                                    messagesRepository.deleteMessagesByFolderName(folder);
                                    messagesRepository.saveAllMessages(messageEntities);
                                    localEntities = messagesRepository.getMessagesByFolder(folder);
                                    break;
                            }
                            messageProviders = MessageProvider
                                    .fromMessageEntities(localEntities, false);
                        } else {
                            messageProviders = MessageProvider
                                    .fromMessageEntities(messageEntities, false);
                        }

                        messagesResponse.postValue(new ResponseMessagesData(messageProviders,
                                offset, folder));
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void searchMessages(String query, int limit, int offset) {
        if (TextUtils.isEmpty(query)) {
            return;
        }
        userRepository.searchMessages(query, limit, offset)
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MessagesResponse response) {
                        List<MessagesResult> messages = response.getMessagesList();
                        List<MessageEntity> messageEntities = MessageProvider
                                .fromMessagesResultsToEntities(messages);
                        List<MessageProvider> messagesProvider = MessageProvider
                                .fromMessageEntities(messageEntities, false);
                        searchMessagesResponse.postValue(new ResponseMessagesData(
                                messagesProvider, offset));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        searchMessagesResponse.postValue(new ResponseMessagesData(
                                Collections.emptyList(), offset));
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMailboxes(int limit, int offset) {
        userRepository.getMailboxesList(limit, offset)
                .subscribe(new Observer<MailboxesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MailboxesResponse mailboxesResponse) {
                        if (mailboxesResponse.getTotalCount() > 0) {
                            userRepository.saveMailboxes(mailboxesResponse.getMailboxesList());
                        }
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MAILBOXES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).code();
                            switch (code) {
                                case 401:
                                case 403:
                                    signIn();
                                    break;
                            }
                        }
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void signIn() {
        String username = userRepository.getUsername();
        String password = userRepository.getUserPassword();
        SignInRequest signInRequest = new SignInRequest(
                username,
                EncodeUtils.generateHash(username, password)
        );

        userRepository.signIn(signInRequest)
                .subscribe(new Observer<SignInResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            switch (exception.code()) {
                                case 400:
                                    logout();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SignInResponse signInResponse) {
                        userRepository.saveUserToken(signInResponse.getToken());
                    }
                });
    }

    public void deleteMessages(Long[] messageIds) {
        String messageIdsString = TextUtils.join(",", messageIds);
        userRepository.deleteMessages(messageIdsString)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> response) {
                        for (long messageId : messageIds) {
                            messagesRepository.deleteMessageById(messageId);
                        }
                        deleteMessagesStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteMessagesStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void emptyFolder(String folder) {
        userRepository.emptyFolder(new EmptyFolderRequest(folder))
                .subscribe(new Observer<EmptyFolderResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EmptyFolderResponse emptyFolderResponse) {
                        messagesRepository.deleteMessagesByFolderName(folder);
                        emptyFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        emptyFolderStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void toFolder(final long messageId, final String folder) {
        userRepository.toFolder(messageId, folder)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        messagesRepository.updateMessageFolderName(messageId, folder);
                        toFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        toFolderStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void markMessageIsStarred(final long id, final boolean starred) {
        userRepository.markMessageIsStarred(id, starred)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> messageResponse) {
                        int resultCode = messageResponse.code();
                        if (resultCode == 204) {
                            CTemplarApp.getAppDatabase().messageDao().updateIsStarred(id, starred);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getFolders(int limit, int offset) {
        manageFoldersRepository.getFoldersList(limit, offset)
                .subscribe(new Observer<FoldersResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FoldersResponse response) {
                        foldersResponse.postValue(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getUnreadFoldersList() {
        manageFoldersRepository.getUnreadFoldersList()
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        unreadFoldersBody.postValue(responseBody);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getUserMyselfInfo() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MyselfResponse myselfResponse) {
                        if (myselfResponse != null) {
                            MyselfResult myselfResult = myselfResponse.getResult()[0];
                            SettingsResponse settingsResponse = myselfResult.getSettings();

                            String timezone = settingsResponse.getTimezone();
                            boolean isContactsEncrypted = settingsResponse.isContactsEncrypted();
                            boolean isDisableLoadingImages = settingsResponse.isDisableLoadingImages();
                            boolean isReportBugsEnabled = settingsResponse.isEnableReportBugs();

                            userRepository.saveTimeZone(timezone);
                            userRepository.setContactsEncryptionEnabled(isContactsEncrypted);
                            userRepository.setBlockExternalImagesEnabled(isDisableLoadingImages);
                            userRepository.setReportBugsEnabled(isReportBugsEnabled);
                        }
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
}
