package mobileapp.ctemplar.com.ctemplarapp.repository.cache;

import java.util.HashMap;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;

public class MessageCacheProvider {
    public static final MessageCacheProvider instance = new MessageCacheProvider();
    private final Map<Long, String> decryptedSubjectsMap = new HashMap<>();

    public String getMessageDecryptedSubject(MessageProvider messageProvider) {
        return decryptedSubjectsMap.get(messageProvider.getId());
    }

    public void setMessageDecryptedSubject(MessageProvider messageProvider) {
        decryptedSubjectsMap.put(messageProvider.getId(), messageProvider.getSubject());
    }
}
