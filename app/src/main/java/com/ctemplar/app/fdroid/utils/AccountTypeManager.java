package com.ctemplar.app.fdroid.utils;

import android.content.Context;
import android.widget.Toast;

import com.ctemplar.app.fdroid.R;

public class AccountTypeManager {
    private static final int FREE_FOLDERS_COUNT = 5;

    public static boolean createFolder(Context ctx, int foldersCount, boolean isPrime) {
        if (isPrime || foldersCount < FREE_FOLDERS_COUNT) {
            return true;
        } else {
            Toast.makeText(ctx, ctx.getString(R.string.txt_create_folder_free_error), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
