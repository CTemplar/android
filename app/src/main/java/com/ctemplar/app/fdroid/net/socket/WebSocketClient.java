package com.ctemplar.app.fdroid.net.socket;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ctemplar.app.fdroid.BuildConfig;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.OkHttpClientFactory;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.messages.SocketMessageResponse;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class WebSocketClient extends WebSocketListener {
    private static WebSocketClient instance;
    private final Gson gson = new GsonBuilder().create();

    private final UserStore userStore;
    private WebSocketClientCallback callback;
    private final Handler handler;

    private WebSocket socket;
    private boolean safeShutDown;

    private WebSocketClient() {
        userStore = CTemplarApp.getUserStore();
        if (Looper.myLooper() == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler = new Handler();
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        if (!text.startsWith("{\"id\"")) {
            Timber.w("onMessage: %s", text);
            return;
        }
        SocketMessageResponse socketMessageResponse;
        try {
            socketMessageResponse = gson.fromJson(text, SocketMessageResponse.class);
        } catch (JsonParseException e) {
            Timber.e(e, "onMessage parse error");
            return;
        }
        Map<String, Integer> unreadCount = socketMessageResponse.getUnreadCount();
        if (unreadCount != null) {
            callback.onUpdateUnreadCount(unreadCount);
        }
        MessagesResult mailResult = socketMessageResponse.getMail();
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

    // If response != null then connection failure, otherwise - read/write failure
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        Timber.e(t, "failure");
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

    public void start(WebSocketClientCallback callback) {
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
        String token = userStore.getUserToken();
        if (TextUtils.isEmpty(token)) {
            Timber.e("User token is null");
            return;
        }
        OkHttpClient client = OkHttpClientFactory.newClient(userStore).newBuilder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .pingInterval(25, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.BASE_SOCKET_URL + "connect/?token=" + token)
                .addHeader("Origin", BuildConfig.ORIGIN)
                .build();

        socket = client.newWebSocket(request, this);
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


    public static WebSocketClient getInstance() {
        if (instance == null) {
            instance = new WebSocketClient();
        }
        return instance;
    }
}
