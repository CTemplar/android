package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;

public class DateUtils {
    private static final String SERVER_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String MAIN_TIME_PATTERN = "h:mm aa";
    private static final String MAIN_DATE_PATTERN = "E, dd MMM yyyy";
    private static final String MAIN_MONTH_PATTERN = "MMM d";
    private static final String MAIN_YEAR_PATTERN = "MMM d, yyyy";

    private static final String MESSAGE_FULL_DATE_PATTERN = "MMM d, yyyy',' h:mm a";
    private static final String EMAIL_PATTERN = "EEE',' MMMM d, yyyy 'at' h:mm a";

    private static final String ELAPSED_TIME_FORMAT = "%2dd %02d:%02d";
    private static final String ELAPSED_TIME_SHORT_FORMAT = "%02d:%02d";

    public static final Gson GENERAL_GSON = new GsonBuilder()
            .setDateFormat(SERVER_DATE_PATTERN)
            .create();

    @NonNull
    public static String displayMessageDate(@Nullable Date date, Resources resources) {
        if (date == null) {
            return "";
        }
        Calendar nowCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        DateFormat timeFormat = new SimpleDateFormat(MAIN_TIME_PATTERN, Locale.getDefault());
        DateFormat monthFormat = new SimpleDateFormat(MAIN_MONTH_PATTERN, Locale.getDefault());
        DateFormat yearFormat = new SimpleDateFormat(MAIN_YEAR_PATTERN, Locale.getDefault());

        if (nowCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            if (nowCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                return timeFormat.format(date);
            } else if (nowCalendar.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 1) {
                return resources.getString(R.string.txt_yesterday);
            } else {
                return monthFormat.format(date);
            }
        }
        return yearFormat.format(date);
    }

    @NonNull
    public static String messageFullDate(@Nullable Date date) {
        if (date == null) {
            return "";
        }
        DateFormat messageFullDateFormat = new SimpleDateFormat(MESSAGE_FULL_DATE_PATTERN, Locale.getDefault());
        return messageFullDateFormat.format(date);
    }

    @Nullable
    public static String elapsedTime(@Nullable Date date) {
        if (date == null) {
            return null;
        }
        long diffInMillis = date.getTime() - new Date().getTime();
        if (diffInMillis < 0) {
            return null;
        }

        long seconds = diffInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        minutes %= 60;
        hours %= 24;

        if (days <= 0) {
            return String.format(Locale.getDefault(), ELAPSED_TIME_SHORT_FORMAT, hours, minutes);
        } else {
            return String.format(Locale.getDefault(), ELAPSED_TIME_FORMAT, days, hours, minutes);
        }
    }

    @Nullable
    public static Date getDeliveryDate(@Nullable MessageProvider messageProvider) {
        if (messageProvider == null) {
            return null;
        }
        return messageProvider.isSend() ? messageProvider.getSentAt() : messageProvider.getCreatedAt();
    }

    @Nullable
    public static String deadMansTime(long hours) {
        if (hours <= 0) {
            return null;
        }

        long days = hours / 24;
        long minutes = hours * 60;
        hours %= 24;
        minutes %= 60;

        if (days <= 0) {
            return String.format(Locale.getDefault(), ELAPSED_TIME_SHORT_FORMAT, hours, minutes);
        } else {
            return String.format(Locale.getDefault(), ELAPSED_TIME_FORMAT, days, hours, minutes);
        }
    }

    @NonNull
    public static String getStringDate(@Nullable Date date) {
        if (date == null) {
            return "";
        }
        DateFormat emailFormat = new SimpleDateFormat(EMAIL_PATTERN, Locale.getDefault());
        return emailFormat.format(date);
    }

    public static String dateFormat(long timeInMillis) {
        DateFormat dateFormat = new SimpleDateFormat(MAIN_DATE_PATTERN, Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String timeFormat(long timeInMillis) {
        DateFormat timeFormat = new SimpleDateFormat(MAIN_TIME_PATTERN, Locale.getDefault());
        return timeFormat.format(timeInMillis);
    }

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
