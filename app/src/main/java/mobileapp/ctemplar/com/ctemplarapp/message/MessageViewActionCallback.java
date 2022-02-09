package mobileapp.ctemplar.com.ctemplarapp.message;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;

public interface MessageViewActionCallback {
    void onDecryptPasswordEncryptedMessageClick(MessageProvider item);

    void onUnsubscribeMailing(long mailboxId, String unsubscribeUrl, String mailto);
}
