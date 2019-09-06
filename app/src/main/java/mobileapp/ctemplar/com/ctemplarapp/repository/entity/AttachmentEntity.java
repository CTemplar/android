package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import com.google.gson.annotations.SerializedName;

public class AttachmentEntity {

    @SerializedName("id")
    private long id;

    @SerializedName("document_link")
    private String documentLink;

    @SerializedName("is_inline")
    private boolean isInline;

    @SerializedName("content_id")
    private String contentId;

    @SerializedName("message")
    private long message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public boolean isInline() {
        return isInline;
    }

    public void setInline(boolean inline) {
        isInline = inline;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        this.message = message;
    }
}
