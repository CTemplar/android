package com.ctemplar.app.fdroid.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

public class DeleteMailboxKeyRequest {
    @SerializedName("password")
    private String password;

    public DeleteMailboxKeyRequest() {
    }

    public DeleteMailboxKeyRequest(String password) {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
