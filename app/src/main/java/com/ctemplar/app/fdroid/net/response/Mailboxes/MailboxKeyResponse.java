package com.ctemplar.app.fdroid.net.response.mailboxes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import com.ctemplar.app.fdroid.repository.enums.KeyType;

public class MailboxKeyResponse {
    @SerializedName("id")
    private long id;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("deleted_at")
    private Date deletedAt;

    @SerializedName("key_type")
    private KeyType keyType;

    @SerializedName("mailbox")
    private long mailbox;


    public long getId() {
        return id;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public long getMailbox() {
        return mailbox;
    }
}
