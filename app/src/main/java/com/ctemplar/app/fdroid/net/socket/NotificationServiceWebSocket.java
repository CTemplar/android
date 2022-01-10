package com.ctemplar.app.fdroid.net.socket;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ctemplar.app.fdroid.BuildConfig;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.OkHttpClientFactory;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.notification.NotificationMessageResponse;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.sentry.Sentry;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class NotificationServiceWebSocket extends WebSocketListener {
    private static NotificationServiceWebSocket instance;
    private final Gson gson = new GsonBuilder().create();

    private WebSocket socket;
    private NotificationServiceWebSocketCallback callback;
    private boolean safeShutDown;
    private final Handler handler;
    private final UserStore userStore;

    private NotificationServiceWebSocket() {
        if (Looper.myLooper() == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler = new Handler();
        }
        userStore = CTemplarApp.getUserStore();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        NotificationMessageResponse notificationMessageResponse;
        try {
            notificationMessageResponse = gson.fromJson(text, NotificationMessageResponse.class);
        } catch (JsonParseException e) {
            Timber.e(e, "onMessage parse error");
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
        Timber.d("started");
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        Timber.d("closed");
        if (safeShutDown) {
            closeWebSocket(webSocket);
            return;
        }
        reconnect();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        Timber.e(t, "failure");
        Sentry.captureException(t);
        if (response != null && response.code() == 403) {
            shutdown();
            return;
        }
        if (safeShutDown) {
            return;
        }
        if (socket != null && webSocket != socket) {
            return;
        }
        reconnect();
    }

    public void start(NotificationServiceWebSocketCallback callback) {
        this.callback = callback;
        start();
    }

    public void start() {
        if (socket == null) {
            run();
        }
    }

    public void restart() {
        shutdown();
        run();
    }

    private void reconnect() {
        Timber.d("reconnect");
        if (socket != null) {
            closeWebSocket(socket);
            socket = null;
        }
        handler.postDelayed(() -> {
            if (!safeShutDown) {
                run();
            }
        }, 60_000);
    }

    public void shutdown() {
        Timber.d("shutdown");
        safeShutDown = true;
        if (socket == null) {
            return;
        }
        closeWebSocket(socket);
        socket = null;
    }

    private void run() {
        Timber.d("run");
        String token = CTemplarApp.getUserRepository().getUserToken();
        if (TextUtils.isEmpty(token)) {
            Timber.e("User token is null");
            return;
        }
        OkHttpClient client = OkHttpClientFactory.newClient(userStore).newBuilder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.BASE_SOCKET_URL + "connect/?token=" + token)
                .addHeader("Origin", BuildConfig.ORIGIN)
                .build();

        socket = client.newWebSocket(request, this);
    }



    public static NotificationServiceWebSocket getInstance() {
        if (instance == null) {
            instance = new NotificationServiceWebSocket();
        }
        return instance;
    }

    private static void closeWebSocket(WebSocket socket) {
        try {
            socket.close(1000, null);
        } catch (Throwable e) {
            Timber.e(e);
            try {
                socket.cancel();
            } catch (Throwable e1) {
                Timber.e(e1);
            }
        }
    }
}
