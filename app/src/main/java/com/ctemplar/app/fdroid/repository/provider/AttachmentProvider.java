package com.ctemplar.app.fdroid.repository.provider;

import java.io.Serializable;
import java.util.Date;

import com.ctemplar.app.fdroid.net.response.messages.MessageAttachment;

public class AttachmentProvider implements Serializable {
    private long id;
    private long fileSize;
    private String documentUrl;
    private boolean isDeleted;
    private Date deletedAt;
    private String name;
    private boolean isInline;
    private boolean isEncrypted;
    private boolean isForwarded;
    private boolean isPGPMime;
    private String contentId;
    private String fileType;
    private long actualSize;
    private long message;

    // Local object
    private String filePath;

    public AttachmentProvider() {
    }

    public AttachmentProvider(long id, long fileSize, String documentUrl, boolean isDeleted,
                              Date deletedAt, String name, boolean isInline, boolean isEncrypted,
                              boolean isForwarded, boolean isPGPMime, String contentId,
                              String fileType, long actualSize, long message) {
        this.id = id;
        this.fileSize = fileSize;
        this.documentUrl = documentUrl;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.name = name;
        this.isInline = isInline;
        this.isEncrypted = isEncrypted;
        this.isForwarded = isForwarded;
        this.isPGPMime = isPGPMime;
        this.contentId = contentId;
        this.fileType = fileType;
        this.actualSize = actualSize;
        this.message = message;
    }

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static AttachmentProvider fromResponse(MessageAttachment response) {
        AttachmentProvider provider = new AttachmentProvider();
        provider.setId(response.getId());
        provider.setFileSize(response.getFileSize());
        provider.setDocumentUrl(response.getDocumentUrl());
        provider.setDeleted(response.isDeleted());
        provider.setDeletedAt(response.getDeletedAt());
        provider.setName(response.getName());
        provider.setInline(response.isInline());
        provider.setEncrypted(response.isEncrypted());
        provider.setForwarded(response.isForwarded());
        provider.setPGPMime(response.isPGPMime());
        provider.setContentId(response.getContentId());
        provider.setFileType(response.getFileType());
        provider.setActualSize(response.getActualSize());
        provider.setMessage(response.getMessage());
        return provider;
    }
}
