package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public class DialogUtils {
    public static void showAlertDialog(Context context, @StringRes int titleResourceId, @StringRes int messageResourceId) {
        showAlertDialog(context, titleResourceId, messageResourceId, false);
    }

    public static void showAlertDialog(Context context, @StringRes int titleResourceId, @StringRes int messageResourceId, boolean cancelable) {
        showAlertDialog(context, titleResourceId, messageResourceId, cancelable, null);
    }

    public static void showAlertDialog(Context context, @StringRes int titleResourceId, @StringRes int messageResourceId, boolean cancelable, DialogInterface.OnDismissListener dismissListener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(titleResourceId))
                .setMessage(context.getString(messageResourceId))
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(cancelable)
                .setOnDismissListener(dismissListener)
                .show();
    }
    public static void showInfoDialog(Context context, @StringRes int titleResourceId, @StringRes int messageResourceId, boolean cancelable) {
        showInfoDialog(context, titleResourceId, messageResourceId, cancelable, null);
    }

    public static void showInfoDialog(Context context, @StringRes int titleResourceId, @StringRes int messageResourceId, boolean cancelable, DialogInterface.OnDismissListener dismissListener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(titleResourceId))
                .setMessage(context.getString(messageResourceId))
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(cancelable)
                .setOnDismissListener(dismissListener)
                .show();
    }
}
