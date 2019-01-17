package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class CreateAttachmentResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("content_id")
    private long content_id;

    @SerializedName("document")
    private String document;

    @SerializedName("is_inline")
    private boolean is_inline;

    @SerializedName("message")
    private long message;

    public long getId() {
        return id;
    }

    public long getContent_id() {
        return content_id;
    }

    public String getDocument() {
        return document;
    }

    public boolean isIs_inline() {
        return is_inline;
    }

    public long getMessage() {
        return message;
    }
}
