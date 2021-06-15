package com.ctemplar.app.fdroid.net.request.mailboxes;

import com.google.gson.annotations.SerializedName;

public class UpdateMailboxPrimaryKeyRequest {
    @SerializedName("mailbox_id")
    private long mailboxId;

    @SerializedName("mailboxkey_id")
    private long mailboxkeyId;

    public UpdateMailboxPrimaryKeyRequest() {
    }

    public UpdateMailboxPrimaryKeyRequest(long mailboxId, long mailboxkeyId) {
        this.mailboxId = mailboxId;
        this.mailboxkeyId = mailboxkeyId;
    }

    public void setMailboxId(long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public void setMailboxkeyId(long mailboxkeyId) {
        this.mailboxkeyId = mailboxkeyId;
    }
}
