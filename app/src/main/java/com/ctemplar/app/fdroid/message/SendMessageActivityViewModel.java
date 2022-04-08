package com.ctemplar.app.fdroid.message;

import androidx.annotation.NonNull;
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
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.messages.EncryptionMessageRequest;
import com.ctemplar.app.fdroid.net.request.messages.SendMessageRequest;
import com.ctemplar.app.fdroid.net.response.messages.EncryptionMessageResponse;
import com.ctemplar.app.fdroid.net.response.messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.myself.MyselfResponse;
import com.ctemplar.app.fdroid.repository.MailboxDao;
import com.ctemplar.app.fdroid.repository.MessagesRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.security.PGPManager;
import com.ctemplar.app.fdroid.utils.AppUtils;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

public class SendMessageActivityViewModel extends ViewModel {
    private final MessagesRepository messagesRepository;
    private final UserRepository userRepository;
    private final MailboxDao mailboxDao;

    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deleteAttachmentStatus = new MutableLiveData<>();
    private final MutableLiveData<AttachmentProvider> grabAttachmentResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> grabAttachmentStatus = new MutableLiveData<>();
    private final MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();
    private final MutableLiveData<MessagesResult> createMessageResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> createMessageStatus = new MutableLiveData<>();
    private final MutableLiveData<MessagesResult> messageEncryptionResult = new MutableLiveData<>();
    private final MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    private final MutableLiveData<MessageProvider> openMessageResponse = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        messagesRepository = MessagesRepository.getInstance();
        userRepository = CTemplarApp.getUserRepository();
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    }

    public List<MailboxEntity> getMailboxes() {
        return mailboxDao.getAll();
    }

    public List<String> getEnabledMailboxAddresses() {
        return mailboxDao.getEnabledMailboxAddresses();
    }

    public MailboxEntity getDefaultMailbox() {
        return mailboxDao.getDefault();
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

    public LiveData<MessagesResult> getCreateMessageResponse() {
        return createMessageResponse;
    }

    public LiveData<ResponseStatus> getCreateMessageStatus() {
        return createMessageStatus;
    }

    public LiveData<ResponseStatus> getDeleteAttachmentStatus() {
        return deleteAttachmentStatus;
    }

    public MutableLiveData<AttachmentProvider> getGrabAttachmentResponse() {
        return grabAttachmentResponse;
    }

    public MutableLiveData<Boolean> getGrabAttachmentStatus() {
        return grabAttachmentStatus;
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
        final EncryptionMessageRequest encryptionMessage = sendMessageRequest.getEncryptionMessage();
        final String password = encryptionMessage.getPassword();
        encryptionMessage.setPassword(null);
        userRepository.updateMessage(id, sendMessageRequest)
                .subscribe(new SingleObserver<MessagesResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull MessagesResult messagesResult) {
                        if (messagesResult.getEncryptionMessage() == null) {
                            messageEncryptionResult.postValue(null);
                        } else {
                            EncryptionMessageResponse encryption = messagesResult.getEncryptionMessage();
                            encryption.setPassword(password);
                            messageEncryptionResult.postValue(messagesResult);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                        messageEncryptionResult.postValue(null);
                    }
                });
    }

    public void deleteMessage(long messageId) {
        userRepository.deleteMessages(new Long[]{messageId});
    }

    public MutableLiveData<DTOResource<MessageAttachment>> uploadAttachment(
            MultipartBody.Part document,
            long messageId,
            boolean isInline,
            boolean isEncrypted,
            String fileType,
            String name,
            long actualSize
    ) {
        return userRepository.uploadAttachment(document, messageId, isInline, isEncrypted, fileType,
                name, actualSize);
    }

    public void deleteAttachment(long id) {
        userRepository.deleteAttachment(id).observeForever(resource -> {
            if (resource.isSuccess()) {
                deleteAttachmentStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
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

    public void grabForwardedAttachments(
            @NonNull final List<AttachmentProvider> forwardedAttachments,
            final long messageId
    ) {
        Single.create(emitter -> {
            for (AttachmentProvider forwardedAttachment : forwardedAttachments) {
                MessageAttachment attachment = remakeAttachmentSync(forwardedAttachment, messageId);
                if (attachment != null) {
                    AttachmentProvider attachmentProvider = AttachmentProvider.fromResponse(attachment);
                    attachmentProvider.setForwarded(true);
                    grabAttachmentResponse.postValue(attachmentProvider);
                } else {
                    Timber.e("grabForwardedAttachments uploaded attachment is null");
                }
            }
            grabAttachmentStatus.postValue(true);
            Timber.d("Grabbed all forwarded attachments");
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private MessageAttachment remakeAttachmentSync(AttachmentProvider attachmentProvider,
                                                   long messageId) {
        File cacheDir = CTemplarApp.getInstance().getCacheDir();
        URL url;
        try {
            url = new URL(attachmentProvider.getDocumentUrl());
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
            downloadedFile = File.createTempFile(UUID.randomUUID().toString(), null, cacheDir);
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
        String documentUrl = attachmentProvider.getDocumentUrl();
        String fileName = attachmentProvider.getName() == null
                ? AppUtils.getFileNameFromURL(documentUrl) : attachmentProvider.getName();
        String type = attachmentProvider.getFileType() == null
                ? AppUtils.getMimeType(documentUrl) : attachmentProvider.getFileType();
        if (type == null) {
            type = "";
        }
        MediaType mediaType = MediaType.parse(type);
        RequestBody attachmentPart = RequestBody.create(mediaType, downloadedFile);
        final MultipartBody.Part document = MultipartBody.Part
                .createFormData("document", fileName, attachmentPart);

        DTOResource<MessageAttachment> messageAttachment = userRepository.uploadAttachmentSync(
                document, messageId, false, attachmentProvider.isEncrypted(), type, fileName,
                downloadedFile.length());

        if (!downloadedFile.delete()) {
            Timber.e("Downloaded file is not deleted (2)");
        }
        if (!messageAttachment.isSuccess()) {
            Timber.e("remakeAttachment uploadAttachmentSync error %s",
                    messageAttachment.getError());
            return null;
        }
        return messageAttachment.getDto();
    }
}
