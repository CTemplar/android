package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageAsReadRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import retrofit2.Response;
import timber.log.Timber;

public class ViewMessagesViewModel extends ViewModel {

    private UserRepository userRepository;
    private MessagesRepository messagesRepository;
    private ManageFoldersRepository manageFoldersRepository;

    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<List<MessageProvider>> messagesResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> starredResponse = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> moveToFolderStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> addWhitelistStatus = new MutableLiveData<>();

    public ViewMessagesViewModel() {
        userRepository = UserRepository.getInstance();
        messagesRepository = MessagesRepository.getInstance();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    MailboxEntity getMailboxById(long mailboxId) {
        return CTemplarApp.getAppDatabase().mailboxDao().getById(mailboxId);
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
            final List<MessageEntity> childrenEntities = messagesRepository.getChildMessages(parentMessage);
            List<MessageEntity> allEntities = new ArrayList<>(childrenEntities.size() + 1);
            allEntities.add(parentMessage);
            allEntities.addAll(childrenEntities);

            List<MessageProvider> messageProviders = MessageProvider.fromMessageEntities(allEntities);
            messagesResponse.postValue(messageProviders);
        }

        userRepository.getChainMessages(id)
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResponse messagesResponse) {
                        List<MessagesResult> messagesResults = messagesResponse.getMessagesList();
                        if (messagesResults == null || messagesResults.isEmpty()) {
                            ViewMessagesViewModel.this.messagesResponse.postValue(null);
                            return;
                        }
                        MessagesResult parentMessageResult = messagesResults.get(0);
                        MessageEntity parentLocalMessage = messagesRepository.getLocalMessage(id);
                        MessageEntity parentEntity = MessageProvider.fromMessagesResultToEntity(
                                parentMessageResult, parentLocalMessage.getRequestFolder()
                        );
                        MessageProvider parentMessage = MessageProvider.fromMessageEntity(parentEntity);

                        MessagesResult[] childrenResult = parentMessageResult.getChildren();
                        List<MessageEntity> childrenEntities = MessageProvider.fromMessagesResultsToEntities(Arrays.asList(childrenResult));
                        List<MessageProvider> childrenMessages = MessageProvider.fromMessageEntities(childrenEntities);

                        messagesRepository.deleteMessagesByParentId(parentEntity.getId());
                        messagesRepository.addMessageToDatabase(parentEntity);
                        messagesRepository.addMessagesToDatabase(childrenEntities);

                        List<MessageProvider> resultList = new ArrayList<>(1 + childrenResult.length);
                        resultList.add(parentMessage);
                        resultList.addAll(childrenMessages);
                        ViewMessagesViewModel.this.messagesResponse.postValue(resultList);
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
                        e.printStackTrace();
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
