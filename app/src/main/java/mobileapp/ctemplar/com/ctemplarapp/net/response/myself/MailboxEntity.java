package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

public class MailboxEntity {

    @SerializedName("id")
    public long id;

    @SerializedName("email")
    public String email;

    @SerializedName("is_default")
    public boolean isDefault;

    @SerializedName("is_enabled")
    public boolean isEnabled;

    @SerializedName("fingerprint")
    public String fingerprint;

    @SerializedName("signature")
    public String signature;

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getSignature() {
        return signature;
    }
}
