package com.ctemplar.app.fdroid.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

@Entity(tableName = "messages")
public class MessageEntity {
    @PrimaryKey
    private long id;
    private String encryption;
    private String sender;
    private boolean hasAttachments;
    private List<AttachmentEntity> attachments;
    private Date createdAt;
    private UserDisplayEntity senderDisplay;
    private List<UserDisplayEntity> receiverDisplayList;
    private List<UserDisplayEntity> ccDisplayList;
    private List<UserDisplayEntity> bccDisplayList;
    private boolean hasChildren;
    private int childrenCount;
    private String subject;
    private String content;
    private List<String> receivers;
    private List<String> cc;
    private List<String> bcc;
    private String folderName;
    private String requestFolder;
    private Date updatedAt;
    private Date destructDate;
    private Date delayedDelivery;
    private Long deadManDuration;
    private boolean isRead;
    private boolean send;
    private boolean isStarred;
    private Date sentAt;
    private boolean isEncrypted;
    private boolean isSubjectEncrypted;
    private boolean isProtected;
    private boolean isHtml;
    private String hash;
    private List<String> spamReason;
    private String lastAction;
    private String lastActionThread;
    private long mailboxId;
    private String parent;

    public MessageEntity() {

    }

    public MessageEntity(long id, String encryption, String sender, boolean hasAttachments, List<AttachmentEntity> attachments, Date createdAt, UserDisplayEntity senderDisplay, List<UserDisplayEntity> receiverDisplayList, List<UserDisplayEntity> ccDisplayList, List<UserDisplayEntity> bccDisplayList, boolean hasChildren, int childrenCount, String subject, String content, List<String> receivers, List<String> cc, List<String> bcc, String folderName, String requestFolder, Date updatedAt, Date destructDate, Date delayedDelivery, Long deadManDuration, boolean isRead, boolean send, boolean isStarred, Date sentAt, boolean isEncrypted, boolean isSubjectEncrypted, boolean isProtected, boolean isHtml, String hash, List<String> spamReason, String lastAction, String lastActionThread, long mailboxId, String parent) {
        this.id = id;
        this.encryption = encryption;
        this.sender = sender;
        this.hasAttachments = hasAttachments;
        this.attachments = attachments;
        this.createdAt = createdAt;
        this.senderDisplay = senderDisplay;
        this.receiverDisplayList = receiverDisplayList;
        this.ccDisplayList = ccDisplayList;
        this.bccDisplayList = bccDisplayList;
        this.hasChildren = hasChildren;
        this.childrenCount = childrenCount;
        this.subject = subject;
        this.content = content;
        this.receivers = receivers;
        this.cc = cc;
        this.bcc = bcc;
        this.folderName = folderName;
        this.requestFolder = requestFolder;
        this.updatedAt = updatedAt;
        this.destructDate = destructDate;
        this.delayedDelivery = delayedDelivery;
        this.deadManDuration = deadManDuration;
        this.isRead = isRead;
        this.send = send;
        this.isStarred = isStarred;
        this.sentAt = sentAt;
        this.isEncrypted = isEncrypted;
        this.isSubjectEncrypted = isSubjectEncrypted;
        this.isProtected = isProtected;
        this.isHtml = isHtml;
        this.hash = hash;
        this.spamReason = spamReason;
        this.lastAction = lastAction;
        this.lastActionThread = lastActionThread;
        this.mailboxId = mailboxId;
        this.parent = parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserDisplayEntity getSenderDisplay() {
        return senderDisplay;
    }

    public void setSenderDisplay(UserDisplayEntity senderDisplay) {
        this.senderDisplay = senderDisplay;
    }

    public List<UserDisplayEntity> getReceiverDisplayList() {
        return receiverDisplayList;
    }

    public void setReceiverDisplayList(List<UserDisplayEntity> receiverDisplayList) {
        this.receiverDisplayList = receiverDisplayList;
    }

    public List<UserDisplayEntity> getCcDisplayList() {
        return ccDisplayList;
    }

    public void setCcDisplayList(List<UserDisplayEntity> ccDisplayList) {
        this.ccDisplayList = ccDisplayList;
    }

    public List<UserDisplayEntity> getBccDisplayList() {
        return bccDisplayList;
    }

    public void setBccDisplayList(List<UserDisplayEntity> bccDisplayList) {
        this.bccDisplayList = bccDisplayList;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
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

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getRequestFolder() {
        return requestFolder;
    }

    public void setRequestFolder(String requestFolder) {
        this.requestFolder = requestFolder;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDestructDate() {
        return destructDate;
    }

    public void setDestructDate(Date destructDate) {
        this.destructDate = destructDate;
    }

    public Date getDelayedDelivery() {
        return delayedDelivery;
    }

    public void setDelayedDelivery(Date delayedDelivery) {
        this.delayedDelivery = delayedDelivery;
    }

    public Long getDeadManDuration() {
        return deadManDuration;
    }

    public void setDeadManDuration(Long deadManDuration) {
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

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
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

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
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
