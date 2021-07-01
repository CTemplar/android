package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AttachmentEntity {
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

    public AttachmentEntity() {
    }

    public AttachmentEntity(long id, long fileSize, String documentUrl, boolean isDeleted, Date deletedAt, String name, boolean isInline, boolean isEncrypted, boolean isForwarded, boolean isPGPMime, String contentId, String fileType, long actualSize, long message) {
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
}
