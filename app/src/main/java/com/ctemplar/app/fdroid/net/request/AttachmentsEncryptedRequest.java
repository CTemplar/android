package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class AttachmentsEncryptedRequest {

    @SerializedName("is_attachments_encrypted")
    private boolean isAttachmentsEncrypted;

    public AttachmentsEncryptedRequest(boolean isAttachmentsEncrypted) {
        this.isAttachmentsEncrypted = isAttachmentsEncrypted;
    }
}
