package mobileapp.ctemplar.com.ctemplarapp.net.socket;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import mobileapp.ctemplar.com.ctemplarapp.net.OkHttpClientFactory;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.SocketMessageResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class WebSocketClient extends WebSocketListener {
    private final Gson gson = new GsonBuilder().create();

    private final UserStore userStore;
    private final WebSocketClientCallback callback;
    private final Handler handler;

    private WebSocket socket;
    private boolean safeShutDown;

    public WebSocketClient(UserStore userStore, WebSocketClientCallback callback) {
        this.userStore = userStore;
        this.callback = callback;
        if (Looper.myLooper() == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler = new Handler();
        }
        run();
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

    public void start() {
        if (socket == null) {
            run();
        }
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
        if (socket != null) {
            return;
        }
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
}
