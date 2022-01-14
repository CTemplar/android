package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class IncludeOriginalMessageRequest {
    @SerializedName("include_original_message")
    private boolean includeOriginalMessage;

    public IncludeOriginalMessageRequest(boolean includeOriginalMessage) {
        this.includeOriginalMessage = includeOriginalMessage;
    }

    public void setIncludeOriginalMessage(boolean includeOriginalMessage) {
        this.includeOriginalMessage = includeOriginalMessage;
    }
}
