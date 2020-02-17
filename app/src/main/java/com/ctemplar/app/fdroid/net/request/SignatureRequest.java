package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class SignatureRequest {

    @SerializedName("signature")
    private String signature;

    public SignatureRequest(String signature) {
        this.signature = signature;
    }
}
