package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class MarkMessageAsReadRequest {
    @SerializedName("read")
    private boolean read;

    public MarkMessageAsReadRequest(boolean read) {
        this.read = read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
