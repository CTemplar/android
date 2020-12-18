package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class CaptchaVerifyResponse {
    @SerializedName("status")
    private boolean status;

    public boolean getStatus() {
        return status;
    }
}
