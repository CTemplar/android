package com.ctemplar.app.fdroid.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

public class MailboxKeyRequest {
    @SerializedName("mailbox_id")
    private long mailboxId;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    public MailboxKeyRequest() {
    }

    public MailboxKeyRequest(long mailboxId, String privateKey, String publicKey) {
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
