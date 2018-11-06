package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppUtils {

    public static String formatDate(String date) {
        if(!TextUtils.isEmpty(date)) {
            return "DATE";
        }

        return null;
    }
}
