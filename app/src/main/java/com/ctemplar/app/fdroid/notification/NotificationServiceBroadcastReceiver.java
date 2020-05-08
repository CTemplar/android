package com.ctemplar.app.fdroid.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import timber.log.Timber;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

public class NotificationServiceBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_START = "com.ctemplar.notification_service.START";

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive");
        if (intent == null) {
            Timber.e("intent is null");
        } else {
            String action = intent.getAction();
            if (action == null) {
                Timber.e("Action is null");
            } else {
                if (action.equals(ACTION_START)) {
                    Timber.i("Action start");
                } else if (action.equals(ACTION_BOOT_COMPLETED)) {
                    Timber.i("Boot completed");
                }
            }
        }
        NotificationService.updateState(context);
    }

    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter(ACTION_START);
        context.registerReceiver(this, intentFilter);
    }

    public static void sendStartNotificationService(Context context) {
        Intent intent = new Intent(ACTION_START);
        intent.setClass(context, NotificationServiceBroadcastReceiver.class);
        context.sendBroadcast(intent);
    }
}
