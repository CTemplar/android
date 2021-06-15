package mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes;

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

    public CreateMailboxRequest() {
    }

    public CreateMailboxRequest(String email, String displayName, String privateKey, String publicKey, String fingerprint) {
        this.email = email;
        this.displayName = displayName;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
