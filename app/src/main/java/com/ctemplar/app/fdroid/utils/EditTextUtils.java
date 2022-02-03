package com.ctemplar.app.fdroid.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctemplar.app.fdroid.BuildConfig;

public class EditTextUtils {
    private static final String EMAIL_PATTERN_STRING = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_STRING, Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_LIST = Pattern.compile("([, ]{0,}" + EMAIL_PATTERN_STRING + ")+");
    private static final Pattern EMAIL_ADDRESS = Pattern.compile(EMAIL_PATTERN_STRING);

    public static boolean isEmailListValid(String emailList) {
        Matcher matcher = EMAIL_LIST.matcher(emailList);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        Matcher matcher = EMAIL_ADDRESS.matcher(email);
        return matcher.matches();
    }

    public static CharSequence parseInputEmails(CharSequence charSequence) {
        Matcher matcher = EMAIL_PATTERN.matcher(charSequence);
        Set<String> emails = new LinkedHashSet<>();
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        if (emails.isEmpty()) {
            return charSequence;
        }
        return TextUtils.join(",", emails);
    }

    public static String extractUnsubscribeUrl(CharSequence charSequence) {
        Pattern pattern = Pattern.compile("http(s)[^>]+");
        Matcher matcher = pattern.matcher(charSequence);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static boolean isTextValid(String text) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9-_. ]+$");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean isUsernameValid(String text) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+[a-zA-Z0-9-_.]*$");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean isUserEmailValid(String text) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9-_.@]+$");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static String formatUsername(String username) {
        return username.toLowerCase().replace("@" + BuildConfig.DOMAIN, "");
    }

    public static String formatUserEmail(String username) {
        return username + "@" + BuildConfig.DOMAIN;
    }

    @NonNull
    public static String getText(@NonNull EditText editText) {
        Editable editable = editText.getText();
        return editable == null ? "" : editable.toString();
    }

    @NonNull
    public static String getText(@NonNull TextView textView) {
        CharSequence text = textView.getText();
        return text == null ? "" : text.toString();
    }

    public static boolean isTextLength(@Nullable CharSequence charSequence, int min, int max) {
        return charSequence != null && charSequence.length() >= min && charSequence.length() <= max;
    }

    public static List<String> getListFromString(String text) {
        return Arrays.asList(text.split("[, ]+"));
    }

    public static String getStringFromList(List<String> stringList) {
        return TextUtils.join(",", stringList);
    }

    public static String getStringFromList(String[] stringArray) {
        return TextUtils.join(",", stringArray);
    }

    public static boolean isNotEmpty(@Nullable CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static String removeBreaks(String text) {
        return text.replaceAll("\n", "");
    }

    public static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String replaceNullString(String value) {
        return replaceNullString(value, "");
    }

    public static String replaceNullString(String value, String defaultValue) {
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static boolean isIPAddress(String value) {
        return Patterns.IP_ADDRESS.matcher(value).matches();
    }

    public static boolean isPort(int port) {
        return port > 1023 && port < 65536;
    }
}
