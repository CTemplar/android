package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class SignInResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("status")
    private boolean status;

    @SerializedName("is_2fa_enabled")
    private boolean is2FAEnabled;


    public String getToken() {
        return token;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean is2FAEnabled() {
        return is2FAEnabled;
    }
}
