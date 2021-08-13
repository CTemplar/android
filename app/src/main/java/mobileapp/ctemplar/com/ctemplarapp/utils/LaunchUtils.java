package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import timber.log.Timber;

public class LaunchUtils {
    private static final String EXTRA_NEED_FOREGROUND = "need_foreground";

    public static boolean launchActivity(Context context, Class<?> cls) {
        return launchActivity(context, new Intent(context, cls));
    }

    public static boolean launchActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            Timber.e(e, "launchActivity error");
            return false;
        }
        return true;
    }

    public static boolean launchActivity(Context context, String action) {
        return launchActivity(context, new Intent(action));
    }

    public static boolean launchSingleService(Context context, Class<?> cls) {
        if (isServiceRunning(context, cls)) {
            return false;
        }
        return launchService(context, cls);
    }

    public static boolean launchService(Context context, Class<?> cls) {
        return launchService(context, new Intent(context, cls));
    }

    public static boolean shutdownService(Context context, Class<?> cls) {
        return shutdownService(context, new Intent(context, cls));
    }

    public static boolean shutdownService(Context context, Intent intent) {
        try {
            return context.stopService(intent);
        } catch (SecurityException e) {
            Timber.e(e, "shutdownService SecurityException");
        } catch (IllegalArgumentException e) {
            Timber.e(e, "shutdownService IllegalArgumentException");
        } catch (Throwable e) {
            Timber.e(e, "shutdownService undefined exception");
        }
        return false;
    }

    public static boolean launchService(Context context, Intent intent) {
        try {
            context.startService(intent);
        } catch (Throwable e) {
            Timber.i(e, "launchService launch in foreground");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(EXTRA_NEED_FOREGROUND, true);
                try {
                    context.startForegroundService(intent);
                } catch (Throwable e1) {
                    String message = "launchService startForegroundService error: " + e1.getMessage();
                    Timber.e(e1, message);
                    return false;
                }
                return true;
            }
            String message = "launchService startService error: " + e.getMessage();
            Timber.e(e, message);
            return false;
        }
        return true;
    }

    public static boolean launchService(Context context, ComponentName componentName) {
        Intent intent = new Intent();
        intent.setComponent(componentName);
        return launchService(context, intent);
    }

    public static boolean launchForegroundService(Context context, Class<?> serviceClass) {
        return launchForegroundService(context, new Intent(context, serviceClass));
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean launchForegroundService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return launchForegroundServiceInternal(context, intent);
        }
        return launchNotForegroundServiceInternal(context, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean launchForegroundServiceInternal(Context context, Intent intent) {
        intent.putExtra(EXTRA_NEED_FOREGROUND, true);
        try {
            context.startForegroundService(intent);
        } catch (Throwable e) {
            String message = "launchForegroundServiceInternal error: " + e.getMessage();
            Timber.e(e, message);
            return false;
        }
        return true;
    }

    private static boolean launchNotForegroundServiceInternal(Context context, Intent intent) {
        try {
            context.startService(intent);
        } catch (Throwable e) {
            String message = "launchNotForegroundServiceInternal failed: " + e.getMessage();
            Timber.e(e, message);
            return false;
        }
        return true;
    }

    public static boolean needForeground(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.getBooleanExtra(EXTRA_NEED_FOREGROUND, false);
    }

    public static boolean needForeground(Bundle extras) {
        if (extras == null) {
            return false;
        }
        return extras.getBoolean(EXTRA_NEED_FOREGROUND, false);
    }
}
