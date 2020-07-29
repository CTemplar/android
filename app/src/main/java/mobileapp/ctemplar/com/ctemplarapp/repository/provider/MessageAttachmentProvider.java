package mobileapp.ctemplar.com.ctemplarapp.repository.provider;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;

public class MessageAttachmentProvider {

    @SerializedName("id")
    private long id;

    @SerializedName("document")
    private String documentLink;

    @SerializedName("is_inline")
    private boolean isInline;

    @SerializedName("content_id")
    private String contentId;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;

    @SerializedName("message")
    private long message;

    // Local object
    @SerializedName("filePath")
    private String filePath;


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

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        this.message = message;
    }

    @Nullable
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(@Nullable String filePath) {
        this.filePath = filePath;
    }


    public static MessageAttachmentProvider fromResponse(MessageAttachment response) {
        MessageAttachmentProvider result = new MessageAttachmentProvider();
        result.setId(response.getId());
        result.setDocumentLink(response.getDocumentLink());
        result.setInline(response.isInline());
        result.setContentId(response.getContentId());
        result.setEncrypted(response.isEncrypted());
        result.setMessage(response.getMessage());
        return result;
    }

    public MessageAttachment toRequest() {
        MessageAttachment request = new MessageAttachment();
        request.setId(getId());
        request.setDocumentLink(getDocumentLink());
        request.setInline(isInline());
        request.setContentId(getContentId());
        request.setEncrypted(isEncrypted());
        request.setMessage(getMessage());
        return request;
    }
}
