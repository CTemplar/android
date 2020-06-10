package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class ThemeUtils {
    public static void setStatusBarTheme(Activity activity) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP && sdkInt < Build.VERSION_CODES.M) {
            int color = ContextCompat.getColor(activity, R.color.colorDarkBlue);
            activity.getWindow().setStatusBarColor(color);
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
}
