package com.ctemplar.app.fdroid.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.message.ViewMessagesActivity;
import com.ctemplar.app.fdroid.message.ViewMessagesFragment;
import com.ctemplar.app.fdroid.net.socket.NotificationServiceWebSocket;
import com.ctemplar.app.fdroid.net.socket.NotificationServiceWebSocketCallback;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.utils.LaunchUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class NotificationService extends Service {
    private static Map<NotificationServiceListener, NotificationServiceConnection> listenerServiceConnectionMap;

    private final NotificationServiceBinder binder = new NotificationServiceBinder();
    private final NotificationServiceWebSocketCallback notificationServiceWebSocketCallback = messageProvider -> {
        if (CTemplarApp.isInForeground()) {
            for (NotificationServiceListener notificationServiceListener : binder.listenerList) {
                notificationServiceListener.onNewMessage(messageProvider);
            }
        } else {
            showMessageNotification(messageProvider);
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
        Timber.i("onCreate");
        NotificationServiceWebSocket notificationServiceWebSocket = NotificationServiceWebSocket.getInstance();
        notificationServiceWebSocket.start(notificationServiceWebSocketCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            onRestarted();
            return START_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationServiceWebSocket.getInstance().shutdown();
    }

    private void onRestarted() {

    }

    private void showMessageNotification(MessageProvider messageProvider) {
        String parent = messageProvider.getParent();
        long parentId = -1;
        if (parent != null && !parent.isEmpty()) {
            try {
                parentId = Long.parseLong(parent);
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
        }
        sendNotification(messageProvider.getSender(), messageProvider.getSubject(), messageProvider.getFolderName(), messageProvider.getId(), parentId, messageProvider.isSubjectEncrypted());
    }

    private void sendNotification(String sender, String subject, String folder, long messageId, long parentId, boolean isSubjectEncrypted) {
        long id = (parentId == -1) ? messageId : parentId;
        String content = (isSubjectEncrypted) ? getString(R.string.txt_encrypted_subject) : subject;

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String channelId = getString(R.string.channel_id);
        String channelName = "messages";

        Intent intent = new Intent(this, ViewMessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ViewMessagesActivity.PARENT_ID, id);
        intent.putExtra(ViewMessagesFragment.FOLDER_NAME, folder);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(sender)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo(sender)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.mipmap.ic_launcher_small);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription(getString(R.string.app_name));
            notificationChannel.setShowBadge(true);
            notificationChannel.canShowBadge();
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int randomId = new Random(System.currentTimeMillis()).nextInt(1000);
        notificationManager.notify(randomId, notificationBuilder.build());
    }


    private static void start(Context context) {
        LaunchUtils.launchService(context.getApplicationContext(), NotificationService.class);
    }

    private static void stop(Context context) {
        LaunchUtils.shutdownService(context.getApplicationContext(), NotificationService.class);
    }

    public static void bind(Context context, NotificationServiceListener listener) {
        NotificationServiceConnection serviceConnection = new NotificationServiceConnection(listener);
        if (listenerServiceConnectionMap == null) {
            listenerServiceConnectionMap = new HashMap<>();
        }
        listenerServiceConnectionMap.put(listener, serviceConnection);
        Intent intent = new Intent(context, NotificationService.class);
        boolean bindedSuccess;
        try {
            bindedSuccess = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable e) {
            Timber.e(e, "bind bindService failed");
            return;
        }
        if (!bindedSuccess) {
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
        String preferenceName = context.getResources()
                .getString(R.string.push_notifications_enabled);
        boolean active = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(preferenceName, false);
        updateState(context, active);
    }

    public static void updateState(Context context, boolean active) {
        if (active && CTemplarApp.isAuthorized()) {
            start(context);
        } else {
            stop(context);
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
