package com.ctemplar.app.fdroid.utils;

import java.util.Locale;

public class UnitUtils {
    public static String memoryDisplay(long volume) {
        double volumeKB = volume / 1024d;
        double volumeMB = volumeKB / 1024d;
        double volumeGB = volumeMB / 1024d;

        if (volumeGB >= 1) {
            return String.format(Locale.getDefault(), "%.2f GB", volumeGB);
        } else if (volumeMB >= 1) {
            return String.format(Locale.getDefault(), "%.2f MB", volumeMB);
        } else if (volumeKB >= 1) {
            return String.format(Locale.getDefault(), "%.2f KB", volumeKB);
        }

        return String.format(Locale.getDefault(), "%d B", volume);
    }
}
