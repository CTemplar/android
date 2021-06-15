package com.ctemplar.app.fdroid.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

public class DefaultMailboxRequest {
    @SerializedName("is_default")
    private boolean isDefault;

    public DefaultMailboxRequest() {
        this.isDefault = true;
    }

    public DefaultMailboxRequest(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
