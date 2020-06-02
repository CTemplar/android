package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.app.Activity;
import android.os.Build;

import androidx.core.content.ContextCompat;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class ThemeUtils {
    public static void setupStatusBarTheme(Activity activity) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP && sdkInt < Build.VERSION_CODES.M) {
            int color = ContextCompat.getColor(activity, R.color.colorDarkBlue);
            activity.getWindow().setStatusBarColor(color);
        }
    }
}
