package mobileapp.ctemplar.com.ctemplarapp.executor;

import android.os.Handler;
import android.os.HandlerThread;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class QueuedExecutor implements Executor {
    private static final String TAG = "SimpleExecutor";
    private static int counter;

    private final HandlerThread handlerThread;
    private Handler handler;

    public QueuedExecutor() {
        this(null);
    }

    public QueuedExecutor(String tag) {
        ++counter;
        if (tag == null) {
            tag = TAG + counter;
        }
        handlerThread = new HandlerThread(tag);
        handlerThread.start();
    }

    @Override
    public void execute(@NotNull Runnable command) {
        getThreadHandler().post(command);
    }


    private Handler getThreadHandler() {
        if (handler == null) {
            handler = new Handler(handlerThread.getLooper());
        }
        return handler;
    }
}
