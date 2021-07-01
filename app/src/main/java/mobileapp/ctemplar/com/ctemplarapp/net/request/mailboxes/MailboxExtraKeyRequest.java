package mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

public class MailboxExtraKeyRequest {
    @SerializedName("mailbox_id")
    private long mailboxId;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("mailbox_key_id")
    private long mailboxKeyId;

    public MailboxExtraKeyRequest() {
    }

    public MailboxExtraKeyRequest(long mailboxId, String privateKey, String publicKey, long mailboxKeyId) {
        this.mailboxId = mailboxId;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.mailboxKeyId = mailboxKeyId;
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

    public void setMailboxKeyId(long mailboxKeyId) {
        this.mailboxKeyId = mailboxKeyId;
    }
}
