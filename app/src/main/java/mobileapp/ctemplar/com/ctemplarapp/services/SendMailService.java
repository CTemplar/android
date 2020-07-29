package mobileapp.ctemplar.com.ctemplarapp.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
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
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.EncryptionMessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageAttachmentProvider;
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
import retrofit2.HttpException;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;

public class SendMailService extends IntentService {
    private static final String TAG = "SendMailService";
    private static final String SEND_MAIL_ACTION = "com.ctemplar.service.mail.send";

    private static final String SEND_MAIL_NOTIFICATION_CHANNEL_ID = "com.ctemplar.mail.sending";
    private static final String SEND_MAIL_NOTIFICATION_CHANNEL_NAME = "Sending mail...";
    private static final String SEND_MAIL_NOTIFICATION_CHANNEL_DESCRIPTION = "Status";

//    private static final String SERVER_FULL_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
//    private static Gson GSON = new GsonBuilder().setDateFormat(SERVER_FULL_DATE_FORMAT).create();
    private static Gson GSON = new Gson();

    private static final String MESSAGE_ID_EXTRA_KEY = "message_id";
    private static final String MESSAGE_PROVIDER_EXTRA_KEY = "message_provider";
    private static final String PUBLIC_KEYS_EXTRA_KEY = "public_keys";
    private static final String ATTACHMENTS_EXTRA_KEY = "attachments";
    private static final String EXTERNAL_ENCRYPTION_EXTRA_KEY = "external_encryption";

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
        if (LaunchUtils.needForeground(intent)) {
            runAsForeground();
        }
        switch (action) {
            case SEND_MAIL_ACTION:
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
                    sendMessageRequestProvider = GSON.fromJson(messageProviderString, SendMessageRequestProvider.class);
                } catch (JsonSyntaxException e) {
                    Timber.e(e, "Cannot parse message provider");
                    return;
                }
                String[] publicKeys = intent.getStringArrayExtra(PUBLIC_KEYS_EXTRA_KEY);
                if (publicKeys == null) {
                    publicKeys = new String[0];
                }
                String[] attachmentsStringArray = intent.getStringArrayExtra(ATTACHMENTS_EXTRA_KEY);
                if (attachmentsStringArray == null) {
                    attachmentsStringArray = new String[0];
                }
                MessageAttachmentProvider[] attachmentProviders = new MessageAttachmentProvider[attachmentsStringArray.length];
                for (int i = 0; i < attachmentProviders.length; ++i) {
                    try {
                        attachmentProviders[i] = GSON.fromJson(attachmentsStringArray[i], MessageAttachmentProvider.class);
                    } catch (JsonSyntaxException e) {
                        Timber.e(e, "Cannot parse attachment provider");
                    }
                }
                String externalEncryptionMessageString = intent.getStringExtra(EXTERNAL_ENCRYPTION_EXTRA_KEY);
                EncryptionMessageProvider encryptionMessageProvider = null;
                if (externalEncryptionMessageString != null) {
                    try {
                        encryptionMessageProvider = GSON.fromJson(externalEncryptionMessageString, EncryptionMessageProvider.class);
                    } catch (JsonSyntaxException e) {
                        Timber.e(e, "Cannot parse external encryption provider");
                    }
                }
                sendMail(messageId, sendMessageRequestProvider, publicKeys, attachmentProviders, encryptionMessageProvider);
        }
    }

    private void runAsForeground() {

    }

    public void sendMail(
            final long messageId,
            @NonNull
            final SendMessageRequestProvider sendMessageRequestProvider,
            final String[] publicKeys,
            final MessageAttachmentProvider[] attachmentProviders,
            @Nullable
            final EncryptionMessageProvider encryptionMessageProvider
    ) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createUploadMessageNotificationChannel(notificationManager);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, SEND_MAIL_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setContentTitle("Sending mail")
                .setContentText("In progress..")
                .setSmallIcon(R.drawable.ic_message_send)
                .setOngoing(true)
                .setProgress(100, 10, true);
        notificationManager.notify((int) messageId, notificationBuilder.build());

        SendMessageRequest sendMessageRequest = sendMessageRequestProvider.toRequest();
        List<String> publicKeyList = Arrays.asList(publicKeys);
        if (encryptionMessageProvider != null) {
            String randomSecret = encryptionMessageProvider.getRandomSecret();
            String password = encryptionMessageProvider.getPassword();

            PGPKeyEntity pgpKeyEntity = PGPManager.generateKeys(randomSecret, password);
            encryptionMessageProvider.setPublicKey(pgpKeyEntity.getPublicKey());
            encryptionMessageProvider.setPrivateKey(pgpKeyEntity.getPrivateKey());

            publicKeyList.add(pgpKeyEntity.getPublicKey());
            sendMessageRequest.setEncryptionMessage(encryptionMessageProvider.toRequest());
        }

        // non-CTemplar receivers checking
        if (publicKeyList.contains(null) && encryptionMessageProvider == null) {
            publicKeyList.clear();
        } else if (publicKeyList.contains(null)) {
            publicKeyList.removeAll(Collections.singleton(null));
        }

        final List<MessageAttachment> messageAttachmentList = new ArrayList<>(attachmentProviders.length);
        if (attachmentProviders.length > 0) {
            if (!DRAFT.equals(sendMessageRequest.getFolder())) {
                notificationBuilder.setContentText("Attachments uploading");
                notificationBuilder.setProgress(attachmentProviders.length, 0, false);
                notificationManager.notify((int) messageId, notificationBuilder.build());
                for (int i = 0, attachmentProvidersLength = attachmentProviders.length; i < attachmentProvidersLength; ++i) {
                    MessageAttachmentProvider attachmentProvider = attachmentProviders[i];
                    MessageAttachment messageAttachment = updateAttachment(attachmentProvider,
                            publicKeyList, messageId, sendMessageRequestProvider.getMailbox());
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

        updateMessage(messageId, sendMessageRequest, publicKeyList, notificationManager, notificationBuilder);
    }

    private void createUploadMessageNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(
                    SEND_MAIL_NOTIFICATION_CHANNEL_ID, SEND_MAIL_NOTIFICATION_CHANNEL_NAME, importance);
            channel.setDescription(SEND_MAIL_NOTIFICATION_CHANNEL_DESCRIPTION);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateMessage(
            final long messageId,
            final SendMessageRequest request,
            final List<String> receiverPublicKeys,
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder
    ) {
        String content = request.getContent();
        String subject = request.getSubject();
        boolean isSubjectEncrypted = request.isSubjectEncrypted();
        boolean isEmptyReceiverKeys = receiverPublicKeys.isEmpty();

        if (!isEmptyReceiverKeys) {
            String[] publicKeys = receiverPublicKeys.toArray(new String[0]);
            content = PGPManager.encrypt(content, publicKeys);
            if (isSubjectEncrypted && !subject.isEmpty()) {
                subject = PGPManager.encrypt(subject, publicKeys);
            }
            request.setContent(content);
            request.setSubject(subject);
        }
        request.setIsEncrypted(!isEmptyReceiverKeys);
        request.setSubjectEncrypted(isSubjectEncrypted && !isEmptyReceiverKeys);

        MessagesResult messagesResult;
        try {
            messagesResult = CTemplarApp.getUserRepository().updateMessageSync(messageId, request);
        } catch (Throwable e) {
            onFailedUpdateMessage(e, messageId, notificationManager, notificationBuilder);
            return;
        }
        onMessageUploadedSuccess(messagesResult, notificationManager, notificationBuilder);
    }

    private void onFailedUpdateMessage(
            final Throwable e,
            final long messageId,
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder
    ) {
        Timber.e(e, "onFailedUpdateMessage");
        ToastUtils.showLongToast(this, "Failed upload message: " + e.getMessage());
        notificationBuilder.setContentText("Upload failed")
                .setOngoing(false);
        notificationManager.notify((int) messageId, notificationBuilder.build());
        notificationManager.cancel((int) messageId);
    }

    private void onMessageUploadedSuccess(
            final MessagesResult messagesResult,
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder
    ) {
        Timber.i("Uploaded message success");
        ToastUtils.showLongToast(this, "Uploaded message success");
        notificationBuilder.setContentText("Uploaded success")
                .setOngoing(false);
        notificationManager.notify((int) messagesResult.getId(), notificationBuilder.build());
        notificationManager.cancel((int) messagesResult.getId());
    }

    private MessageAttachment updateAttachment(
            final MessageAttachmentProvider attachmentProvider,
            final List<String> publicKeyList,
            final long messageId,
            final long mailboxId
    ) {
        File cacheDir = getCacheDir();
        File decryptedFile = null;
        boolean isCachedFile = false;

        String localFilePath = attachmentProvider.getFilePath();
        if (EditTextUtils.isNotEmpty(localFilePath)) {
            decryptedFile = new File(localFilePath);
        }

        if (decryptedFile == null || !decryptedFile.exists()) {
            URL url;
            try {
                url = new URL(attachmentProvider.getDocumentLink());
            } catch (MalformedURLException e) {
                Timber.e(e, "updateAttachment: MalformedURLException");
                return null;
            }
            InputStream inputStream;
            try {
                inputStream = url.openStream();
            } catch (IOException e) {
                Timber.e(e, "updateAttachment: download inputStream error");
                return null;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            File downloadedFile;
            try {
                downloadedFile = File.createTempFile("downloadedAttachment", ".ext", cacheDir);
            } catch (IOException e) {
                Timber.e(e, "updateAttachment: create downloaded tmp file error");
                return null;
            }
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(downloadedFile);
            } catch (FileNotFoundException e) {
                Timber.wtf(e, "updateAttachment: downloaded tmp file not found");
                if (!downloadedFile.delete()) {
                    Timber.e("Downloaded file is not deleted after error");
                }
                return null;
            }
            BufferedOutputStream outputBufferedStream = new BufferedOutputStream(fileOutputStream);
            try {
                FileUtils.copyBytes(bufferedInputStream, outputBufferedStream);
            } catch (IOException e) {
                Timber.e(e, "updateAttachment: copyBytes error");
                return null;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Timber.e(e, "updateAttachment: close inputStream error");
                }
                try {
                    outputBufferedStream.close();
                } catch (IOException e) {
                    Timber.e(e, "updateAttachment: close outputBufferedStream error");
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Timber.e(e, "updateAttachment: close fileOutputStream error");
                }
            }
            try {
                decryptedFile = File.createTempFile("decryptedAttachment", ".ext", cacheDir);
            } catch (IOException e) {
                Timber.e(e, "updateAttachment: create decrypted tmp file error");
                return null;
            }
            if (attachmentProvider.isEncrypted()) {
                MailboxEntity mailboxEntity = CTemplarApp.getAppDatabase().mailboxDao().getById(mailboxId);
                String privateKey = mailboxEntity.getPrivateKey();
                String password = CTemplarApp.getUserRepository().getUserPassword();
                EncryptUtils.decryptAttachment(downloadedFile, decryptedFile, password, privateKey);
                if (!downloadedFile.delete()) {
                    Timber.e("Downloaded file is not deleted after decryption error");
                }
            }
            isCachedFile = true;
        }

        String documentLink = attachmentProvider.getDocumentLink();
        String type = AppUtils.getMimeType(documentLink);
        if (type == null) {
            type = "";
        }
        String fileName = AppUtils.getFileNameFromURL(documentLink);
        MediaType mediaType = MediaType.parse(type);

        File encryptedFile;
        try {
            encryptedFile = File.createTempFile("encryptedAttachment", ".ext", cacheDir);
        } catch (IOException e) {
            Timber.e(e, "updateAttachment: create encrypted attachment error");
            return null;
        }
        RequestBody attachmentPart;
        if (publicKeyList.isEmpty()) {
            attachmentPart = RequestBody.create(mediaType, decryptedFile);
        } else {
            EncryptUtils.encryptAttachment(decryptedFile, encryptedFile, publicKeyList);
            attachmentPart = RequestBody.create(mediaType, encryptedFile);
        }

        final MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", fileName, attachmentPart);

        MessageAttachment result;
        try {
            result = CTemplarApp.getUserRepository()
                    .updateAttachmentSync(
                            attachmentProvider.getId(),
                            multipartAttachment,
                            messageId,
                            true
                    );
        } catch (Throwable e) {
            if(e instanceof HttpException) {
                if (((HttpException)e).code() == 413) {
                    ToastUtils.showLongToast(this, "Upload attachment: Too large file");
                } else {
                    ToastUtils.showLongToast(this, "Upload attachment: Something went wrong");
                }
            } else {
                ToastUtils.showLongToast(this, "Upload attachment: Some error is occured");
            }
            return null;
        }
        if (isCachedFile && !decryptedFile.delete()) {
            Timber.e("updateAttachment: delete decrypted cached file error");
        }
        if (!encryptedFile.delete()) {
            Timber.e("updateAttachment: delete encrypted cached file error");
        }
        return result;
    }

    public static void sendMessage(
            Context context,
            long messageId,
            SendMessageRequestProvider sendMessageRequestProvider,
            String[] publicKeyList,
            MessageAttachmentProvider[] attachmentProviderList,
            EncryptionMessageProvider encryptionMessageProvider
    ) {
        Intent intent = new Intent(SEND_MAIL_ACTION);
        intent.setComponent(new ComponentName(context, SendMailService.class));
        intent.putExtra(MESSAGE_ID_EXTRA_KEY, messageId);
        intent.putExtra(MESSAGE_PROVIDER_EXTRA_KEY, GSON.toJson(sendMessageRequestProvider));
        intent.putExtra(PUBLIC_KEYS_EXTRA_KEY, publicKeyList);
        String[] attachmentsStringArray = new String[attachmentProviderList.length];
        for (int i = 0, count = attachmentProviderList.length; i < count; ++i) {
            MessageAttachmentProvider attachmentProvider = attachmentProviderList[i];
            attachmentsStringArray[i] = GSON.toJson(attachmentProvider);
        }
        intent.putExtra(ATTACHMENTS_EXTRA_KEY, attachmentsStringArray);
        if (encryptionMessageProvider != null) {
            intent.putExtra(EXTERNAL_ENCRYPTION_EXTRA_KEY, GSON.toJson(encryptionMessageProvider));
        }
        LaunchUtils.launchService(context, intent);
    }
}
