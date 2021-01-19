package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class RecoveryEmailRequest {
    @SerializedName("recovery_email")
    private String recoveryEmail;

    public RecoveryEmailRequest(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }
}
