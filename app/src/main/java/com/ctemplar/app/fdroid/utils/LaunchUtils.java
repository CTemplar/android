package com.ctemplar.app.fdroid.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class LaunchUtils {
    private static final String EXTRA_NEED_FOREGROUND = "need_foreground";

    public static boolean launchApp(Context context, String packageName) {
        Timber.i( "launchApp(" + packageName + ")");
        Intent launchIntent;
        try {
            launchIntent = getLaunchIntentForPackage(context.getPackageManager(), packageName);
        } catch (Throwable e) {
            Timber.e(e, "launchApp(" + packageName + "). Unhandled exception from getLaunchIntentForPackage");
            return false;
        }
        if (launchIntent == null) {
            Timber.e( "Launch App intent is null");
            return false;
        }
        try {
            context.startActivity(launchIntent);
        } catch (Throwable e) {
            Timber.e(e, "launchApp(" + packageName + ") error");
            return false;
        }
        return true;
    }

    @Nullable
    public static Intent getLaunchIntentForPackage(PackageManager packageManager, String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            return launchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }

        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.setPackage(packageName);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> launchables = packageManager.queryIntentActivities(main, 0);

        Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(packageManager));

        if (launchables == null || launchables.isEmpty()) {
            return null;
        }

        ResolveInfo launchable = launchables.get(0);
        ActivityInfo activityInfo = launchable.activityInfo;
        ComponentName componentName =
                new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
        return new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .setComponent(componentName);
    }


    public static boolean launchNewActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return launchActivity(context, intent);
    }

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
            return true;
        } catch (Throwable e) {
            Timber.e(e, "launchService");
            return false;
        }
    }

    public static boolean launchServiceAsForegroundIfNeedIt(Context context, Intent intent) {
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
            } else {
                String message = "launchService startService error: " + e.getMessage();
                Timber.e(e, message);
                return false;
            }
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
