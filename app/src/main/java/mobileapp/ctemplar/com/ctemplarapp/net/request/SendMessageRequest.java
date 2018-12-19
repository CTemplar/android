package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SendMessageRequest {

    @SerializedName("subject")
    private String subject;

    @SerializedName("content")
    private String content;

    @SerializedName("folder")
    private String folder;

    @SerializedName("send")
    private boolean send;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;

    @SerializedName("parent")
    private Long parent;

    @SerializedName("mailbox")
    private long mailbox;

    @SerializedName("receiver")
    private List<String> receivers;

    public SendMessageRequest(String subject, String content, String folder, boolean send, boolean isEncrypted, long mailbox, Long parent) {
        this.subject = subject;
        this.content = content;
        this.folder = folder;
        this.send = send;
        this.isEncrypted = isEncrypted;
        this.mailbox = mailbox;
        this.parent = parent;
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
}
