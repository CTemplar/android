package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class AddFirebaseTokenResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("platform")
    private String platform;

    public String getToken() {
        return token;
    }

    public String getPlatform() {
        return platform;
    }
}
