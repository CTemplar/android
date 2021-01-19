package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class CaptchaVerifyResponse {
    @SerializedName("status")
    private boolean status;

    public boolean getStatus() {
        return status;
    }
}
