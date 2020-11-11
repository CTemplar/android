package com.ctemplar.app.fdroid.executor;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

public class HandlerExecutor implements Executor {
    private final Handler handler;

    public HandlerExecutor(Looper looper) {
        this.handler = new FinalThreadHandler(looper);
    }

    public void execute(@NonNull Runnable runnable) {
        this.handler.post(runnable);
    }
}
