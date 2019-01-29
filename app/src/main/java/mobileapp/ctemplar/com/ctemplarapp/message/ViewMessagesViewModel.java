package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.main.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class ViewMessagesViewModel extends ViewModel {
    private UserRepository userRepository;
    private MessagesRepository messagesRepository;
    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<List<MessageProvider>> messagesResponse = new MutableLiveData<>();
    private MutableLiveData<MessageProvider> starredResponse = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> moveToFolderStatus = new MutableLiveData<>();

    public ViewMessagesViewModel() {
        userRepository = UserRepository.getInstance();
        messagesRepository = MessagesRepository.getInstance();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
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
                        MessageEntity parentEntity = MessageProvider.fromMessagesResultToEntity(parentMessageResult);
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

    public void markMessageIsStarred(long id, boolean starred) {
        userRepository.markMessageIsStarred(id, starred)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult messagesResult) {
                        MessageProvider messageProvider = MessageProvider.fromMessagesResult(messagesResult);
                        starredResponse.postValue(messageProvider);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void markMessageAsRead(long id) {
        userRepository.markMessageAsRead(id)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult messagesResult) {
                        Timber.i("Message marked as read");
                    }

                    @Override
                    public void onError(Throwable e) {

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

    public void moveToFolder(long messageId, String folder) {
        userRepository.toFolder(messageId, folder)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
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

    MutableLiveData<ResponseStatus> getMoveToFolderStatus() {
        return moveToFolderStatus;
    }

    MutableLiveData<List<MessageProvider>> getMessagesResponse() {
        return messagesResponse;
    }

    MutableLiveData<MessageProvider> getStarredResponse() {
        return starredResponse;
    }

    MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }
}
