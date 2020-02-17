package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class MarkMessageAsReadRequest {

    @SerializedName("read")
    private boolean isRead;

    public MarkMessageAsReadRequest(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
