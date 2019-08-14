package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class AppUtils {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String LEFT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String ELAPSED_TIME_FORMAT = "%2dd %02d:%02d";
    private static final String ELAPSED_TIME_SHORT_FORMAT = "%02d:%02d";

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
            int offsetInHours = -timezoneOffsetInMillis() / 1000 / 60 / 60;
            nowCalendar.add(Calendar.HOUR_OF_DAY, offsetInHours);

            DateFormat parseFormat = new SimpleDateFormat(LEFT_DATE_PATTERN, Locale.getDefault());

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

                if (days <= 0) {
                    return String.format(Locale.getDefault(), ELAPSED_TIME_SHORT_FORMAT, hours, minutes);
                } else {
                    return String.format(Locale.getDefault(), ELAPSED_TIME_FORMAT, days, hours, minutes);
                }
            } catch (ParseException e) {
                Timber.e("DateParse error: %s", e.getMessage());
            }
        }
        return null;
    }

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
                date.setTime(date.getTime() + timezoneOffsetInMillis());

                if (nowCalendar.get(Calendar.YEAR) == parseCalendar.get(Calendar.YEAR)) {
                    if (nowCalendar.get(Calendar.DATE) == parseCalendar.get(Calendar.DATE)) {
                        return timeFormat.format(date);
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

    public static int timezoneOffsetInMillis() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = GregorianCalendar.getInstance(timeZone);
        return timeZone.getOffset(calendar.getTimeInMillis());
    }

    public static boolean twoWeeksTrial(String stringDate) {
        if (!TextUtils.isEmpty(stringDate)) {
            DateFormat parseFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
            try {
                Date date = parseFormat.parse(stringDate);
                long joinedDate = date.getTime();
                long currentTime = System.currentTimeMillis();
                long timeDifference = (currentTime - joinedDate) / 1000;

                return timeDifference < 14 * 24 * 60 * 60;
            } catch (ParseException e) {
                Timber.e(e);
            }
        }
        return false;
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
