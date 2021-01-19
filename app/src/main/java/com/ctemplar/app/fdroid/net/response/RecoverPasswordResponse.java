package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class RecoverPasswordResponse {
    @SerializedName("token")
    private String token;


    public String getToken() {
        return token;
    }
}
