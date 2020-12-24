package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class RecoverPasswordResponse {
    @SerializedName("token")
    private String token;


    public String getToken() {
        return token;
    }
}
