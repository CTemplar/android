package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class SignInResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("status")
    private boolean status;

    @SerializedName("is_2fa_enabled")
    private boolean is2FAEnabled;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean is2FAEnabled() {
        return is2FAEnabled;
    }

    public void set2FAEnabled(boolean is2FAEnabled) {
        this.is2FAEnabled = is2FAEnabled;
    }
}
