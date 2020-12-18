package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PublicKeysRequest {
    @SerializedName("emails")
    private List<String> emails;

    public PublicKeysRequest(List<String> emails) {
        this.emails = emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
