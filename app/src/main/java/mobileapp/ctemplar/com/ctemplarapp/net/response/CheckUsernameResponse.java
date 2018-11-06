package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class CheckUsernameResponse {

    @SerializedName("exists")
    Boolean exists;

    public boolean isExists() {
        return exists;
    }
}
