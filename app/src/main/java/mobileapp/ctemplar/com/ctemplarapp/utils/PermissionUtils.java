package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static String[] readManifestPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static String[] readAndWriteManifestPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean readExternalStorage(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean writeExternalStorage(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || isNewSDK();
    }

    public static String[] externalStoragePermissions() {
        return isNewSDK() ? readManifestPermission : readAndWriteManifestPermissions;
    }

    public static boolean isNewSDK() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }

    public static boolean readAndWriteExternalStorage(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, readAndWriteManifestPermissions, 1);
            return false;
        } else {
            return true;
        }
    }

    public static boolean vibrate(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.VIBRATE}, 4);
            return false;
        } else {
            return true;
        }
    }

    public static boolean audioRecord(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
            return false;
        } else {
            return true;
        }
    }

    public static boolean readAndWriteContacts(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 3);
            return false;
        } else {
            return true;
        }
    }
}
