package com.ctemplar.app.fdroid.net.socket;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

import java.util.Map;

public interface WebSocketClientCallback {
    void onNewMessage(MessageProvider messageProvider);

    void onUpdateUnreadCount(Map<String, Integer> unreadCount);
}
