package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class RecoverPasswordRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("recovery_email")
    private String recovery_email;

    @SerializedName("code")
    private String code;

    @SerializedName("password")
    private String password;

    @SerializedName("private_key")
    private String private_key;

    @SerializedName("public_key")
    private String public_key;

    @SerializedName("fingerprint")
    private String fingerprint = "fingerprint";

    public RecoverPasswordRequest(String username, String email) {
        this.recovery_email = email;
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return recovery_email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPublicKey(String publicKey) {
        this.public_key = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.private_key = privateKey;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
