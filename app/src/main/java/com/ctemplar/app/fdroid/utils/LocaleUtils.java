package com.ctemplar.app.fdroid.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

import com.ctemplar.app.fdroid.CTemplarApp;

public class LocaleUtils extends ContextWrapper {
    public LocaleUtils(Context base) {
        super(base);
    }

    public static ContextWrapper getContextWrapper(Context context) {
        String languageKey = CTemplarApp.getUserStore().getLanguageKey();
        if ("auto".equals(languageKey)) {
            return new ContextWrapper(context);
        }
        Locale newLocale = new Locale(languageKey);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
        } else {
            configuration.locale = newLocale;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Locale.setDefault(newLocale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        } else {
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);
        }

        return new ContextWrapper(context);
    }
}
