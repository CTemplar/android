package com.ctemplar.app.fdroid.net.response.messages;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MessageAttachment {
    @SerializedName("id")
    private long id;

    @SerializedName("file_size")
    private long fileSize;

    @SerializedName("document")
    private String documentUrl;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("deleted_at")
    private Date deletedAt;

    @SerializedName("name")
    private String name;

    @SerializedName("is_inline")
    private boolean isInline;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;

    @SerializedName("is_forwarded")
    private boolean isForwarded;

    @SerializedName("is_pgp_mime")
    private boolean isPGPMime;

    @SerializedName("content_id")
    private String contentId;

    @SerializedName("file_type")
    private String fileType;

    @SerializedName("actual_size")
    private long actualSize;

    @SerializedName("message")
    private long message;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInline() {
        return isInline;
    }

    public void setInline(boolean inline) {
        isInline = inline;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isForwarded() {
        return isForwarded;
    }

    public void setForwarded(boolean forwarded) {
        isForwarded = forwarded;
    }

    public boolean isPGPMime() {
        return isPGPMime;
    }

    public void setPGPMime(boolean PGPMime) {
        isPGPMime = PGPMime;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getActualSize() {
        return actualSize;
    }

    public void setActualSize(long actualSize) {
        this.actualSize = actualSize;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        this.message = message;
    }
}
