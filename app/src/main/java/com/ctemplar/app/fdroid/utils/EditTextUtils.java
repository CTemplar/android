package com.ctemplar.app.fdroid.utils;

import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextUtils {
    private static final String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final Pattern EMAIL_LIST = Pattern.compile("([, ]{0,}" + EMAIL_PATTERN + ")+");
    private static final Pattern EMAIL_ADDRESS = Pattern.compile(EMAIL_PATTERN);

    public static boolean isEmailListValid(String emailList) {
        Matcher matcher = EMAIL_LIST.matcher(emailList);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        Matcher matcher = EMAIL_ADDRESS.matcher(email);
        return matcher.matches();
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

    public static String getText(EditText editText) {
        Editable editable = editText.getText();
        return editable == null ? "" : editable.toString();
    }

    public static String getText(TextView textView) {
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

    public static String toHtml(Spannable text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.toHtml(text, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            return Html.toHtml(text);
        }
    }

    public static Spanned fromHtml(String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static boolean isHtml(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String fromHtml = fromHtml(text).toString();
        return !text.equals(fromHtml);
    }

    public static boolean isNotEmpty(@Nullable CharSequence str) {
        return str != null && str.length() > 0;
    }
}
