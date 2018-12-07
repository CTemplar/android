package mobileapp.ctemplar.com.ctemplarapp.net.response.Messages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessagesResult {

    @SerializedName("id")
    private long id;

    @SerializedName("encryption")
    private String encryption;

    @SerializedName("sender")
    private String sender;

    @SerializedName("attachments")
    private List<MessageAttachment> attachments;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("has_children")
    private boolean hasChildren;

    @SerializedName("children_count")
    private int childrenCount;

    @SerializedName("subject")
    private String subject;

    @SerializedName("content")
    private String content;

    @SerializedName("receiver")
    private String[] receivers;

    @SerializedName("cc")
    private String[] cc;

    @SerializedName("bcc")
    private String[] bcc;

    @SerializedName("folder")
    private String folderName;

    @SerializedName("updated")
    private String updated;

    @SerializedName("destruct_date")
    private String destructDate;

    @SerializedName("ic_delayed_delivery")
    private String delayedDelivery;

    @SerializedName("dead_man_duration")
    private String deadManDuration;

    @SerializedName("read")
    private boolean isRead;

    @SerializedName("send")
    private boolean send;

    @SerializedName("starred")
    private boolean isStarred;

    @SerializedName("sent_at")
    private String sentAt;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;

    @SerializedName("is_protected")
    private boolean isProtected;

    @SerializedName("hash")
    private String hash;

    @SerializedName("mailbox")
    private long mailboxId;

    @SerializedName("parent")
    private String parent;

    public long getId() {
        return id;
    }

    public String getEncryption() {
        return encryption;
    }

    public String getSender() {
        return sender;
    }

    public List<MessageAttachment> getAttachments() {
        return attachments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * @return encrypted message
     */
    public String getContent() {
        return content;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public String[] getCC() {
        return cc;
    }

    public String[] getBCC() {
        return bcc;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getUpdated() {
        return updated;
    }

    public String getDestructDate() {
        return destructDate;
    }

    public String getDelayedDelivery() {
        return delayedDelivery;
    }

    public String getDeadManDuration() {
        return deadManDuration;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isSend() {
        return send;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public String getSentAt() {
        return sentAt;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public String getHash() {
        return hash;
    }

    public long getMailboxId() {
        return mailboxId;
    }

    public String getParent() {
        return parent;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }
}
