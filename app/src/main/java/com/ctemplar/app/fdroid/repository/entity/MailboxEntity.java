package com.ctemplar.app.fdroid.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import com.ctemplar.app.fdroid.repository.enums.KeyType;

@Entity(tableName = "mailboxes")
public class MailboxEntity {
    @PrimaryKey
    private final long id;
    private final String email;
    private final boolean isDeleted;
    private final Date deletedAt;
    private final String displayName;
    private final boolean isDefault;
    private final boolean isEnabled;
    private final String privateKey;
    private final String publicKey;
    private final String fingerprint;
    private final int sortOrder;
    private final String signature;
    private final String preferEncrypt;
    private final boolean isAutocryptEnabled;
    private final KeyType keyType;

    public MailboxEntity(
            long id, String email, boolean isDeleted, Date deletedAt, String displayName,
            boolean isDefault, boolean isEnabled, String privateKey, String publicKey,
            String fingerprint, int sortOrder, String signature, String preferEncrypt,
            boolean isAutocryptEnabled, KeyType keyType
    ) {
        this.id = id;
        this.email = email;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.displayName = displayName;
        this.isDefault = isDefault;
        this.isEnabled = isEnabled;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
        this.sortOrder = sortOrder;
        this.signature = signature;
        this.preferEncrypt = preferEncrypt;
        this.isAutocryptEnabled = isAutocryptEnabled;
        this.keyType = keyType;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isEnabled() {
        return isEnabled;
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

    public int getSortOrder() {
        return sortOrder;
    }

    public String getSignature() {
        return signature;
    }

    public String getPreferEncrypt() {
        return preferEncrypt;
    }

    public boolean isAutocryptEnabled() {
        return isAutocryptEnabled;
    }

    public KeyType getKeyType() {
        return keyType;
    }
}
