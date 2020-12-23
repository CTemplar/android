package com.ctemplar.app.fdroid.net.response.messages;

import com.google.gson.annotations.SerializedName;

public class EmptyFolderResponse {
    @SerializedName("detail")
    private String detail;

    @SerializedName("message")
    private String message;

    public String getDetail() {
        return detail;
    }

    public String getMessage() {
        return message;
    }
}
