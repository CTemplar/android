package com.ctemplar.app.fdroid.repository.provider;

import java.io.Serializable;

public class AttachmentProvider implements Serializable {
    private long id;
    private String documentLink;
    private boolean isInline;
    private String contentId;
    private boolean isEncrypted;
    private long message;
    private String fileName;

    public AttachmentProvider() {

    }

    public AttachmentProvider(long id, String documentLink, boolean isInline, String contentId, boolean isEncrypted, long message, String fileName) {
        this.id = id;
        this.documentLink = documentLink;
        this.isInline = isInline;
        this.contentId = contentId;
        this.isEncrypted = isEncrypted;
        this.message = message;
        this.fileName = fileName;
    }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
