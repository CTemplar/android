package com.ctemplar.app.fdroid.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

import com.ctemplar.app.fdroid.repository.enums.KeyType;

public class CreateMailboxKeyRequest {
    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("password")
    private String password;

    @SerializedName("key_type")
    private KeyType keyType;

    @SerializedName("mailbox")
    private long mailbox;

    public CreateMailboxKeyRequest() {
    }

    public CreateMailboxKeyRequest(
            String privateKey, String publicKey, String fingerprint, String password,
            KeyType keyType, long mailbox
    ) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
        this.password = password;
        this.keyType = keyType;
        this.mailbox = mailbox;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public void setMailbox(long mailbox) {
        this.mailbox = mailbox;
    }
}
