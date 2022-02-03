package com.ctemplar.app.fdroid.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.Button;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.ctemplar.app.fdroid.R;
import timber.log.Timber;

public class DialogUtils {
    public static void showAlertDialog(Context context,
                                       String title,
                                       String message,
                                       String positiveButton,
                                       String negativeButton,
                                       boolean cancelable,
                                       DialogInterface.OnClickListener positiveClick,
                                       DialogInterface.OnClickListener negativeClick,
                                       DialogInterface.OnDismissListener dismissClick) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveButton, positiveClick)
                .setNegativeButton(negativeButton, negativeClick)
                .setOnDismissListener(dismissClick)
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

    public static void showOpenLinkDialog(Context context, String url, boolean isWarnExternalLink) {
        if (url == null || url.isEmpty()) {
            Timber.e("showOpenLinkDialog: url is empty");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (!isWarnExternalLink) {
            context.startActivity(intent);
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_confirm)
                .setMessage(getResourceWithUrl(context, R.string.open_link_description, url))
                .setPositiveButton(R.string.continue_to_link, (dialog, which) -> context.startActivity(intent))
                .setNeutralButton(R.string.btn_cancel, null)
                .show();
        setAlertButtonStyle(context, alertDialog);
    }

    public static void showUnsubscribeMailingDialog(Context context, String url) {
        if (url == null || url.isEmpty()) {
            Timber.e("showUnsubscribeMailingDialog: url is empty");
            return;
        }
        AlertDialog alertDialog =new AlertDialog.Builder(context)
                .setTitle(R.string.unsubscribe)
                .setMessage(getResourceWithUrl(context, R.string.unsubscribe_mailing_description, url))
                .setPositiveButton(R.string.unsubscribe, (dialog, which)
                        -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))))
                .setNeutralButton(R.string.btn_cancel, null)
                .show();
        setAlertButtonStyle(context, alertDialog);
    }

    public static SpannableString getResourceWithUrl(Context context, int resource, String url) {
        SpannableString spannable = new SpannableString(context.getString(resource) + "\n\n" + url);
        Linkify.addLinks(spannable, Linkify.ALL);
        return spannable;
    }

    public static void setAlertButtonStyle(Context context, AlertDialog alertDialog) {
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(true);
        Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setTextColor(context.getResources().getColor(R.color.colorBlue));
        neutralButton.setAllCaps(true);
    }
}
