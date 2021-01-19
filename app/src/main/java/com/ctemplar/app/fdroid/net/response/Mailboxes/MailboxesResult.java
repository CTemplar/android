package com.ctemplar.app.fdroid.net.response.mailboxes;

import com.google.gson.annotations.SerializedName;

public class MailboxesResult {
    @SerializedName("id")
    private long id;

    @SerializedName("email")
    private String email;

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

    @SerializedName("signature")
    private String signature;


    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
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

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getSignature() {
        return signature;
    }
}
