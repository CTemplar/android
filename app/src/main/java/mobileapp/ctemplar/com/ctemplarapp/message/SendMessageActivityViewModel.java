package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.DeleteAttachmentResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.EncryptionMessage;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class SendMessageActivityViewModel extends ViewModel {

    UserRepository userRepository;
    ContactsRepository contactsRepository;
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    MutableLiveData<ResponseStatus> uploadAttachmentStatus = new MutableLiveData<>();
    MutableLiveData<MessageAttachment> uploadAttachmentResponse = new MutableLiveData<>();
    MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();
    MutableLiveData<MessagesResult> createMessageResponse = new MutableLiveData<>();
    MutableLiveData<ResponseStatus> createMessageStatus = new MutableLiveData<>();
    MutableLiveData<ContactsResponse> contactsResponse = new MutableLiveData<>();
    MutableLiveData<KeyResponse> keyResponse = new MutableLiveData<>();
    MutableLiveData<MessagesResult> messageEncryptionResult = new MutableLiveData<>();
    MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        contactsRepository = CTemplarApp.getContactsRepository();
    }

    public void createMessage(SendMessageRequest request) {
        userRepository.sendMessage(request)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult messagesResult) {
                        createMessageStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        createMessageResponse.postValue(messagesResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        createMessageStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateMessage(long id, SendMessageRequest request, ArrayList<String> receiverPublicKeys, long mailboxId) {
        String content = request.getContent();

        MailboxEntity mailboxEntity = CTemplarApp.getAppDatabase().mailboxDao().getById(mailboxId);
        PGPManager pgpManager = new PGPManager();

        if (!receiverPublicKeys.contains(null)) {
            receiverPublicKeys.add(mailboxEntity.getPublicKey());

            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);
            content = pgpManager.encryptMessage(content, publicKeys);

        } else if (request.getEncryptionMessage() != null) {
            receiverPublicKeys.add(mailboxEntity.getPublicKey());

            // GENERATION MESSAGE KEYS
            String randomSecret = request.getEncryptionMessage().getRandomSecret();
            String password = request.getEncryptionMessage().getPassword();
            PGPKeyEntity pgpKeyEntity = pgpManager.generateKeys(randomSecret, password);
            receiverPublicKeys.add(pgpKeyEntity.getPublicKey());

            receiverPublicKeys.removeAll(Collections.singleton(null));
            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);

            // ENCRYPTION
            content = pgpManager.encryptMessage(content, publicKeys);

            // ADD KEYS IN MESSAGE
            EncryptionMessage encryptionMessage = request.getEncryptionMessage();
            encryptionMessage.setPublicKey(pgpKeyEntity.getPublicKey());
            encryptionMessage.setPrivateKey(pgpKeyEntity.getPrivateKey());
            request.setEncryptionMessage(encryptionMessage);
        }
        request.setContent(content);

        userRepository.updateMessage(id, request)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult result) {
                        messagesResult.postValue(result);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void setEncryptionMessage(long id, SendMessageRequest sendMessageRequest) {
        userRepository.updateMessage(id, sendMessageRequest)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult messagesResult) {
                        messageEncryptionResult.postValue(messagesResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteMessage(long messageId) {
        userRepository.deleteMessage(messageId)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getEmailPublicKeys(PublicKeysRequest request) {
        userRepository.getEmailPublicKeys(request)
                .subscribe(new Observer<KeyResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KeyResponse keyResponse) {
                        SendMessageActivityViewModel.this.keyResponse.postValue(keyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getContacts(int limit, int offset) {
        contactsRepository.getContactsList(limit, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactsResponse response) {
                        contactsResponse.postValue(response);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_CONTACTS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void uploadAttachment(MultipartBody.Part attachment, final long message) {
        userRepository.uploadAttachment(attachment, message)
                .subscribe(new Observer<MessageAttachment>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageAttachment messageAttachment) {
                        uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        uploadAttachmentResponse.postValue(messageAttachment);
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteAttachment(long id) {
        userRepository.deleteAttachment(id)
                .subscribe(new Observer<DeleteAttachmentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeleteAttachmentResponse messageAttachment) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void mySelfData() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyselfResponse response) {
                        myselfResponse.postValue(response);
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

    public List<MailboxEntity> getMailboxes() {
        return CTemplarApp.getAppDatabase().mailboxDao().getAll();
    }

    public LiveData<MessagesResult> getMessagesResult() {
        return messagesResult;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public LiveData<ContactsResponse> getContactsResponse() {
        return contactsResponse;
    }

    public LiveData<KeyResponse> getKeyResponse() {
        return keyResponse;
    }

    public LiveData<MessagesResult> getCreateMessageResponse() {
        return  createMessageResponse;
    }

    public LiveData<ResponseStatus> getCreateMessageStatus() {
        return createMessageStatus;
    }

    public LiveData<ResponseStatus> getUploadAttachmentStatus() {
        return uploadAttachmentStatus;
    }

    public LiveData<MessageAttachment> getUploadAttachmentResponse() {
        return uploadAttachmentResponse;
    }

    public LiveData<MessagesResult> getMessageEncryptionResult() {
        return messageEncryptionResult;
    }

    public LiveData<MyselfResponse> getMySelfResponse() {
        return myselfResponse;
    }
}
