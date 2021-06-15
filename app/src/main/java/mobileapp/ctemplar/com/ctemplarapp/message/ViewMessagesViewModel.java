package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.messages.MarkMessageAsReadRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import retrofit2.Response;
import timber.log.Timber;

public class ViewMessagesViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;
    private final ManageFoldersRepository manageFoldersRepository;

    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<List<MessageProvider>> messagesResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> starredResponse = new MutableLiveData<>();
    private final MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> moveToFolderStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> addWhitelistStatus = new MutableLiveData<>();

    public ViewMessagesViewModel() {
        userRepository = UserRepository.getInstance();
        messagesRepository = MessagesRepository.getInstance();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    MailboxEntity getMailboxById(long mailboxId) {
        return CTemplarApp.getAppDatabase().mailboxDao().getById(mailboxId);
    }

    MailboxEntity getDefaultMailbox() {
        return CTemplarApp.getAppDatabase().mailboxDao().getDefault();
    }

    String getUserPassword() {
        return userRepository.getUserPassword();
    }

    public MutableLiveData<ResponseStatus> getMoveToFolderStatus() {
        return moveToFolderStatus;
    }

    public MutableLiveData<List<MessageProvider>> getMessagesResponse() {
        return messagesResponse;
    }

    public MutableLiveData<Boolean> getStarredResponse() {
        return starredResponse;
    }

    public MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    public MutableLiveData<ResponseStatus> getAddWhitelistStatus() {
        return addWhitelistStatus;
    }

    public void getChainMessages(long id) {
        final MessageEntity parentMessage = messagesRepository.getLocalMessage(id);
        if (parentMessage != null) {
            final List<MessageEntity> childrenEntities = messagesRepository.getChildMessages(parentMessage.getId());
            List<MessageEntity> allEntities = new ArrayList<>(childrenEntities.size() + 1);
            allEntities.add(parentMessage);
            allEntities.addAll(childrenEntities);

            Single.fromCallable(() -> MessageProvider.fromMessageEntities(allEntities, true, true))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(new SingleObserver<List<MessageProvider>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull List<MessageProvider> messageProviders) {
                            messagesResponse.postValue(messageProviders);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Timber.e(e);
                        }
                    });
        }

        userRepository.getChainMessages(id)
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MessagesResponse messagesResponse) {
                        List<MessagesResult> messagesResults = messagesResponse.getMessagesList();
                        if (messagesResults == null || messagesResults.isEmpty()) {
                            ViewMessagesViewModel.this.messagesResponse.postValue(null);
                            return;
                        }
                        MessagesResult parentMessageResult = messagesResults.get(0);
                        MessageEntity parentEntity = MessageProvider
                                .fromMessagesResultToEntity(parentMessageResult, null);
                        MessageProvider parentMessage = MessageProvider
                                .fromMessageEntity(parentEntity, true, true);

                        List<MessagesResult> childrenResultList = parentMessageResult.getChildrenAsList();
                        List<MessageEntity> childrenEntities = MessageProvider
                                .fromMessagesResultsToEntities(childrenResultList);
                        List<MessageProvider> childrenMessages = MessageProvider
                                .fromMessageEntities(childrenEntities, true, false);

                        messagesRepository.deleteMessagesByParentId(parentEntity.getId());
//                        messagesRepository.addMessageToDatabase(parentEntity);
                        messagesRepository.saveAllMessages(childrenEntities);

                        List<MessageProvider> resultList = new ArrayList<>(1 + childrenResultList.size());
                        resultList.add(parentMessage);
                        resultList.addAll(childrenMessages);
                        ViewMessagesViewModel.this.messagesResponse.postValue(resultList);
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

    public void markMessageIsStarred(long id, final boolean isStarred) {
        userRepository.markMessageIsStarred(id, isStarred)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> messageResponse) {
                        int resultCode = messageResponse.code();
                        if (resultCode == 204) {
                            messagesRepository.markMessageIsStarred(id, isStarred);
                            starredResponse.postValue(isStarred);
                        } else {
                            Timber.e("Update starred response is not success: code = %s", resultCode);
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

    public void markMessageAsRead(long id, boolean isRead) {
        MarkMessageAsReadRequest request = new MarkMessageAsReadRequest(isRead);
        userRepository.markMessageAsRead(id, request)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> messageResponse) {
                        int resultCode = messageResponse.code();
                        if (resultCode == 204) {
                            messagesRepository.markMessageAsRead(id, isRead);
                        } else {
                            Timber.e("Update isRead response is not success: code = %s", resultCode);
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
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void moveToFolder(final long messageId, final String folder) {
        userRepository.toFolder(messageId, folder)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        messagesRepository.updateMessageFolderName(messageId, folder);
                        moveToFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Move message");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void addWhitelistContact(String name, String email) {
        userRepository.addWhitelistContact(new WhiteListContact(name, email))
                .subscribe(new Observer<WhiteListContact>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WhiteListContact whiteListContact) {
                        addWhitelistStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
