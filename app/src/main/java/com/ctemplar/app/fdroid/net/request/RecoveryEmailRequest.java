package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class RecoveryEmailRequest {
    @SerializedName("recovery_email")
    private String recoveryEmail;

    public RecoveryEmailRequest(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }
}
