package mobileapp.ctemplar.com.ctemplarapp.net.response.keys;

import com.google.gson.annotations.SerializedName;

import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;

public class KeyResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("is_enabled")
    private boolean isEnabled;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("prefer_encrypt")
    private String preferEncrypt;

    @SerializedName("is_autocrypt_enabled")
    private boolean isAutocryptEnabled;

    @SerializedName("key_type")
    private KeyType keyType;

    @SerializedName("exists")
    private boolean exists;

    @SerializedName("internal")
    private boolean internal;


    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getPublicKey() {
        return publicKey;
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

    public boolean isExists() {
        return exists;
    }

    public boolean isInternal() {
        return internal;
    }
}
