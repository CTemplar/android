package com.ctemplar.app.fdroid.net.socket;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

public interface NotificationServiceWebSocketCallback {
    void onNewMessage(MessageProvider messageProvider);
}
