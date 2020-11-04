package com.ctemplar.app.fdroid.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;

import org.jetbrains.annotations.NotNull;

final class FinalThreadHandler extends Handler {
    public FinalThreadHandler(Looper looper) {
        super(looper);
    }
}
