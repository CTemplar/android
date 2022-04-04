package mobileapp.ctemplar.com.ctemplarapp.services;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.common.io.Files;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.request.messages.EncryptionMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.messages.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.keys.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.keys.KeysResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.EncryptionMessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.SendMessageRequestProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.LaunchUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

public class SendMailService extends IntentService {
    private static final String TAG = "SendMailService";

    private static final String MESSAGE_ID_EXTRA_KEY = "message_id";
    private static final String MESSAGE_PROVIDER_EXTRA_KEY = "message_provider";
    private static final String SENDER_PUBLIC_KEY = "sender_public_key";
    private static final String ATTACHMENTS_EXTRA_KEY = "attachments";
    private static final String EXTERNAL_ENCRYPTION_EXTRA_KEY = "external_encryption";
    private static final String DRAFT_MESSAGE = "draft_message";

    private static final UserRepository userRepository = CTemplarApp.getUserRepository();

    public SendMailService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case ServiceConstants.SEND_MAIL_SERVICE_ACTION:
                long messageId = intent.getLongExtra(MESSAGE_ID_EXTRA_KEY, -1);
                if (messageId < 0) {
                    Timber.e("Message id is null");
                    return;
                }
                String messageProviderString = intent.getStringExtra(MESSAGE_PROVIDER_EXTRA_KEY);
                if (messageProviderString == null) {
                    Timber.e("Message provider is null");
                    return;
                }
                SendMessageRequestProvider sendMessageRequestProvider;
                try {
                    sendMessageRequestProvider = GENERAL_GSON.fromJson(messageProviderString, SendMessageRequestProvider.class);
                } catch (JsonSyntaxException e) {
                    Timber.e(e, "Cannot parse message provider");
                    return;
                }
                String senderPublicKey = intent.getStringExtra(SENDER_PUBLIC_KEY);
                if (senderPublicKey == null) {
                    senderPublicKey = "";
                }
                String[] attachmentsStringArray = intent.getStringArrayExtra(ATTACHMENTS_EXTRA_KEY);
                if (attachmentsStringArray == null) {
                    attachmentsStringArray = new String[0];
                }
                AttachmentProvider[] attachmentProviders = new AttachmentProvider[attachmentsStringArray.length];
                for (int i = 0; i < attachmentProviders.length; ++i) {
                    try {
                        attachmentProviders[i] = GENERAL_GSON.fromJson(attachmentsStringArray[i], AttachmentProvider.class);
                    } catch (JsonSyntaxException e) {
                        Timber.e(e, "Cannot parse attachment provider");
                    }
                }
                String externalEncryptionMessageString = intent.getStringExtra(EXTERNAL_ENCRYPTION_EXTRA_KEY);
                EncryptionMessageProvider encryptionMessageProvider = null;
                if (externalEncryptionMessageString != null) {
                    try {
                        encryptionMessageProvider = GENERAL_GSON.fromJson(externalEncryptionMessageString, EncryptionMessageProvider.class);
                    } catch (JsonSyntaxException e) {
                        Timber.e(e, "Cannot parse external encryption provider");
                    }
                }
                boolean draftMessage = intent.getBooleanExtra(DRAFT_MESSAGE, true);
                sendMail(messageId, sendMessageRequestProvider, senderPublicKey, attachmentProviders, encryptionMessageProvider, draftMessage);
        }
    }

    public void sendMail(
            final long messageId,
            final @NonNull SendMessageRequestProvider sendMessageRequestProvider,
            final String senderPublicKey,
            final AttachmentProvider[] attachmentsProvider,
            @Nullable EncryptionMessageProvider encryptionMessageProvider,
            boolean draftMessage
    ) {
        SendMessageRequest sendMessageRequest = sendMessageRequestProvider.toRequest();
        String title = draftMessage ? getString(R.string.txt_saving_mail) : getString(R.string.txt_sending_mail);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Timber.e("notificationManager is null");
            return;
        }
        createSendMailNotificationChannel(notificationManager, title);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(this, ServiceConstants.SEND_MAIL_SERVICE_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_message_send)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOngoing(true)
                .setProgress(100, 10, true);
        notificationManager.notify((int) messageId, notificationBuilder.build());

        List<String> publicKeyList = new ArrayList<>(Collections.singleton(senderPublicKey));
        DTOResource<KeysResponse> keysResponse = userRepository.getEmailPublicKeysSync(
                getRecipients(sendMessageRequest));
        if (keysResponse.isSuccess()) {
            for (KeyResponse keyResponse : keysResponse.getDto().getKeys()) {
                publicKeyList.add(keyResponse.getPublicKey());
            }
        } else {
            ToastUtils.showToast(this, keysResponse.getError());
            draftMessage = true;
        }

        if (draftMessage) {
            encryptionMessageProvider = null;
        }
        if (encryptionMessageProvider != null) {
            sendMessageRequest.setEncryptionMessage(encryptionMessageProvider.toRequest());
        }

        // non-CTemplar receivers checking
        boolean publicKeyListContainsEmpty = publicKeyList.contains(null) || publicKeyList.contains("");
        if (publicKeyListContainsEmpty && !draftMessage && encryptionMessageProvider == null) {
            publicKeyList.clear();
        } else if (publicKeyListContainsEmpty) {
            publicKeyList.removeAll(Arrays.asList(null, ""));
        }

        final List<MessageAttachment> messageAttachmentList = new ArrayList<>();
        if (attachmentsProvider.length > 0) {
            if (!draftMessage) {
                notificationBuilder.setContentText(getString(R.string.txt_attachments_in_processing));
                notificationBuilder.setProgress(attachmentsProvider.length, 0, false);
                notificationManager.notify((int) messageId, notificationBuilder.build());
                for (int i = 0, attachmentProvidersLength = attachmentsProvider.length; i < attachmentProvidersLength; ++i) {
                    AttachmentProvider attachmentProvider = attachmentsProvider[i];
                    MessageAttachment messageAttachment = updateAttachment(attachmentProvider, publicKeyList,
                            messageId, sendMessageRequestProvider.getMailbox(), encryptionMessageProvider);
                    if (messageAttachment == null) {
                        Timber.e("Message attachment is null");
                    } else {
                        messageAttachmentList.add(messageAttachment);
                    }
                    notificationBuilder.setProgress(attachmentProvidersLength, i + 1, false);
                    notificationManager.notify((int) messageId, notificationBuilder.build());
                }
            }
            sendMessageRequest.setAttachments(messageAttachmentList);
        }

        updateMessage(messageId, sendMessageRequest, publicKeyList, notificationManager, notificationBuilder, draftMessage);
    }

    private Set<String> getRecipients(SendMessageRequest sendMessageRequest) {
        Set<String> recipients = new HashSet<>();
        recipients.addAll(sendMessageRequest.getReceivers());
        recipients.addAll(sendMessageRequest.getCc());
        recipients.addAll(sendMessageRequest.getBcc());
        return recipients;
    }

    private void createSendMailNotificationChannel(NotificationManager notificationManager, String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ServiceConstants.SEND_MAIL_SERVICE_CHANNEL_ID,
                    title, NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateMessage(
            final long messageId,
            final SendMessageRequest request,
            final List<String> receiverPublicKeys,
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder,
            final boolean isDraft
    ) {
        EncryptionMessageRequest encryptionMessage = request.getEncryptionMessage();
        final String subject = request.getSubject();
        final String content = request.getContent();
        if (encryptionMessage != null) {
            final String password = encryptionMessage.getPassword();
            encryptionMessage.setPassword(null);
            request.setEncryptionMessage(encryptionMessage);
            request.setSubject(PGPManager.encryptGPG(subject, password));
            request.setContent(PGPManager.encryptGPG(content, password));
        } else if (receiverPublicKeys.size() > 0) {
            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);
            request.setSubject(PGPManager.encrypt(subject, publicKeys));
            request.setContent(PGPManager.encrypt(content, publicKeys));
        }

        boolean isMessageEncrypted = encryptionMessage != null || receiverPublicKeys.size() > 0;
        request.setSubjectEncrypted(isMessageEncrypted);
        request.setEncrypted(isMessageEncrypted);
        request.setUpdatedAt(new Date());

        DTOResource<MessagesResult> messagesResult = userRepository.updateMessageSync(messageId,
                request);
        if (!messagesResult.isSuccess()) {
            onFailedUpdateMessage(messagesResult.getError(), messageId, isDraft, notificationManager,
                    notificationBuilder);
            return;
        }
        onMessageSentSuccess(messagesResult.getDto(), isDraft, notificationManager, notificationBuilder);
    }

    private void onFailedUpdateMessage(
            final String error,
            final long messageId,
            final boolean isDraft,
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder
    ) {
        Timber.e("onFailedUpdateMessage: %s ; Draft: %s", error, isDraft);
        String errorMessage = isDraft ? getString(R.string.toast_not_saved) : error;
        ToastUtils.showToast(this, errorMessage);
        notificationBuilder.setContentText(errorMessage).setOngoing(false);
        notificationManager.notify((int) messageId, notificationBuilder.build());
        notificationManager.cancel((int) messageId);
    }

    private void onMessageSentSuccess(
            final MessagesResult messagesResult,
            final boolean isDraft,
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder
    ) {
        Timber.d("onMessageSentSuccess");
        String displayMessage = isDraft ? getString(R.string.toast_message_saved_as_draft) : getString(R.string.toast_message_sent);
        ToastUtils.showLongToast(this, displayMessage);
        notificationBuilder.setContentText(displayMessage).setOngoing(false);
        notificationManager.notify((int) messagesResult.getId(), notificationBuilder.build());
        notificationManager.cancel((int) messagesResult.getId());
    }

    private File downloadAttachment(String attachmentUrl) {
        File cacheDir = getCacheDir();
        URL url;
        try {
            url = new URL(attachmentUrl);
        } catch (MalformedURLException e) {
            Timber.e(e, "downloadAttachment: MalformedURLException");
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            Timber.e(e, "downloadAttachment: download inputStream error");
            return null;
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        File downloadedFile;
        try {
            downloadedFile = File.createTempFile(UUID.randomUUID().toString(), null, cacheDir);
        } catch (IOException e) {
            Timber.e(e, "downloadAttachment: create downloaded tmp file error");
            return null;
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(downloadedFile);
        } catch (FileNotFoundException e) {
            Timber.wtf(e, "downloadAttachment: downloaded tmp file not found");
            if (!downloadedFile.delete()) {
                Timber.e("downloadAttachment: downloaded file is not deleted after error");
            }
            return null;
        }
        BufferedOutputStream outputBufferedStream = new BufferedOutputStream(fileOutputStream);
        try {
            FileUtils.copyBytes(bufferedInputStream, outputBufferedStream);
        } catch (IOException e) {
            Timber.e(e, "downloadAttachment: copyBytes error");
            if (!downloadedFile.delete()) {
                Timber.e("downloadAttachment: downloaded file is not deleted after error");
            }
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Timber.e(e, "downloadAttachment: close inputStream error");
            }
            try {
                outputBufferedStream.close();
            } catch (IOException e) {
                Timber.e(e, "downloadAttachment: close outputBufferedStream error");
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Timber.e(e, "downloadAttachment: close fileOutputStream error");
            }
        }
        return downloadedFile;
    }

    private MessageAttachment updateAttachment(
            final AttachmentProvider attachmentsProvider,
            final List<String> publicKeyList,
            final long messageId,
            final long mailboxId,
            final EncryptionMessageProvider encryptionMessageProvider
    ) {
        File decryptedFile = null;
        String filePath = attachmentsProvider.getFilePath();
        if (EditTextUtils.isNotEmpty(filePath)) {
            decryptedFile = new File(filePath);
        }

        if (decryptedFile == null || !decryptedFile.exists()) {
            File downloadedFile = downloadAttachment(attachmentsProvider.getDocumentUrl());
            if (downloadedFile == null) {
                Timber.e("updateAttachment: download attachment error");
                return null;
            }
            try {
                decryptedFile = File.createTempFile(UUID.randomUUID().toString(), null,
                        getCacheDir());
            } catch (IOException e) {
                Timber.e(e, "updateAttachment: create decrypted tmp file error");
                return null;
            }
            if (attachmentsProvider.isEncrypted()) {
                try {
                    EncryptUtils.decryptAttachment(downloadedFile, decryptedFile,
                            userRepository.getUserPassword(), mailboxId);
                } catch (InterruptedException e) {
                    Timber.e(e, "updateAttachment: downloaded file decryption fail");
                    return null;
                }
            } else {
                try {
                    Files.copy(downloadedFile, decryptedFile);
                } catch (IOException e) {
                    Timber.e(e, "updateAttachment: unencrypted file copy fail");
                    return null;
                }
            }
            if (!downloadedFile.delete()) {
                Timber.e("updateAttachment: downloaded file is not deleted");
            }
        }

        String documentUrl = attachmentsProvider.getDocumentUrl();
        String type = attachmentsProvider.getFileType() == null
                ? AppUtils.getMimeType(documentUrl) : attachmentsProvider.getFileType();
        if (type == null) {
            Timber.w("updateAttachment: type is null");
            type = "";
        }
        String fileName = attachmentsProvider.getName() == null
                ? AppUtils.getFileNameFromURL(documentUrl) : attachmentsProvider.getName();
        MediaType mediaType = MediaType.parse(type);

        File encryptedFile;
        try {
            encryptedFile = File.createTempFile(UUID.randomUUID().toString(), null,
                    getCacheDir());
        } catch (IOException e) {
            Timber.e(e, "updateAttachment: create encrypted attachment error");
            return null;
        }
        RequestBody attachmentPart;
        if (encryptionMessageProvider != null) {
            final String password = encryptionMessageProvider.getPassword();
            EncryptUtils.encryptAttachmentGPG(decryptedFile, encryptedFile, password);
            attachmentPart = RequestBody.create(encryptedFile, mediaType);
        } else if (publicKeyList.size() > 0) {
            EncryptUtils.encryptAttachment(decryptedFile, encryptedFile, publicKeyList);
            attachmentPart = RequestBody.create(encryptedFile, mediaType);
        } else {
            attachmentPart = RequestBody.create(decryptedFile, mediaType);
        }
        final MultipartBody.Part document = MultipartBody.Part
                .createFormData("document", fileName, attachmentPart);

        DTOResource<MessageAttachment> messageAttachment = userRepository.updateAttachmentSync(
                attachmentsProvider.getId(),
                document,
                messageId,
                false,
                true,
                type,
                fileName,
                decryptedFile.length()
        );
        if (!decryptedFile.delete()) {
            Timber.e("updateAttachment: delete decrypted cached file error");
        }
        if (!encryptedFile.delete()) {
            Timber.e("updateAttachment: delete encrypted cached file error");
        }
        if (!messageAttachment.isSuccess()) {
            ToastUtils.showToast(this, messageAttachment.getError());
            return null;
        }
        return messageAttachment.getDto();
    }

    public static void sendMessage(
            final Context context,
            final long messageId,
            final SendMessageRequestProvider sendMessageRequestProvider,
            final String senderPublicKey,
            final AttachmentProvider[] attachmentsProvider,
            final EncryptionMessageProvider encryptionMessageProvider,
            final boolean draftMessage
    ) {
        Intent intent = new Intent(ServiceConstants.SEND_MAIL_SERVICE_ACTION);
        intent.setComponent(new ComponentName(context, SendMailService.class));
        intent.putExtra(MESSAGE_ID_EXTRA_KEY, messageId);
        intent.putExtra(MESSAGE_PROVIDER_EXTRA_KEY, GENERAL_GSON.toJson(sendMessageRequestProvider));
        intent.putExtra(SENDER_PUBLIC_KEY, senderPublicKey);
        String[] attachmentsStringArray = new String[attachmentsProvider.length];
        for (int i = 0, count = attachmentsProvider.length; i < count; ++i) {
            AttachmentProvider attachmentProvider = attachmentsProvider[i];
            attachmentsStringArray[i] = GENERAL_GSON.toJson(attachmentProvider);
        }
        intent.putExtra(ATTACHMENTS_EXTRA_KEY, attachmentsStringArray);
        if (encryptionMessageProvider != null) {
            intent.putExtra(EXTERNAL_ENCRYPTION_EXTRA_KEY, GENERAL_GSON.toJson(encryptionMessageProvider));
        }
        intent.putExtra(DRAFT_MESSAGE, draftMessage);
        LaunchUtils.launchService(context, intent);
    }
}
