package mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

public class MailboxKey {
    @SerializedName("mailbox_id")
    private long mailboxId;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    public MailboxKey() {

    }

    public MailboxKey(long mailboxId, String privateKey, String publicKey) {
        this.mailboxId = mailboxId;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public void setMailboxId(long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
