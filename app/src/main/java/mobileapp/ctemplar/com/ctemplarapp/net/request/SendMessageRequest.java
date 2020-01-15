package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.EncryptionMessage;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;

public class SendMessageRequest {

    @SerializedName("encryption")
    private EncryptionMessage encryptionMessage;

    @SerializedName("sender")
    private String sender;

    @SerializedName("subject")
    private String subject;

    @SerializedName("content")
    private String content;

    @SerializedName("receiver")
    private List<String> receivers;

    @SerializedName("cc")
    private List<String> cc;

    @SerializedName("bcc")
    private List<String> bcc;

    @SerializedName("folder")
    private String folder;

    @SerializedName("destruct_date")
    private String destructDate;

    @SerializedName("delayed_delivery")
    private String delayedDelivery;

    @SerializedName("dead_man_duration")
    private Long deadManDuration;

    @SerializedName("send")
    private boolean send;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;

    @SerializedName("is_html")
    private boolean isHtml;

    @SerializedName("is_subject_encrypted")
    private boolean isSubjectEncrypted;

    @SerializedName("mailbox")
    private long mailbox;

    @SerializedName("parent")
    private Long parent;

    @SerializedName("attachments")
    private List<MessageAttachment> attachments;

    public SendMessageRequest() {

    }

    public SendMessageRequest(String sender,
                              String content,
                              ArrayList<String> receivers,
                              ArrayList<String> cc,
                              ArrayList<String> bcc,
                              String folder,
                              long mailbox) {
        this.sender = sender;
        this.content = content;
        this.receivers = receivers;
        this.cc = cc;
        this.bcc = bcc;
        this.folder = folder;
        this.mailbox = mailbox;
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public long getMailbox() {
        return mailbox;
    }

    public void setMailbox(long mailbox) {
        this.mailbox = mailbox;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public boolean isSubjectEncrypted() {
        return isSubjectEncrypted;
    }

    public void setSubjectEncrypted(boolean subjectEncrypted) {
        isSubjectEncrypted = subjectEncrypted;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
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

    public String getDelayedDelivery() {
        return delayedDelivery;
    }

    public void setDelayedDelivery(String delayedDelivery) {
        this.delayedDelivery = delayedDelivery;
    }

    public String getDestructDate() {
        return destructDate;
    }

    public void setDestructDate(String destructDate) {
        this.destructDate = destructDate;
    }

    public Long getDeadManDuration() {
        return deadManDuration;
    }

    public void setDeadManDuration(Long deadManDuration) {
        this.deadManDuration = deadManDuration;
    }

    public EncryptionMessage getEncryptionMessage() {
        return encryptionMessage;
    }

    public void setEncryptionMessage(EncryptionMessage encryptionMessage) {
        this.encryptionMessage = encryptionMessage;
    }
}
