package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class SubjectEncryptedRequest {

    @SerializedName("is_subject_encrypted")
    private boolean isSubjectEncrypted;

    public SubjectEncryptedRequest(boolean isSubjectEncrypted) {
        this.isSubjectEncrypted = isSubjectEncrypted;
    }
}
