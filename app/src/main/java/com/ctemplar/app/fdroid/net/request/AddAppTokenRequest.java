package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class AddAppTokenRequest {
    @SerializedName("token")
    private String token;

    @SerializedName("platform")
    private String platform;

    public AddAppTokenRequest(String token, String platform) {
        this.token = token;
        this.platform = platform;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
