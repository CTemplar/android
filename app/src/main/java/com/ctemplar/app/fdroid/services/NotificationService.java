package com.ctemplar.app.fdroid.services;

import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.FOLDER_NAME;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.main.MainActivity;
import com.ctemplar.app.fdroid.message.ViewMessagesActivity;
import com.ctemplar.app.fdroid.net.socket.WebSocketClient;
import com.ctemplar.app.fdroid.net.socket.WebSocketClientCallback;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.utils.LaunchUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class NotificationService extends Service {
    private static Map<NotificationServiceListener, NotificationServiceConnection> listenerServiceConnectionMap;
    private WebSocketClient webSocketClient;

    private final NotificationServiceBinder binder = new NotificationServiceBinder();
    private final WebSocketClientCallback webSocketClientCallback = new WebSocketClientCallback() {
        @Override
        public void onNewMessage(MessageProvider messageProvider) {
            if (CTemplarApp.isInForeground()) {
                for (NotificationServiceListener notificationServiceListener : binder.listenerList) {
                    notificationServiceListener.onNewEmail(messageProvider);
                }
            }
            showEmailNotification(messageProvider);
        }

        @Override
        public void onUpdateUnreadCount(Map<String, Integer> unreadCount) {
            for (NotificationServiceListener notificationServiceListener : binder.listenerList) {
                notificationServiceListener.onUpdateUnreadCount(unreadCount);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreate");
        webSocketClient = WebSocketClient.getInstance();
        webSocketClient.start(webSocketClientCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            onRestarted();
            return START_STICKY;
        }
        String action = intent.getAction();
        if (ServiceConstants.NOTIFICATION_SERVICE_ACTION_STOP.equals(action)) {
            webSocketClient.shutdown();
            stopForeground(true);
            stopSelf();
            LaunchUtils.shutdownService(this, getClass());
            return START_NOT_STICKY;
        } else if (ServiceConstants.NOTIFICATION_SERVICE_ACTION_START.equals(action)) {
            Timber.d("onStartCommand action START");
            startForegroundPriority();
            webSocketClient.start();
        } else if (ServiceConstants.NOTIFICATION_SERVICE_ACTION_RESTART.equals(action)) {
            if (LaunchUtils.needForeground(intent)) {
                startForegroundPriority();
            }
            webSocketClient.restart();
        } else {
            if (LaunchUtils.needForeground(intent)) {
                startForegroundPriority();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        webSocketClient.shutdown();
        NotificationServiceBroadcastReceiver.sendStartNotificationService(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Timber.d("onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(),
                1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
    }

    private void onRestarted() {
        Timber.d("onRestarted");
    }

    private void startForegroundPriority() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            return;
        }
        createForegroundNotificationChannel();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, ServiceConstants.NOTIFICATION_SERVICE_FOREGROUND_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_small)
                .setContentTitle(getString(R.string.title_notification_service))
                .setContentText(getString(R.string.title_service_running))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setWhen(0)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(),
                        0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
        startForeground(ServiceConstants.NOTIFICATION_SERVICE_FOREGROUND_ID, builder.build());
    }

    private void createForegroundNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ServiceConstants.NOTIFICATION_SERVICE_FOREGROUND_CHANNEL_ID,
                    getString(R.string.title_notification_service),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.title_service_running));
            channel.setShowBadge(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                Timber.e("createForegroundNotificationChannel NotificationManager is null");
                return;
            }
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showEmailNotification(MessageProvider messageProvider) {
        String parent = messageProvider.getParent();
        long parentId = -1;
        if (parent != null && !parent.isEmpty()) {
            try {
                parentId = Long.parseLong(parent);
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
        }
        showNotification(
                messageProvider.getSenderDisplayName(),
                messageProvider.getSubject(),
                messageProvider.getFolderName(),
                messageProvider.getId(), parentId,
                messageProvider.isSubjectEncrypted()
        );
    }

    private void showNotification(String sender, String subject, String folder, long messageId,
                                  long parentId, boolean isSubjectEncrypted) {
        long id = (parentId == -1) ? messageId : parentId;
        int notificationID = (messageId == -1) ? new Random().nextInt(1000) : (int) messageId;
        String content = (isSubjectEncrypted) ? getString(R.string.txt_new_message) : subject;

        Intent intent = new Intent(this, ViewMessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ViewMessagesActivity.PARENT_ID, id);
        intent.putExtra(FOLDER_NAME, folder);
        intent.putExtra(ServiceConstants.FROM_NOTIFICATION_SERVICE, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(this, ServiceConstants.NOTIFICATION_SERVICE_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_small)
//                .setGroup(String.valueOf(parentId))
                .setContentTitle(sender)
                .setContentText(content)
                .setContentInfo(sender)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Timber.e("showNotification NotificationManager is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    ServiceConstants.NOTIFICATION_SERVICE_CHANNEL_ID, getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            notificationChannel.setShowBadge(false);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(notificationID, notificationBuilder.build());
    }


    private static void start(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ServiceConstants.NOTIFICATION_SERVICE_ACTION_START);
        LaunchUtils.launchService(context, intent);
    }

    private static void stop(Context context) {
        if (!LaunchUtils.isServiceRunning(context, NotificationService.class)) {
            return;
        }
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ServiceConstants.NOTIFICATION_SERVICE_ACTION_STOP);
        LaunchUtils.launchService(context, intent);
    }

    public static void bind(Context context, NotificationServiceListener listener) {
        NotificationServiceConnection serviceConnection = new NotificationServiceConnection(listener);
        if (listenerServiceConnectionMap == null) {
            listenerServiceConnectionMap = new HashMap<>();
        }
        listenerServiceConnectionMap.put(listener, serviceConnection);
        Intent intent = new Intent(context, NotificationService.class);
        boolean boundSuccess;
        try {
            boundSuccess = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable e) {
            Timber.e(e, "bind bindService failed");
            return;
        }
        if (!boundSuccess) {
            Timber.e("bind bind failed");
        }
    }

    public static void unbind(Context context, NotificationServiceListener listener) {
        if (listenerServiceConnectionMap == null) {
            return;
        }
        NotificationServiceConnection serviceConnection = listenerServiceConnectionMap.get(listener);
        if (serviceConnection == null) {
            return;
        }
        NotificationServiceBinder binder = serviceConnection.binder;
        if (binder != null) {
            binder.removeListener(serviceConnection.listener);
        }
        try {
            context.unbindService(serviceConnection);
        } catch (Throwable e) {
            Timber.e(e, "unbindService error");
        }
        listenerServiceConnectionMap.remove(listener);
        if (listenerServiceConnectionMap.isEmpty()) {
            listenerServiceConnectionMap = null;
        }
    }

    public static void updateState(Context context) {
        boolean active = CTemplarApp.getUserStore().isNotificationsEnabled();
        if (active && CTemplarApp.isAuthorized()) {
            start(context);
        } else {
            stop(context);
        }
    }

    public static void restart(Context context) {
        boolean active = CTemplarApp.getUserStore().isNotificationsEnabled();
        if (active && CTemplarApp.isAuthorized()) {
            Intent intent = new Intent(context, NotificationService.class);
            intent.setAction(ServiceConstants.NOTIFICATION_SERVICE_ACTION_RESTART);
            LaunchUtils.launchService(context, intent);
        }
    }

    static class NotificationServiceConnection implements ServiceConnection {
        private final NotificationServiceListener listener;
        private NotificationServiceBinder binder;

        private NotificationServiceConnection(NotificationServiceListener listener) {
            this.listener = listener;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof NotificationServiceBinder) {
                binder = (NotificationServiceBinder) service;
                binder.addListener(listener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (binder != null) {
                binder.removeListener(listener);
            }
        }
    }
}
