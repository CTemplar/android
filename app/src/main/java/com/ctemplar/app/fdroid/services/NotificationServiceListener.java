package com.ctemplar.app.fdroid.services;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

public interface NotificationServiceListener {
    void onNewEmail(MessageProvider message);
}
