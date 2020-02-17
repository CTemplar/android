package com.ctemplar.app.fdroid.main;

import java.util.List;

import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

public class ResponseMessagesData {
    public final List<MessageProvider> messages;
    public final String folderName;
    public final int offset;

    public ResponseMessagesData(List<MessageProvider> messages, String folderName, int offset) {
        this.messages = messages;
        this.folderName = folderName;
        this.offset = offset;
    }
}
