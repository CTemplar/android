package com.ctemplar.app.fdroid.notification;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

public interface NotificationServiceListener {
    void onNewEmail(MessageProvider message);
}
