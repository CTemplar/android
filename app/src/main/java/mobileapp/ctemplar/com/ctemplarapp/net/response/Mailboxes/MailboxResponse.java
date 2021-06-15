package mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;

public class MailboxResponse {
    @SerializedName("id")
    private long id;

    @SerializedName("email")
    private String email;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("deleted_at")
    private Date deletedAt;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("is_enabled")
    private boolean isEnabled;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("sort_order")
    private int sortOrder;

    @SerializedName("signature")
    private String signature;

    @SerializedName("prefer_encrypt")
    private String preferEncrypt;

    @SerializedName("is_autocrypt_enabled")
    private boolean isAutocryptEnabled;

    @SerializedName("key_type")
    private KeyType keyType;


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
