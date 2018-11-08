package mobileapp.ctemplar.com.ctemplarapp.net.response.Messages;

import com.google.gson.annotations.SerializedName;

public class MessageAttachment {

    @SerializedName("id")
    private long id;

    @SerializedName("document")
    private String documentLink;

    @SerializedName("is_inline")
    private boolean isInline;

    @SerializedName("content_id")
    private String contentId;

    @SerializedName("message")
    private long messageId;

    public long getId() {
        return id;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public boolean isInline() {
        return isInline;
    }

    public String getContentId() {
        return contentId;
    }

    public long getMessageId() {
        return messageId;
    }
}
