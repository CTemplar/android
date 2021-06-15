package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;

@Entity(tableName = "mailbox_keys")
public class MailboxKeyEntity {
    @PrimaryKey
    private final long id;
    private final String privateKey;
    private final String publicKey;
    private final String fingerprint;
    private final boolean isDeleted;
    private final Date deletedAt;
    private final KeyType keyType;

    private final long mailbox;

    public MailboxKeyEntity(
            long id, String privateKey, String publicKey, String fingerprint,
            boolean isDeleted, Date deletedAt, KeyType keyType, long mailbox
    ) {
        this.id = id;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.keyType = keyType;
        this.mailbox = mailbox;
    }

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
