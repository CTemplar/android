package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class SignInRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("otp")
    private String otp;

    @SerializedName("rememberMe")
    private boolean rememberMe;

    public SignInRequest(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
