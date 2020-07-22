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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.EncryptionMessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageAttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.SendMessageRequestProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.LaunchUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import timber.log.Timber;

public class SendMailService extends IntentService {
    private static final String TAG = "SendMailService";
    private static final String SEND_MAIL_ACTION = "com.ctemplar.service.mail.send";

    private static final String SEND_MAIL_NOTIFICATION_CHANNEL_ID = "com.ctemplar.mail.sending";
    private static final String SEND_MAIL_NOTIFICATION_CHANNEL_NAME = "Sending mail...";
    private static final String SEND_MAIL_NOTIFICATION_CHANNEL_DESCRIPTION = "Status";

    private static final String SERVER_FULL_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
    private static Gson GSON = new GsonBuilder().setDateFormat(SERVER_FULL_DATE_FORMAT).create();

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
                        Timber.wtf(e, "Cannot parse attachment provider");
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
                sendMessage(messageId, sendMessageRequestProvider, publicKeys, attachmentProviders, encryptionMessageProvider);
        }
    }

    private void runAsForeground() {

    }

    public void sendMessage(
            long messageId,
            @NonNull
            SendMessageRequestProvider sendMessageRequestProvider,
            String[] publicKeys,
            MessageAttachmentProvider[] attachmentProviders,
            @Nullable
            EncryptionMessageProvider encryptionMessageProvider
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
            long messageId,
            SendMessageRequest request,
            List<String> receiverPublicKeys,
            NotificationManager notificationManager,
            NotificationCompat.Builder notificationBuilder
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

    private void onFailedUpdateMessage(Throwable e, long messageId, NotificationManager notificationManager, NotificationCompat.Builder notificationBuilder) {
        Timber.e(e, "onFailedUpdateMessage");
        ToastUtils.showLongToast(this, "Sending failed: " + e.getMessage());
        notificationBuilder.setContentText("Sending failed")
                .setOngoing(false);
        notificationManager.notify((int) messageId, notificationBuilder.build());
        notificationManager.cancel((int) messageId);
    }

    private void onMessageUploadedSuccess(MessagesResult messagesResult, NotificationManager notificationManager, NotificationCompat.Builder notificationBuilder) {
        Timber.i("Sending success");
        ToastUtils.showLongToast(this, "Uploaded message success");
        notificationBuilder.setContentText("Sending success")
                .setOngoing(false);
        notificationManager.notify((int) messagesResult.getId(), notificationBuilder.build());
        notificationManager.cancel((int) messagesResult.getId());
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
        if (encryptionMessageProvider != null) {
            intent.putExtra(EXTERNAL_ENCRYPTION_EXTRA_KEY, GSON.toJson(encryptionMessageProvider));
        }
        LaunchUtils.launchService(context, intent);
    }
}
