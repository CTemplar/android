package com.ctemplar.app.fdroid.services;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

import java.util.Map;

public interface NotificationServiceListener {
    void onNewEmail(MessageProvider message);

    void onUpdateUnreadCount(Map<String, Integer> unreadCount);
}
