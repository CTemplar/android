package mobileapp.ctemplar.com.ctemplarapp.net.response;

import androidx.annotation.Nullable;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;

public class ResponseMessagesData {
    public final List<MessageProvider> messages;
    public final int offset;
    public String folderName;

    public ResponseMessagesData(List<MessageProvider> messages, int offset) {
        this.messages = messages;
        this.offset = offset;
    }

    public ResponseMessagesData(List<MessageProvider> messages, int offset, String folderName) {
        this.messages = messages;
        this.offset = offset;
        this.folderName = folderName;
    }

    public List<MessageProvider> getMessages() {
        return messages;
    }

    public int getOffset() {
        return offset;
    }

    @Nullable
    public String getFolderName() {
        return folderName;
    }
}
