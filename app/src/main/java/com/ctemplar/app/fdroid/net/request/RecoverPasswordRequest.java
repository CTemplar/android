package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class RecoverPasswordRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("recovery_email")
    private String recoveryEmail;

    @SerializedName("code")
    private String code;

    @SerializedName("password")
    private String password;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("fingerprint")
    private String fingerprint = "fingerprint";

    public RecoverPasswordRequest() {

    }

    public RecoverPasswordRequest(String username, String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
