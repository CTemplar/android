package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class CheckUsernameResponse {
    @SerializedName("exists")
    private boolean exists;

    public boolean isExists() {
        return exists;
    }
}
