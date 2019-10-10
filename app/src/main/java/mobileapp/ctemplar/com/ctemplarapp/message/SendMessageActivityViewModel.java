package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class SendMessageActivityViewModel extends ViewModel {

    private UserRepository userRepository;
    private MessagesRepository messagesRepository;
    private ContactsRepository contactsRepository;

    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> uploadAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<MessageAttachment> uploadAttachmentResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> updateAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> createMessageResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> createMessageStatus = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsResponse = new MutableLiveData<>();
    private MutableLiveData<KeyResponse> keyResponse = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> messageEncryptionResult = new MutableLiveData<>();
    private MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    private MutableLiveData<MessageEntity> openMessageResponse = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        messagesRepository = MessagesRepository.getInstance();
        contactsRepository = CTemplarApp.getContactsRepository();
    }

    public List<MailboxEntity> getMailboxes() {
        return CTemplarApp.getAppDatabase().mailboxDao().getAll();
    }

    public MailboxEntity getMailboxById(long id) {
        return CTemplarApp.getAppDatabase().mailboxDao().getById(id);
    }

    public String getUserPassword() {
        return userRepository.getUserPassword();
    }

    public boolean getAttachmentsEncryptionEnabled() {
        return userRepository.getAttachmentsEncryptionEnabled();
    }

    public LiveData<MessagesResult> getMessagesResult() {
        return messagesResult;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public LiveData<List<Contact>> getContactsResponse() {
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

    public LiveData<ResponseStatus> getUpdateAttachmentStatus() {
        return updateAttachmentStatus;
    }

    public LiveData<MessageAttachment> getUploadAttachmentResponse() {
        return uploadAttachmentResponse;
    }

    public LiveData<ResponseStatus> getDeleteAttachmentStatus() {
        return deleteAttachmentStatus;
    }

    public LiveData<MessagesResult> getMessageEncryptionResult() {
        return messageEncryptionResult;
    }

    public LiveData<MyselfResponse> getMySelfResponse() {
        return myselfResponse;
    }

    public LiveData<MessageEntity> getOpenMessageResponse() {
        return openMessageResponse;
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

    public void openMessage(long id) {
        MessageEntity messageEntity = messagesRepository.getLocalMessage(id);
        openMessageResponse.postValue(messageEntity);
    }

    public void updateMessage(long id, SendMessageRequest request, List<String> receiverPublicKeys) {
        String content = request.getContent();
        String subject = request.getSubject();
        boolean isSubjectEncrypted = request.isSubjectEncrypted();

        if (!receiverPublicKeys.isEmpty()) {
            PGPManager pgpManager = new PGPManager();
            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);
            content = pgpManager.encryptMessage(content, publicKeys);
            if (isSubjectEncrypted && !subject.isEmpty()) {
                subject = pgpManager.encryptMessage(subject, publicKeys);
            }
            request.setContent(content);
            request.setSubject(subject);
        }
        request.setIsEncrypted(!receiverPublicKeys.isEmpty());

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
                        Timber.e(e);
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
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getContacts(int limit, int offset) {
        List<ContactEntity> contactEntities = contactsRepository.getLocalContacts();
        List<Contact> contactList = Contact.fromEntities(contactEntities);
        if (contactList.isEmpty()) {
            contactsResponse.postValue(null);
        } else {
            contactsResponse.postValue(contactList);
            return;
        }

        contactsRepository.getContactsList(limit, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ContactsResponse response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ContactData[] contacts = response.getResults();
                                ContactData[] decryptedContacts = Contact.decryptContactData(contacts);

                                contactsRepository.saveContacts(decryptedContacts);
                                List<Contact> contactList = Contact.fromResponseResults(decryptedContacts);

                                contactsResponse.postValue(contactList);
                            }
                        }).start();
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

    public void uploadAttachment(MultipartBody.Part attachment, final long message, boolean isEncrypted) {
        userRepository.uploadAttachment(attachment, message, isEncrypted)
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
                        if(e instanceof HttpException) {
                            if (((HttpException)e).code() == 413) {
                                uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR_TOO_LARGE);
                            } else {
                                uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                            }
                        } else {
                            uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateAttachment(long id, MultipartBody.Part attachment, long message, boolean isEncrypted) {
        userRepository.updateAttachment(id, attachment, message, isEncrypted)
                .subscribe(new Observer<MessageAttachment>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageAttachment messageAttachment) {
                        updateAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException) {
                            if (((HttpException)e).code() == 413) {
                                updateAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR_TOO_LARGE);
                            } else {
                                updateAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                            }
                        } else {
                            updateAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteAttachment(long id) {
        userRepository.deleteAttachment(id)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        deleteAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
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
}
