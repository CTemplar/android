package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class TokenRefreshRequest {
    @SerializedName("token")
    private String token;

    public TokenRefreshRequest(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
