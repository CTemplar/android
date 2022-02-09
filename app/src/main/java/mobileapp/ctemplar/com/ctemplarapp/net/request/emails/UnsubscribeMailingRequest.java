package mobileapp.ctemplar.com.ctemplarapp.net.request.emails;

import com.google.gson.annotations.SerializedName;

public class UnsubscribeMailingRequest {
    @SerializedName("mailbox_id")
    private long mailboxId;

    @SerializedName("mailto")
    private String mailto;

    public UnsubscribeMailingRequest(long mailboxId, String mailto) {
        this.mailboxId = mailboxId;
        this.mailto = mailto;
    }

    public void setMailboxId(long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public void setMailto(String mailto) {
        this.mailto = mailto;
    }
}
