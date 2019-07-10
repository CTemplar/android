package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class CreateMailboxRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("fingerprint")
    private String fingerprint;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
