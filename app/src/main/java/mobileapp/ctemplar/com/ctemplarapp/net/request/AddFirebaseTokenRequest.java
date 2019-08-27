package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class AddFirebaseTokenRequest {

    @SerializedName("token")
    private String token;

    @SerializedName("platform")
    private String platform;

    public AddFirebaseTokenRequest(String token, String platform) {
        this.token = token;
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
