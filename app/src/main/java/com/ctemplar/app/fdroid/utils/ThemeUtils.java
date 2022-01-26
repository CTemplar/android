package com.ctemplar.app.fdroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.repository.UserStore;

public class ThemeUtils {
    public static void setTheme(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            int color = ContextCompat.getColor(activity, R.color.colorDarkBlue);
            activity.getWindow().setStatusBarColor(color);
        }
        UserStore userStore = CTemplarApp.getUserStore();
        AppCompatDelegate.setDefaultNightMode(userStore.getDarkModeValue());
        if (userStore.isPINLockEnabled() || userStore.isHideAppPreview()) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    public static void setDarkModeFromServer(boolean value, UserStore userStore) {
        int darkModeValue = userStore.getDarkModeValue();
        if (value) {
            if (darkModeValue != AppCompatDelegate.MODE_NIGHT_YES) {
                userStore.setDarkModeValue(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else {
            if (darkModeValue != AppCompatDelegate.MODE_NIGHT_NO
                    && darkModeValue != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ) {
                userStore.setDarkModeValue(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    }

    public static void setWebViewDarkTheme(Context context, WebView webView) {
        WebSettings webSettings = webView.getSettings();
        int nightModeFlags = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON);
                webView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(webSettings,
                        WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING);
            }
        }
    }

    public static boolean isModeNight(String key) {
        return "on".equals(key);
    }
}
