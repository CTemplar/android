package mobileapp.ctemplar.com.ctemplarapp.net.response.Messages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessagesResult {

    @SerializedName("id")
    private long id;

    @SerializedName("encryption")
    private EncryptionMessage encryption;

    @SerializedName("sender")
    private String sender;

    @SerializedName("attachments")
    private List<MessageAttachment> attachments;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("sender_display")
    private UserDisplay senderDisplay;

    @SerializedName("receiver_display")
    private List<UserDisplay> receiverDisplay;

    @SerializedName("cc_display")
    private List<UserDisplay> ccDisplay;

    @SerializedName("bcc_display")
    private List<UserDisplay> bccDisplay;

    @SerializedName("reply_to_display")
    private List<UserDisplay> replyToDisplay;

    @SerializedName("has_children")
    private boolean hasChildren;

    @SerializedName("has_attachments")
    private boolean hasAttachments;

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

    @SerializedName("delayed_delivery")
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

    @SerializedName("is_subject_encrypted")
    private boolean isSubjectEncrypted;

    @SerializedName("is_protected")
    private boolean isProtected;

    @SerializedName("hash")
    private String hash;

    @SerializedName("spam_reason")
    private List<String> spamReason;

    @SerializedName("last_action")
    private String lastAction;

    @SerializedName("last_action_thread")
    private String lastActionThread;

    @SerializedName("last_action_parent_id")
    private long lastActionParentId;

    @SerializedName("children")
    private MessagesResult[] children;

    @SerializedName("mailbox")
    private long mailboxId;

    @SerializedName("parent")
    private String parent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EncryptionMessage getEncryption() {
        return encryption;
    }

    public void setEncryption(EncryptionMessage encryption) {
        this.encryption = encryption;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<MessageAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MessageAttachment> attachments) {
        this.attachments = attachments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserDisplay getSenderDisplay() {
        return senderDisplay;
    }

    public void setSenderDisplay(UserDisplay senderDisplay) {
        this.senderDisplay = senderDisplay;
    }

    public List<UserDisplay> getReceiverDisplay() {
        return receiverDisplay;
    }

    public void setReceiverDisplay(List<UserDisplay> receiverDisplay) {
        this.receiverDisplay = receiverDisplay;
    }

    public List<UserDisplay> getCcDisplay() {
        return ccDisplay;
    }

    public void setCcDisplay(List<UserDisplay> ccDisplay) {
        this.ccDisplay = ccDisplay;
    }

    public List<UserDisplay> getBccDisplay() {
        return bccDisplay;
    }

    public void setBccDisplay(List<UserDisplay> bccDisplay) {
        this.bccDisplay = bccDisplay;
    }

    public List<UserDisplay> getReplyToDisplay() {
        return replyToDisplay;
    }

    public void setReplyToDisplay(List<UserDisplay> replyToDisplay) {
        this.replyToDisplay = replyToDisplay;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getDestructDate() {
        return destructDate;
    }

    public void setDestructDate(String destructDate) {
        this.destructDate = destructDate;
    }

    public String getDelayedDelivery() {
        return delayedDelivery;
    }

    public void setDelayedDelivery(String delayedDelivery) {
        this.delayedDelivery = delayedDelivery;
    }

    public String getDeadManDuration() {
        return deadManDuration;
    }

    public void setDeadManDuration(String deadManDuration) {
        this.deadManDuration = deadManDuration;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isSubjectEncrypted() {
        return isSubjectEncrypted;
    }

    public void setSubjectEncrypted(boolean subjectEncrypted) {
        isSubjectEncrypted = subjectEncrypted;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<String> getSpamReason() {
        return spamReason;
    }

    public void setSpamReason(List<String> spamReason) {
        this.spamReason = spamReason;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getLastActionThread() {
        return lastActionThread;
    }

    public void setLastActionThread(String lastActionThread) {
        this.lastActionThread = lastActionThread;
    }

    public long getLastActionParentId() {
        return lastActionParentId;
    }

    public void setLastActionParentId(long lastActionParentId) {
        this.lastActionParentId = lastActionParentId;
    }

    public MessagesResult[] getChildren() {
        return children;
    }

    public void setChildren(MessagesResult[] children) {
        this.children = children;
    }

    public long getMailboxId() {
        return mailboxId;
    }

    public void setMailboxId(long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
