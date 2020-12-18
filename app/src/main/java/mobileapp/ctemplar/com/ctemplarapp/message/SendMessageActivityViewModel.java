package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

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
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageAttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class SendMessageActivityViewModel extends ViewModel {
    private final MessagesRepository messagesRepository;
    private final UserRepository userRepository;
    private final MailboxDao mailboxDao;

    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> uploadAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<MessageAttachmentProvider> uploadAttachmentResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> updateAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> grabAttachmentStatus = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> createMessageResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> createMessageStatus = new MutableLiveData<>();
    private MutableLiveData<KeyResponse> keyResponse = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> messageEncryptionResult = new MutableLiveData<>();
    private MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    private MutableLiveData<MessageProvider> openMessageResponse = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        messagesRepository = MessagesRepository.getInstance();
        userRepository = CTemplarApp.getUserRepository();
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    }

    public List<MailboxEntity> getMailboxes() {
        return mailboxDao.getAll();
    }

    public MailboxEntity getMailboxById(long id) {
        return mailboxDao.getById(id);
    }

    public MailboxEntity getMailboxByEmail(String email) {
        return mailboxDao.getByEmail(email);
    }

    public boolean isSignatureEnabled() {
        return userRepository.isSignatureEnabled();
    }

    public String getUserPassword() {
        return userRepository.getUserPassword();
    }

    public boolean isDraftsAutoSaveEnabled() {
        return userRepository.isDraftsAutoSaveEnabled();
    }

    public LiveData<MessagesResult> getMessagesResult() {
        return messagesResult;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
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

    public LiveData<MessageAttachmentProvider> getUploadAttachmentResponse() {
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

    public LiveData<MessageProvider> getOpenMessageResponse() {
        return openMessageResponse;
    }

    public void createMessage(SendMessageRequest request) {
        userRepository.sendMessage(request)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MessagesResult messagesResult) {
                        createMessageStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        createMessageResponse.postValue(messagesResult);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        createMessageStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void openMessage(long id) {
        MessageEntity messageEntity = messagesRepository.getLocalMessage(id);
        Single.fromCallable(() -> MessageProvider.fromMessageEntity(messageEntity, true, true))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new SingleObserver<MessageProvider>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull MessageProvider messageProvider) {
                        openMessageResponse.postValue(messageProvider);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    @Deprecated
    public void updateMessage(long id, SendMessageRequest request, List<String> receiverPublicKeys) {
        String content = request.getContent();
        String subject = request.getSubject();
        boolean isSubjectEncrypted = request.isSubjectEncrypted();
        boolean isEmptyReceiverKeys = receiverPublicKeys.isEmpty();

        if (!isEmptyReceiverKeys) {
            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);
            content = PGPManager.encrypt(content, publicKeys);
            if (isSubjectEncrypted && EditTextUtils.isNotEmpty(subject)) {
                subject = PGPManager.encrypt(subject, publicKeys);
            }
            request.setContent(content);
            request.setSubject(subject);
        }
        request.setEncrypted(!isEmptyReceiverKeys);
        request.setSubjectEncrypted(isSubjectEncrypted && !isEmptyReceiverKeys);

        userRepository.updateMessage(id, request)
                .subscribe(new SingleObserver<MessagesResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull MessagesResult result) {
                        messagesResult.postValue(result);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        messagesResult.postValue(null);
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }
                });
    }

    public void setEncryptionMessage(long id, SendMessageRequest sendMessageRequest) {
        userRepository.updateMessage(id, sendMessageRequest)
                .subscribe(new SingleObserver<MessagesResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull MessagesResult messagesResult) {
                        messageEncryptionResult.postValue(messagesResult);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e.getCause());
                    }
                });
    }

    public void deleteMessage(long messageId) {
        userRepository.deleteMessages(String.valueOf(messageId))
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> voidResponse) {
                        Timber.i("deleteMessage");
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

    public void getEmailPublicKeys(PublicKeysRequest request) {
        userRepository.getEmailPublicKeys(request)
                .subscribe(new Observer<KeyResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull KeyResponse keyResponse) {
                        SendMessageActivityViewModel.this.keyResponse.postValue(keyResponse);
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

    public void uploadAttachment(
            @NonNull final MultipartBody.Part attachment,
            final long messageId,
            final String filePath,
            final boolean isEncrypted
    ) {
        userRepository.uploadAttachment(attachment, messageId, isEncrypted)
                .subscribe(new Observer<MessageAttachment>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MessageAttachment messageAttachment) {
                        if (messageAttachment != null) {
                            MessageAttachmentProvider messageAttachmentProvider
                                    = MessageAttachmentProvider.fromResponse(messageAttachment);
                            messageAttachmentProvider.setFilePath(filePath);
                            uploadAttachmentResponse.postValue(messageAttachmentProvider);
                            uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        } else {
                            uploadAttachmentStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e instanceof HttpException) {
                            if (((HttpException) e).code() == 413) {
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
            long message,
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

    public void updateAttachment(
            long id,
            MultipartBody.Part attachment,
            long message,
            boolean isEncrypted
    ) {
        userRepository.updateAttachment(id, attachment, message, isEncrypted)
                .subscribe(new SingleObserver<MessageAttachment>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull MessageAttachment messageAttachment) {
                        updateAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                });
    }

    public void deleteAttachment(long id) {
        userRepository.deleteAttachment(id)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> voidResponse) {
                        deleteAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MyselfResponse response) {
                        myselfResponse.postValue(response);
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



    void grabForwardedAttachments(
            @NonNull final List<AttachmentProvider> forwardedAttachments,
            final long messageId
    ) {
        Single.create(emitter -> {
            for (AttachmentProvider forwardedAttachment : forwardedAttachments) {
                MessageAttachment attachment = remakeAttachment(forwardedAttachment, messageId);
                if (attachment != null) {
                    MessageAttachmentProvider messageAttachmentProvider
                            = MessageAttachmentProvider.fromResponse(attachment);
                    uploadAttachmentResponse.postValue(messageAttachmentProvider);
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
            downloadedFile = File.createTempFile("attachment", ".ext", cacheDir);
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
        String fileName = DateUtils.getFileNameFromURL(documentLink);
        String type = DateUtils.getMimeType(documentLink);
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
