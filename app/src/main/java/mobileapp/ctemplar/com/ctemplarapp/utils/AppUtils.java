package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
import timber.log.Timber;

public class AppUtils {

    private static UserStore userStore = CTemplarApp.getUserStore();

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String LEFT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String VIEW_DATE_PATTERN = "MMM d, yyyy',' h:mm a";
    private static final String EMAIL_PATTERN = "EEE',' MMMM d, yyyy 'at' h:mm a";

    private static final String ELAPSED_TIME_FORMAT = "%2dd %02d:%02d";
    private static final String ELAPSED_TIME_SHORT_FORMAT = "%02d:%02d";

    public static String elapsedTime(String stringDate) {
        if (!TextUtils.isEmpty(stringDate)) {
            Calendar nowCalendar = Calendar.getInstance(getTimeZone());
            nowCalendar.setTimeInMillis(
                    nowCalendar.getTimeInMillis() - timezoneOffsetInMillis()
            );

            DateFormat parseFormat = new SimpleDateFormat(LEFT_DATE_PATTERN, Locale.getDefault());
            parseFormat.setTimeZone(getTimeZone());
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
                Timber.e(e);
            }
        }
        return null;
    }

    public static String messageDate(String stringDate) {
        if (!TextUtils.isEmpty(stringDate)) {
            Calendar nowCalendar = Calendar.getInstance(getTimeZone());
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
                Timber.e(e);
            }
        }
        return null;
    }

    public static String messageViewDate(String stringDate) {
        if (TextUtils.isEmpty(stringDate)) {
            return "";
        }
        DateFormat parseFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        DateFormat viewFormat = new SimpleDateFormat(VIEW_DATE_PATTERN, Locale.getDefault());
        String viewDate = "";
        try {
            Date date = parseFormat.parse(stringDate);
            date.setTime(date.getTime() + timezoneOffsetInMillis());
            viewDate = viewFormat.format(date);
        } catch (ParseException e) {
            Timber.e(e);
        }
        return viewDate;
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

    public static String datetimeForServer(long timeInMillis) {
        DateFormat standardFormat = new SimpleDateFormat(LEFT_DATE_PATTERN, Locale.getDefault());
        standardFormat.setTimeZone(getTimeZone());
        return standardFormat.format(timeInMillis);
    }

    public static Long millisFromServer(String stringDate) {
        DateFormat parseFormat = new SimpleDateFormat(LEFT_DATE_PATTERN, Locale.getDefault());
        try {
            Date parseDate = parseFormat.parse(stringDate);
            return parseDate.getTime();
        } catch (ParseException e) {
            Timber.e(e);
        }
        return null;
    }

    public static String dateFormat(long timeInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault());
        dateFormat.setTimeZone(getTimeZone());
        return dateFormat.format(timeInMillis);
    }

    public static String timeFormat(long timeInMillis) {
        DateFormat timeFormat = new SimpleDateFormat("h:mm aa", Locale.getDefault());
        timeFormat.setTimeZone(getTimeZone());
        return timeFormat.format(timeInMillis);
    }

    public static String usedStorage(long volume) {
        if (volume > 1024) {
            return String.format(Locale.getDefault(), "%.2fMB", ((double) volume / 1024));
        } else {
            return String.format(Locale.getDefault(), "%dKB", volume);
        }
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
            Timber.e(e);
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();
        int lastQMPos = url.lastIndexOf('?');
        int lastHashPos = url.lastIndexOf('#');

        if (lastQMPos == -1) {
            lastQMPos = length;
        }
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        int endIndex = Math.min(lastQMPos, lastHashPos);
        try {
            return URLDecoder.decode(url.substring(startIndex, endIndex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getStringDate(String stringDate) {
        if (stringDate == null) {
            return "";
        }
        DateFormat parseFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        DateFormat viewFormat = new SimpleDateFormat(EMAIL_PATTERN, Locale.getDefault());
        try {
            Date date = parseFormat.parse(stringDate);
            stringDate = viewFormat.format(date);

        } catch (ParseException e) {
            Timber.e("DateParse error: %s", e.getMessage());
        }
        return stringDate;
    }
}
