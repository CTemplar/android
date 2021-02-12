package com.ctemplar.app.fdroid.net.socket;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ctemplar.app.fdroid.BuildConfig;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.notification.NotificationMessageResponse;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class NotificationServiceWebSocket extends WebSocketListener {
    private static NotificationServiceWebSocket instance;
    private final Gson gson = new GsonBuilder().create();

    private OkHttpClient client;
    private NotificationServiceWebSocketCallback callback;
    private boolean safeShutDown;
    private final Handler handler;

    private NotificationServiceWebSocket() {
        if (Looper.myLooper() != null) {
            handler = new Handler();
        } else {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        NotificationMessageResponse notificationMessageResponse;
        try {
            notificationMessageResponse = gson.fromJson(text, NotificationMessageResponse.class);
        } catch (JsonParseException e) {
            Timber.w(e, "onMessage parse error");
            return;
        }
        MessagesResult mailResult = notificationMessageResponse.getMail();
        if (mailResult != null) {
            MessageEntity messageEntity = MessageProvider.fromMessagesResultToEntity(mailResult);
            MessageProvider messageProvider = MessageProvider.fromMessageEntity(messageEntity,
                    false, false);
            callback.onNewMessage(messageProvider);
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        Timber.i("is started");
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        Timber.i("is closed");
        if (safeShutDown) {
            webSocket.close(1000, null);
            return;
        }
        reconnect();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        Timber.e(t, "failure");
        if (safeShutDown) {
            return;
        }
        reconnect();
    }

    public void start(NotificationServiceWebSocketCallback callback) {
        this.callback = callback;
        if (client == null) {
            run();
        }
    }

    private void reconnect() {
        Timber.i("reconnect");
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client = null;
        }
        handler.postDelayed(() -> {
            if (!safeShutDown) {
                run();
            }
        }, 30_000);
    }

    public void shutdown() {
        Timber.i("shutdown");
        safeShutDown = true;
        if (client == null) {
            return;
        }
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
        client = null;
    }

    private void run() {
        Timber.i("run");

        String token = CTemplarApp.getUserRepository().getUserToken();
        if (TextUtils.isEmpty(token)) {
            Timber.e("User token is null");
            return;
        }

        client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.BASE_SOCKET_URL + "connect/?token=" + token)
                .addHeader("Origin", BuildConfig.ORIGIN)
                .build();

        client.newWebSocket(request, this);
    }



    public static NotificationServiceWebSocket getInstance() {
        if (instance == null) {
            instance = new NotificationServiceWebSocket();
        }
        return instance;
    }
}
