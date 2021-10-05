package com.ctemplar.app.fdroid.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import timber.log.Timber;

public class ToastUtils {
    private static Handler mainHandler;
    private static Handler getMainHandler() {
        return mainHandler != null ? mainHandler : (mainHandler = new Handler(Looper.getMainLooper()));
    }

    public static void showToast(Context context, String message) {
        if (context == null) {
            Timber.e("Context is null. Message: %s", message);
            return;
        }
        getMainHandler().post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(Context context, int resourceId) {
        if (context == null) {
            Timber.e("Context is null. ResourceId: %s", resourceId);
            return;
        }
        getMainHandler().post(() -> Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show());
    }

    public static void showLongToast(Context context, String message) {
        if (context == null) {
            Timber.e("Context is null. Message: %s", message);
            return;
        }
        getMainHandler().post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    public static void showLongToast(Context context, int resourceId) {
        if (context == null) {
            Timber.e("Context is null. ResourceId: %s", resourceId);
            return;
        }
        getMainHandler().post(() -> Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show());
    }
}
