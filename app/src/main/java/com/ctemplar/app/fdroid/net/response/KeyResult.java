package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class KeyResult {
    @SerializedName("email")
    private String email;

    @SerializedName("is_enabled")
    private boolean isEnabled;

    @SerializedName("public_key")
    private String publicKey;


    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
