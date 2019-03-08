package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class AppUtils {

    private static String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static String LEFT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static String datetimeForServer(long timeInMillis) {
        DateFormat standardFormat = new SimpleDateFormat(LEFT_DATE_PATTERN, Locale.getDefault());
        return standardFormat.format(timeInMillis);
    }

    public static String dateFormat(long timeInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String timeFormat(long timeInMillis) {
        DateFormat timeFormat = new SimpleDateFormat("h:mm aa", Locale.getDefault());
        return timeFormat.format(timeInMillis);
    }

    public static String usedStorage(long volume) {
        if (volume > 1024) {
            return String.format(Locale.getDefault(), "%.2fMB", ((double) volume / 1024));
        } else {
            return String.format(Locale.getDefault(), "%dKB", volume);
        }
    }

    public static String elapsedTime(String stringDate) {
        if (!TextUtils.isEmpty(stringDate)) {
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.add(Calendar.HOUR_OF_DAY, -offsetFromCalendar(nowCalendar));

            DateFormat parseFormat = new SimpleDateFormat(LEFT_DATE_PATTERN, Locale.getDefault());
            String elapsedTimeFormat = "%2dd %02d:%02d";

            try {
                Date parseDate = parseFormat.parse(stringDate);
                long stringTimeInMillis = parseDate.getTime();
                long diffInMillis = stringTimeInMillis - nowCalendar.getTimeInMillis();

                if (diffInMillis < 0) {
                    return null;
                }

                long seconds = diffInMillis / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                minutes %= 60;
                hours %= 24;

                return String.format(Locale.getDefault(), elapsedTimeFormat, days, hours, minutes);
            } catch (ParseException e) {
                Timber.e("DateParse error: %s", e.getMessage());
            }
        }
        return null;
    }

    public static String messageDate(String stringDate) {
        if (!TextUtils.isEmpty(stringDate)) {
            Calendar nowCalendar = Calendar.getInstance();
            DateFormat parseFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
            DateFormat timeFormat = new SimpleDateFormat("h:mm aa", Locale.getDefault());
            DateFormat monthFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
            DateFormat yearFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

            try {
                Date date = parseFormat.parse(stringDate);
                Calendar parseCalendar = parseFormat.getCalendar();

                if (nowCalendar.get(Calendar.YEAR) == parseCalendar.get(Calendar.YEAR)) {
                    if (nowCalendar.get(Calendar.DATE) == parseCalendar.get(Calendar.DATE)) {
                        return timeFormat.format(date); // TODO
                    } else if (nowCalendar.get(Calendar.DATE) - parseCalendar.get(Calendar.DATE) == 1) {
                        return "Yesterday";
                    } else {
                        return monthFormat.format(date);
                    }
                } else {
                    return yearFormat.format(date);
                }
            } catch (ParseException e) {
                Timber.e("DateParse error: %s", e.getMessage());
            }
        }
        return null;
    }

    public static int offsetFromCalendar(Calendar calendar) {
        TimeZone timeZone = calendar.getTimeZone();
        int timeZoneOffset = timeZone.getRawOffset();
        return timeZoneOffset / 1000 / 60 / 60;
    }

    public static String formatDate(String stringDate) {
        if (!TextUtils.isEmpty(stringDate)) {
            DateFormat parseFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
            DateFormat viewFormat = new SimpleDateFormat("h:mm a d.MM.yyyy", Locale.getDefault());
            try {
                Date date = parseFormat.parse(stringDate);
                stringDate = viewFormat.format(date);
                return stringDate;
            } catch (ParseException e) {
                Timber.e("DateParse error: %s", e.getMessage());
            }
        }
        return null;
    }

    public static String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }

        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                return "";
            }
        }  catch(MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }
}
