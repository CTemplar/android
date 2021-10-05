package com.ctemplar.app.fdroid.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.ctemplar.app.fdroid.R;
import timber.log.Timber;

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

    public static void showOpenLinkDialog(Context context, String link) {
        DialogInterface.OnClickListener onPositiveClick = (dialog, which) -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
            } catch (Throwable e) {
                Timber.e(e);
            }
        };
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_confirm)
                .setMessage(R.string.open_link_description)
                .setPositiveButton(R.string.continue_to_link, onPositiveClick)
                .setNegativeButton(R.string.action_cancel, null)
                .show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(true);
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(true);
    }
}
