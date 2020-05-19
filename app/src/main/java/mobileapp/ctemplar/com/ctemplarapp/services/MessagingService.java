package mobileapp.ctemplar.com.ctemplarapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.InboxFragment;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.RemoteMessageAction;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.RemoteMessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import retrofit2.Response;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesFragment.FOLDER_NAME;

public class MessagingService extends FirebaseMessagingService {
    private Random random = new Random();
    private UserStore userStore;

    public MessagingService() {
        userStore = CTemplarApp.getUserStore();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: %s", remoteMessage.getData());
            Map<String, String> remoteMessageMap = remoteMessage.getData();
            RemoteMessageEntity remoteMessageEntity = RemoteMessageEntity.getFromMap(remoteMessageMap);

            if (RemoteMessageAction.CHANGE_PASSWORD.toString()
                    .equals(remoteMessageEntity.getAction())) {
                onPasswordChanged();
                return;
            }
            boolean isNotificationsEnabled = userStore.getNotificationsEnabled();
            if (isNotificationsEnabled) {
                sendNotification(
                        remoteMessageEntity.getSender(),
                        remoteMessageEntity.getSubject(),
                        remoteMessageEntity.getFolder(),
                        remoteMessageEntity.getMessageID(),
                        remoteMessageEntity.getParentID(),
                        remoteMessageEntity.isSubjectEncrypted()
                );
            }
            WeakReference<InboxFragment> inboxFragmentReference = InboxFragment.instanceReference;
            if (inboxFragmentReference != null) {
                InboxFragment inboxFragment = inboxFragmentReference.get();
                if (inboxFragment != null && !inboxFragment.isRemoving()) {
                    inboxFragment.onNewMessage(remoteMessageEntity.getMessageID());
                }
            }
        }
    }

    private void onPasswordChanged() {
        Timber.d("onPasswordChanged");
        String token = CTemplarApp.getUserRepository().getFirebaseToken();
        CTemplarApp.getUserRepository().signOut(MainActivityViewModel.ANDROID, token)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        postExit();
                    }

                    @Override
                    public void onError(Throwable e) {
                        postExit();
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void postExit() {
        Intent intent = new Intent(MainActivityViewModel.EXIT_BROADCAST_ACTION);
        sendBroadcast(intent);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void sendNotification(String sender, String subject, String folder, long messageId, long parentId, boolean isSubjectEncrypted) {
        long id = (parentId == -1) ? messageId : parentId;
        String content = (isSubjectEncrypted) ? getString(R.string.txt_encrypted_subject) : subject;

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String channelId = getString(R.string.channel_id);
        String channelName = "messages";

        Intent intent = new Intent(this, ViewMessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(PARENT_ID, id);
        intent.putExtra(FOLDER_NAME, folder);
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

        int randomId = random.nextInt(1000);
        notificationManager.notify(randomId, notificationBuilder.build());
    }
}
