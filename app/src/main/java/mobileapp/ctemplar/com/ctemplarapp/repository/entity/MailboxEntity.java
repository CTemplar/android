package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mailboxes")
public class MailboxEntity {
    @PrimaryKey
    public long id;
    public String email;
    public String displayName;
    public boolean isDefault;
    public boolean isEnabled;
    public String privateKey;
    public String publicKey;
    public String fingerprint;
    public String signature;

    public MailboxEntity() {

    }

    public MailboxEntity(long id, String email, String displayName, boolean isDefault, boolean isEnabled, String privateKey, String publicKey, String fingerprint, String signature) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isDefault = isDefault;
        this.isEnabled = isEnabled;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
        this.signature = signature;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
