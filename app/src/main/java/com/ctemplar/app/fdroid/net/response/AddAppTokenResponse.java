package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class AddAppTokenResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("platform")
    private String platform;


    public String getToken() {
        return token;
    }

    public String getPlatform() {
        return platform;
    }
}
