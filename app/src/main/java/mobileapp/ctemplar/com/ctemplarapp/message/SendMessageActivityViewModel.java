package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class SendMessageActivityViewModel extends ViewModel {

    private MessagesRepository messagesRepository;
    private UserRepository userRepository;
    private ContactsRepository contactsRepository;
    private MailboxDao mailboxDao;

    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> uploadAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<MessageAttachment> uploadAttachmentResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> updateAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> grabAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> createMessageResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> createMessageStatus = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsResponse = new MutableLiveData<>();
    private MutableLiveData<KeyResponse> keyResponse = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> messageEncryptionResult = new MutableLiveData<>();
    private MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    private MutableLiveData<MessageEntity> openMessageResponse = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        messagesRepository = MessagesRepository.getInstance();
        userRepository = CTemplarApp.getUserRepository();
        contactsRepository = CTemplarApp.getContactsRepository();
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    }

    public List<MailboxEntity> getMailboxes() {
        return mailboxDao.getAll();
    }

    public MailboxEntity getMailboxById(long id) {
        return mailboxDao.getById(id);
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

    public LiveData<Boolean> getGrabAttachmentStatus() {
        return grabAttachmentStatus;
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
            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);
            content = PGPManager.encrypt(content, publicKeys);
            if (isSubjectEncrypted && !subject.isEmpty()) {
                subject = PGPManager.encrypt(subject, publicKeys);
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
                        messagesResult.postValue(null);
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
                        new Thread(() -> {
                            ContactData[] contacts = response.getResults();
                            ContactData[] decryptedContacts = Contact.decryptContactData(contacts);

                            contactsRepository.saveContacts(decryptedContacts);
                            List<Contact> contactList1 = Contact.fromResponseResults(decryptedContacts);

                            contactsResponse.postValue(contactList1);
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

    @Nullable
    MessageAttachment uploadAttachmentSync(
            MultipartBody.Part attachment,
            final long message,
            boolean isEncrypted
    ) {
        try {
            return userRepository
                    .uploadAttachment(attachment, message, isEncrypted)
                    .blockingSingle();
        } catch (Throwable e) {
            if(e instanceof HttpException) {
                if (((HttpException)e).code() == 413) {
                    uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR_TOO_LARGE);
                } else {
                    uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                }
            } else {
                uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
            }
            Timber.e(e, "uploadAttachmentSync blocking error");
            return null;
        }
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



    void grabForwardedAttachments(
            @NonNull final List<AttachmentProvider> forwardedAttachments,
            final long messageId
    ) {
        Single.create(emitter -> {
            for (AttachmentProvider forwardedAttachment : forwardedAttachments) {
                MessageAttachment attachment = remakeAttachment(forwardedAttachment, messageId);
                if (attachment != null) {
                    uploadAttachmentResponse.postValue(attachment);
                } else {
                    Timber.e("grabForwardedAttachments uploaded attachment is null");
                }
            }
            grabAttachmentStatus.postValue(true);
            Timber.i("Grabbed all forwarded attachments");
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private MessageAttachment remakeAttachment(AttachmentProvider attachmentProvider, long messageId) {
        File cacheDir = CTemplarApp.getInstance().getCacheDir();
        URL url;
        try {
            url = new URL(attachmentProvider.getDocumentLink());
        } catch (MalformedURLException e) {
            Timber.e(e, "remakeAttachment MalformedURLException");
            return null;
        }
        InputStream downloadStream;
        try {
            downloadStream = url.openStream();
        } catch (IOException e) {
            Timber.e(e, "remakeAttachment download stream error");
            return null;
        }
        BufferedInputStream inputStream = new BufferedInputStream(downloadStream);
        File downloadedFile;
        try {
            downloadedFile = File.createTempFile("AtTouchMeNow", ".ext", cacheDir);
        } catch (IOException e) {
            Timber.e(e, "remakeAttachment createTempFile error");
            return null;
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(downloadedFile);
        } catch (FileNotFoundException e) {
            Timber.wtf(e, "remakeAttachment Temp file not found");
            if (!downloadedFile.delete()) {
                Timber.e("Downloaded file is not deleted");
            }
            return null;
        }
        BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);
        try {
            FileUtils.copyBytes(inputStream, fileOutputStream);
        } catch (IOException e) {
            Timber.e(e, "remakeAttachment copyBytes error");
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Timber.e(e, "remakeAttachment close inputStream error");
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                Timber.e(e, "remakeAttachment close outputStream error");
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Timber.e(e, "remakeAttachment close fileOutputStream error");
            }
        }
        String documentLink = attachmentProvider.getDocumentLink();
        String fileName = AppUtils.getFileNameFromURL(documentLink);
        String type = AppUtils.getMimeType(documentLink);
        if (type == null) {
            type = "";
        }
        MediaType mediaType = MediaType.parse(type);
        RequestBody attachmentPart = RequestBody.create(mediaType, downloadedFile);
        final MultipartBody.Part multipartAttachment = MultipartBody.Part
                .createFormData("document", fileName, attachmentPart);

        MessageAttachment messageAttachment = uploadAttachmentSync(
                multipartAttachment, messageId, attachmentProvider.isEncrypted());
        if (!downloadedFile.delete()) {
            Timber.e("Downloaded file is not deleted (2)");
        }
        return messageAttachment;
    }
}
