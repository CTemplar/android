package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.DimenRes;

public class DisplayUtils {
    public static float getDimension(Context context, @DimenRes int resourceId) {
        return context.getResources().getDimension(resourceId);
    }

    public static int getRotation(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
    }
}
