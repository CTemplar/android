package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class SubjectEncryptedRequest {

    @SerializedName("is_subject_encrypted")
    private boolean isSubjectEncrypted;

    public SubjectEncryptedRequest(boolean isSubjectEncrypted) {
        this.isSubjectEncrypted = isSubjectEncrypted;
    }
}
