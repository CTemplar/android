package mobileapp.ctemplar.com.ctemplarapp.utils;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import timber.log.Timber;

public class DateUtils {
    private static UserStore userStore = CTemplarApp.getUserStore();

    private static final String MAIN_TIME_PATTERN = "h:mm aa";
    private static final String MAIN_DATE_PATTERN = "E, dd MMM yyyy";
    private static final String MAIN_MONTH_PATTERN = "MMM d";
    private static final String MAIN_YEAR_PATTERN = "MMM d, yyyy";

    private static final String MESSAGE_FULL_DATE_PATTERN = "MMM d, yyyy',' h:mm a";
    private static final String EMAIL_PATTERN = "EEE',' MMMM d, yyyy 'at' h:mm a";

    private static final String ELAPSED_TIME_FORMAT = "%2dd %02d:%02d";
    private static final String ELAPSED_TIME_SHORT_FORMAT = "%02d:%02d";

    public static String messageDate(@Nullable Date date) {
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
                return "Yesterday";
            } else {
                return monthFormat.format(date);
            }
        }
        return yearFormat.format(date);
    }

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
            return "";
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

    public static int timezoneOffsetInMillis() {
        TimeZone timeZone = getTimeZone();
        Calendar calendar = GregorianCalendar.getInstance(timeZone);
        return timeZone.getOffset(calendar.getTimeInMillis());
    }

    public static TimeZone getTimeZone() {
        String userTimeZone = userStore.getTimeZone();
        return userTimeZone.isEmpty()
                ? TimeZone.getDefault()
                : TimeZone.getTimeZone(userTimeZone);
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

    public static String convertToServerDatePattern(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(MAIN_DATE_PATTERN, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static String millisToServer(long timeInMillis) {
        DateFormat standardFormat = new SimpleDateFormat(MAIN_DATE_PATTERN, Locale.getDefault());
        standardFormat.setTimeZone(getTimeZone());
        return standardFormat.format(timeInMillis);
    }

    public static Long millisFromServer(String stringDate) {
        DateFormat parseFormat = new SimpleDateFormat(MAIN_DATE_PATTERN, Locale.getDefault());
        try {
            Date parseDate = parseFormat.parse(stringDate);
            return parseDate.getTime();
        } catch (ParseException e) {
            Timber.e(e);
        }
        return null;
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

    public static String getStringDate(@Nullable Date date) {
        if (date == null) {
            return "";
        }
        DateFormat emailFormat = new SimpleDateFormat(EMAIL_PATTERN, Locale.getDefault());
        return emailFormat.format(date);
    }
}
