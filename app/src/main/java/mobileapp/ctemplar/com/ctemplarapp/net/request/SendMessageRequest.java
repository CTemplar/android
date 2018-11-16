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

    @SerializedName("mailbox")
    private int mailbox;

    @SerializedName("receiver")
    private List<String> receivers;

    public SendMessageRequest(String subject, String content, String folder, int mailbox) {
        this.subject = subject;
        this.content = content;
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

    public int getMailbox() {
        return mailbox;
    }

    public void setMailbox(int mailbox) {
        this.mailbox = mailbox;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }
}
