package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

public class PublicKeysRequest {
    @SerializedName("emails")
    private Set<String> emails;

    public PublicKeysRequest(Set<String> emails) {
        this.emails = emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }
}
