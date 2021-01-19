package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class CreateAttachmentResponse {
    @SerializedName("id")
    private long id;

    @SerializedName("content_id")
    private long contentId;

    @SerializedName("document")
    private String document;

    @SerializedName("is_inline")
    private boolean isInline;

    @SerializedName("message")
    private long message;

    public long getId() {
        return id;
    }

    public long getContentId() {
        return contentId;
    }

    public String getDocument() {
        return document;
    }

    public boolean isInline() {
        return isInline;
    }

    public long getMessage() {
        return message;
    }
}
