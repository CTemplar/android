package mobileapp.ctemplar.com.ctemplarapp.net.socket;

import java.util.Map;

public interface WebSocketClientCallback {
    void onUpdateUnreadCount(Map<String, Integer> unreadCount);
}
