package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}
