package com.ctemplar.app.fdroid.net.response.Messages;

import com.google.gson.annotations.SerializedName;

public class EncryptionMessage {

    @SerializedName("id")
    private long id;

    @SerializedName("random_secret")
    private String randomSecret;

    @SerializedName("password")
    private String password;

    @SerializedName("password_hint")
    private String passwordHint;

    @SerializedName("expiry_hours")
    private int expireHours;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("created")
    private String created;

    @SerializedName("expires")
    private String expires;

    @SerializedName("message")
    private long message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRandomSecret() {
        return randomSecret;
    }

    public void setRandomSecret(String randomSecret) {
        this.randomSecret = randomSecret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    public int getExpireHours() {
        return expireHours;
    }

    public void setExpireHours(int expireHours) {
        this.expireHours = expireHours;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        this.message = message;
    }
}
