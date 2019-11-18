package mobileapp.ctemplar.com.ctemplarapp.net.request;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class CheckUsernameRequest {

    @SerializedName("username")
    String username;

    public CheckUsernameRequest(String username) {
        this.username = username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
