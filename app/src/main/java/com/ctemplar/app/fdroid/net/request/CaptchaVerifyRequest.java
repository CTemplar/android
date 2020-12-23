package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class CaptchaVerifyRequest {
    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    public CaptchaVerifyRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
