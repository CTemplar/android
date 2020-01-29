package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextUtils {
    private static final Pattern EMAIL_LIST
            = Pattern.compile(
            "(([, ]?)+" +
            "[a-zA-Z0-9+._%\\-+]{1,256}" +
            "@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+)+"
    );

    public static boolean isEmailListValid(String emailList) {
        Matcher matcher = EMAIL_LIST.matcher(emailList);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isTextValid(String text) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9-_. ]+$");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static String getText(EditText editText) {
        Editable editable = editText.getText();
        return editable == null ? "" : editable.toString();
    }

    public static boolean isTextLength(String text, int min, int max) {
        return text.length() >= min && text.length() <= max;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.toHtml(text, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            return Html.toHtml(text);
        }
    }

    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static boolean isHtml(String text) {
        String fromHtml = fromHtml(text).toString();
        return !text.equals(fromHtml);
    }
}
