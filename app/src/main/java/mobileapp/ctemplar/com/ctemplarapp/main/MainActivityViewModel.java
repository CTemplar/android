package mobileapp.ctemplar.com.ctemplarapp.main;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.executor.QueuedExecutor;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.EmptyFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.ResponseMessagesData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.EmptyFolderResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.SettingsResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.cache.MessageCacheProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.workers.WorkersHelper;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivityViewModel extends AndroidViewModel {
    public static final String ANDROID = "android";
    public static final String EXIT_BROADCAST_ACTION = "ctemplar.action.exit";

    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;
    private final ManageFoldersRepository manageFoldersRepository;
    private final MutableLiveData<MainActivityActions> actions = new SingleLiveEvent<>();
    private final MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<MessageProvider> messageResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseMessagesData> messagesResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseMessagesData> searchMessagesResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> toFolderStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deleteMessagesStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> emptyFolderStatus = new MutableLiveData<>();
    private final MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseBody> unreadFoldersBody = new MutableLiveData<>();
    private final MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    private final MutableLiveData<String> currentFolder = new MutableLiveData<>();
    private final QueuedExecutor executor;

    public interface OnDecryptFinishedCallback {
        void onDecryptFinished(MessageProvider message);
    }

    public void decryptSubjects(List<MessageProvider> messages, OnDecryptFinishedCallback callback) {
        MessageCacheProvider messageCacheProvider = MessageCacheProvider.instance;
        List<MessageProvider> messagesToDecrypt = new ArrayList<>();
        for (MessageProvider message : messages) {
            if (!message.isSubjectEncrypted()) {
                continue;
            }
            if (message.getDecryptedSubject() != null) {
                continue;
            }
            String cached = messageCacheProvider.getMessageDecryptedSubject(message);
            if (cached != null) {
                message.setSubject(cached);
                message.setSubjectDecrypted(true);
                continue;
            }
            messagesToDecrypt.add(message);
        }
        if (messagesToDecrypt.isEmpty()) {
            return;
        }
        executor.execute(() -> {
            boolean keepDecryptedSubjects = true;
            for (MessageProvider message : messagesToDecrypt) {
                String decrypted = EncryptUtils.decryptSubject(message.getSubject(),
                        message.getMailboxId());
                message.setSubject(decrypted);
                message.setSubjectDecrypted(true);
                messageCacheProvider.setMessageDecryptedSubject(message);
                callback.onDecryptFinished(message);
                if (keepDecryptedSubjects) {
                    message.setDecryptedSubject(decrypted);
                    messagesRepository.updateDecryptedSubject(message.getId(), decrypted);
                }
            }
        });
    }

    private class ExitBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (EXIT_BROADCAST_ACTION.equals(action)) {
                clearUserData();
            }
        }

        public void register(Application application) {
            IntentFilter intentFilter = new IntentFilter(EXIT_BROADCAST_ACTION);
            application.registerReceiver(this, intentFilter);
        }

        public void unregister(Application application) {
            application.unregisterReceiver(this);
        }
    }

    private final ExitBroadcastReceiver exitBroadcastReceiver;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        userRepository = CTemplarApp.getUserRepository();
        messagesRepository = CTemplarApp.getMessagesRepository();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
        executor = new QueuedExecutor();
        exitBroadcastReceiver = new ExitBroadcastReceiver();
        exitBroadcastReceiver.register(application);
    }

    @Override
    protected void onCleared() {
        exitBroadcastReceiver.unregister(getApplication());
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

    public LiveData<MessageProvider> getMessageResponse() {
        return messageResponse;
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
        String token = userRepository.getFirebaseToken();
        Observable.concat(
                userRepository.deleteFirebaseToken(token),
                userRepository.signOut(ANDROID, token)
        )
                .doFinally(this::clearUserData)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> voidResponse) {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e, "logout");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("logout: onComplete");
                    }
                });
    }

    public void checkUserSession() {
        if (TextUtils.isEmpty(userRepository.getUserToken())) {
            clearUserData();
        }
    }

    public void clearUserData() {
        userRepository.clearData();
        WorkersHelper.cancelAllWork(getApplication());
        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }

    public void getMessage(long messageId, String folder) {
        userRepository.getMessage(messageId).subscribe(new Observer<MessagesResponse>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull MessagesResponse messagesResponse) {
                if (messagesResponse.getTotalCount() == 0) {
                    Timber.w("getMessage count is 0");
                    return;
                }
                MessagesResult messagesResult = messagesResponse.getMessagesList().get(0);
                MessageEntity messageEntity = MessageProvider.fromMessagesResultToEntity(
                        messagesResult, folder);
                messagesRepository.saveMessage(messageEntity);
                MessageProvider messageProvider = MessageProvider.fromMessageEntity(messageEntity,
                        false, false);
                messageResponse.postValue(messageProvider);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Timber.e(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getMessages(int limit, int offset, String folder, Date lastMessageUpdateTime) {
        if (TextUtils.isEmpty(folder)) {
            return;
        }

        boolean[] loaded = new boolean[]{false};
        Single.fromCallable(() -> {
            List<MessageEntity> localMessageEntities;
            switch (folder) {
                case MainFolderNames.STARRED:
                    localMessageEntities = messagesRepository.getStarredMessages(limit, offset);
                    break;
                case MainFolderNames.ALL_MAILS:
                    localMessageEntities = messagesRepository.getAllMailsMessages(limit, offset);
                    break;
                case MainFolderNames.UNREAD:
                    localMessageEntities = messagesRepository.getUnreadMessages(limit, offset);
                    break;
                case MainFolderNames.SENT:
                    localMessageEntities = messagesRepository.getSentMessages(limit, offset);
                    break;
                case MainFolderNames.INBOX:
                    localMessageEntities = messagesRepository.getInboxMessages(limit, offset);
                    break;
                default:
                    localMessageEntities = messagesRepository.getMessagesByFolder(folder, limit, offset);
                    break;
            }
            return MessageProvider.fromMessageEntities(localMessageEntities,
                    false, false);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new SingleObserver<List<MessageProvider>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull List<MessageProvider> messageProviders) {
                        if (loaded[0]) {
                            return;
                        }
                        ResponseMessagesData localMessagesData = new ResponseMessagesData(
                                messageProviders, offset, folder);
                        if (localMessagesData.messages.size() > 0) {
                            Timber.i("Loaded from DB: %s", System.currentTimeMillis() % 10000);
                            messagesResponse.postValue(localMessagesData);
                        }
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
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
                        List<MessageEntity> localEntities;
                        switch (folder) {
                            case MainFolderNames.STARRED:
                                localEntities = messagesRepository.updateStarred(messageEntities, lastMessageUpdateTime);
                                break;
                            case MainFolderNames.UNREAD:
                                localEntities = messagesRepository.updateUnread(messageEntities, lastMessageUpdateTime);
                                break;
                            case MainFolderNames.ALL_MAILS:
                                localEntities = messagesRepository.updateAllMails(messageEntities, lastMessageUpdateTime);
                                break;
                            case MainFolderNames.SENT:
                                localEntities = messagesRepository.updateFolder(folder, messageEntities, lastMessageUpdateTime);
                                break;
                            default:
                                localEntities = messagesRepository.updateFolder(folder, messageEntities, lastMessageUpdateTime);
                                break;
                        }
                        messageProviders = MessageProvider
                                .fromMessageEntities(localEntities, false, false);

                        loaded[0] = true;
                        Timber.i("Loaded from Network: %s", System.currentTimeMillis() % 10000);
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
                                .fromMessageEntities(messageEntities, false, false);
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MailboxesResponse mailboxesResponse) {
                        if (mailboxesResponse.getTotalCount() > 0) {
                            userRepository.saveMailboxes(mailboxesResponse.getMailboxesList());
                        }
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MAILBOXES);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SignInResponse signInResponse) {
                        userRepository.saveUserToken(signInResponse.getToken());
                    }
                });
    }

    public void deleteMessages(Long[] messageIds) {
        String messageIdsString = TextUtils.join(",", messageIds);
        userRepository.deleteMessages(messageIdsString)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> response) {
                        for (long messageId : messageIds) {
                            messagesRepository.deleteMessageById(messageId);
                        }
                        deleteMessagesStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull EmptyFolderResponse emptyFolderResponse) {
                        messagesRepository.deleteMessagesByFolderName(folder);
                        emptyFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> voidResponse) {
                        messagesRepository.updateMessageFolderName(messageId, folder);
                        toFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> messageResponse) {
                        int resultCode = messageResponse.code();
                        if (resultCode == 204) {
                            CTemplarApp.getAppDatabase().messageDao().updateIsStarred(id, starred);
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

    public void getFolders(int limit, int offset) {
        manageFoldersRepository.getFoldersList(limit, offset)
                .subscribe(new Observer<FoldersResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FoldersResponse response) {
                        foldersResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull ResponseBody responseBody) {
                        unreadFoldersBody.postValue(responseBody);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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

                        ThemeUtils.setDarkModeFromServer(
                                settingsResponse.isNightMode(),
                                userRepository.getUserStore()
                        );
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
