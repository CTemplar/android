package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class DarkModeRequest {
    @SerializedName("is_night_mode")
    private boolean isNightMode;

    public DarkModeRequest(boolean isNightMode) {
        this.isNightMode = isNightMode;
    }

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
    }
}
