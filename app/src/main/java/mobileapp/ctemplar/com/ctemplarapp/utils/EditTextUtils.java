package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextUtils {

    public static boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isTextLength(String text, int min, int max) {
        return text.length() >= min && text.length() <= max;
    }

    public static boolean isTextValid(String text) {
        Pattern pattern = Pattern.compile("^[a-z]+[a-z0-9._-]+$");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
