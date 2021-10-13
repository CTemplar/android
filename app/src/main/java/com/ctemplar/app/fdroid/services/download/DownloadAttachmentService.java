package com.ctemplar.app.fdroid.services.download;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.OkHttpClientFactory;
import com.ctemplar.app.fdroid.services.ServiceConstants;
import com.ctemplar.app.fdroid.utils.AppUtils;
import com.ctemplar.app.fdroid.utils.EncryptUtils;
import com.ctemplar.app.fdroid.utils.FileUtils;
import com.ctemplar.app.fdroid.utils.LaunchUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

public class DownloadAttachmentService extends Service {
    private static final String TAG = "DownloadAttachmentService";

    private static final File externalFilesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
    private NotificationManager notificationManager;
    private Looper taskLooper;
    private TaskHandler taskHandler;


    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        taskLooper = thread.getLooper();
        taskHandler = new TaskHandler(taskLooper);

        createNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            onRestarted();
            return START_STICKY;
        }
        String action = intent.getAction();
        if (action == null) {
            return START_STICKY;
        }
        switch (action) {
            case ServiceConstants.DOWNLOAD_ATTACHMENT_SERVICE_ADD_TO_QUEUE_ACTION:
                addTaskToQueue(intent, startId);
                break;
            default:
                if (LaunchUtils.needForeground(intent)) {
                    startForegroundPriority(generateNotificationId(),
                            createTaskNotificationBuilder(getString(R.string.download_attachment_service)).build());
                    Timber.d("Required foreground");
                }
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        taskLooper.quit();
    }

    private void onRestarted() {

    }

    private void addTaskToQueue(Intent intent, int startId) {
        Message msg = taskHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        taskHandler.sendMessage(msg);
    }

    private DownloadAttachmentTask parseTask(String json) {
        try {
            return GENERAL_GSON.fromJson(json, DownloadAttachmentTask.class);
        } catch (Throwable e) {
            Timber.e(e);
            return null;
        }
    }

    private NotificationCompat.Builder createTaskNotificationBuilder(String title) {
        return new NotificationCompat
                .Builder(this, ServiceConstants.DOWNLOAD_ATTACHMENT_SERVICE_FOREGROUND_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_attachment_unchecked)
                .setOngoing(true)
                .setProgress(100, 0, false);
    }

    private void emitTask(DownloadAttachmentTask task) throws IOException {
        if (task == null || task.attachments == null || task.attachments.length == 0) {
            return;
        }
        Timber.d("Emitting the task");
        NotificationCompat.Builder notificationBuilder = createTaskNotificationBuilder(
                getString(R.string.downloading_attachments, task.attachments.length));
        int notificationId = generateNotificationId();
        startForegroundPriority(notificationId, notificationBuilder.build());
        int attachmentCounter = 0;
        int attachmentsCount = task.attachments.length;
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        StringBuilder bigTextBuffer = new StringBuilder();
        notificationBuilder.setStyle(bigTextStyle);
        for (DownloadAttachmentInfo attachment : task.attachments) {
            bigTextStyle.bigText(bigTextBuffer.append("\n").append(getString(R.string.downloading))
                    .append(" '").append(attachment.name).append("' (").append(++attachmentCounter)
                    .append("/").append(attachmentsCount).append(")"));
            notificationManager.notify(notificationId, notificationBuilder.build());
            File tempFile = downloadAttachment(attachment.url, ((progress, max) -> {
                notificationBuilder.setProgress((int) (max / 100), (int) (progress / 100), false);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }));
            bigTextStyle.bigText(bigTextBuffer.append("\n").append(getString(R.string.processing))
                    .append(" '").append(attachment.name).append("' (").append(++attachmentCounter)
                    .append("/").append(attachmentsCount).append(")"));
            notificationBuilder.setProgress(100, 100, true);
            notificationManager.notify(notificationId, notificationBuilder.build());
            boolean extractedSuccess = extractAttachment(tempFile, generateOutputAttachmentFile(attachment), attachment);
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    Timber.e("Temp file not deleted");
                }
            }
            bigTextBuffer.append("\n").append(attachment.name).append(" ");
            if (!extractedSuccess) {
                bigTextBuffer.append(getString(R.string.failed_to_extract).toLowerCase());
                Timber.e("Failed to extract attachment");
            } else {
                bigTextBuffer.append(getString(R.string.saved_successfully).toLowerCase());
            }
            notificationBuilder.setProgress(0, 0, false);
            bigTextStyle.bigText(bigTextBuffer);
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
        notificationBuilder.setOngoing(false);
        stopForeground(true);
        notificationManager.notify(generateNotificationId(), notificationBuilder.build());
        Timber.d("Finished the task");
    }

    private void executeTask(Intent intent) {
        String stringData = intent.getStringExtra(ServiceConstants.DOWNLOAD_ATTACHMENT_SERVICE_TASK_EXTRA_KEY);
        DownloadAttachmentTask task = parseTask(stringData);
        try {
            emitTask(task);
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private File downloadAttachment(String url, DownloadProgressCallback progressCallback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = OkHttpClientFactory.newClient()
                .newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new RuntimeException("Body is null");
        }
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        File downloadedFile = File.createTempFile("attachment-", null, getCacheDir());
        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
        Buffer sinkBuffer = sink.getBuffer();
        long totalBytesRead = 0;
        int bufferSize = 8 * 1024;
        for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
            sink.emit();
            totalBytesRead += bytesRead;
            progressCallback.onProgress(totalBytesRead, contentLength);
        }
        try {
            sink.flush();
        } catch (IOException ignore) {
        }
        try {
            sink.close();
        } catch (IOException ignore) {
        }
        try {
            source.close();
        } catch (IOException ignore) {
        }
        return downloadedFile;
    }

    private boolean extractAttachment(File rawAttachmentFile, File destinationFile, DownloadAttachmentInfo attachmentInfo) {
        if (attachmentInfo.gpgEncryption != null) {
            String password = attachmentInfo.gpgEncryption.password;
            if (password == null) {
                ToastUtils.showLongToast(this, getString(R.string.firstly_decrypt_message));
                return false;
            }
            return EncryptUtils.decryptAttachmentGPG(
                    rawAttachmentFile, destinationFile, password
            );
        }
        if (attachmentInfo.pgpEncryption != null) {
            long mailboxId = attachmentInfo.pgpEncryption.mailboxId;
            String password = attachmentInfo.pgpEncryption.password;
            return EncryptUtils.decryptAttachment(
                    rawAttachmentFile, destinationFile, password, mailboxId
            );
        }
        return rawAttachmentFile.renameTo(destinationFile);
    }

    private void startForegroundPriority(int id, Notification notification) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForeground(id, notification);
        } else {
            notificationManager.notify(id, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ServiceConstants.DOWNLOAD_ATTACHMENT_SERVICE_FOREGROUND_CHANNEL_ID,
                    getString(R.string.title_notification_service),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(getString(R.string.title_service_running));
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void start(Context context, DownloadAttachmentTask task) {
        Intent intent = new Intent(context, DownloadAttachmentService.class)
                .setAction(ServiceConstants.DOWNLOAD_ATTACHMENT_SERVICE_ADD_TO_QUEUE_ACTION)
                .putExtra(ServiceConstants.DOWNLOAD_ATTACHMENT_SERVICE_TASK_EXTRA_KEY, GENERAL_GSON.toJson(task));
        LaunchUtils.launchService(context, intent);
    }


    private static File generateOutputAttachmentFile(DownloadAttachmentInfo attachment) {
        String originalFileName = attachment.name == null
                ? AppUtils.getFileNameFromURL(attachment.url) : attachment.name;
        File file;
        try {
            file = FileUtils.generateFileName(originalFileName, externalFilesDir);
        } catch (Throwable e) {
            file = null;
        }
        if (file == null) {
            file = new File(externalFilesDir, originalFileName);
        }
        return file;
    }

    private static int notificationCounter = 0;

    private static int generateNotificationId() {
        return 5000 + ++notificationCounter;
    }

    private class TaskHandler extends Handler {
        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            executeTask((Intent) msg.obj);
            stopSelf(msg.arg1);
        }
    }

    private interface DownloadProgressCallback {
        void onProgress(long progress, long max);
    }
}
