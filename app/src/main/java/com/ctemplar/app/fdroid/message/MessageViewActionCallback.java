package com.ctemplar.app.fdroid.message;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

public interface MessageViewActionCallback {
    void onDecryptPasswordEncryptedMessageClick(MessageProvider item);

    void onUnsubscribeMailing(long mailboxId, String unsubscribeUrl, String mailto);
}
