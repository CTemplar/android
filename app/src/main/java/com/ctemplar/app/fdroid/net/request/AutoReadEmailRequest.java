package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class AutoReadEmailRequest {
    @SerializedName("auto_read")
    private boolean autoRead;

    public AutoReadEmailRequest(boolean autoRead) {
        this.autoRead = autoRead;
    }

    public void setAutoRead(boolean autoRead) {
        this.autoRead = autoRead;
    }
}
