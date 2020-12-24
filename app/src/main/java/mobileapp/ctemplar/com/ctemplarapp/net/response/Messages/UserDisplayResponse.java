package mobileapp.ctemplar.com.ctemplarapp.net.response.messages;

import com.google.gson.annotations.SerializedName;

public class UserDisplayResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }
}
