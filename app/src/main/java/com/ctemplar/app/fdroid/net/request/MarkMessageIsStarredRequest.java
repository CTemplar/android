package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class MarkMessageIsStarredRequest {
    @SerializedName("starred")
    private boolean starred;

    public MarkMessageIsStarredRequest(boolean starred) {
        this.starred = starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}
