package com.ctemplar.app.fdroid.repository.provider;

import com.google.gson.annotations.SerializedName;

import com.ctemplar.app.fdroid.net.response.messages.EncryptionMessage;

public class EncryptionMessageProvider {

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


    public static EncryptionMessageProvider fromResponse(EncryptionMessage response) {
        if (response == null) {
            return null;
        }
        EncryptionMessageProvider result = new EncryptionMessageProvider();
        result.setId(response.getId());
        result.setRandomSecret(response.getRandomSecret());
        result.setPassword(response.getPassword());
        result.setPasswordHint(response.getPasswordHint());
        result.setExpireHours(response.getExpireHours());
        result.setPrivateKey(response.getPrivateKey());
        result.setPublicKey(response.getPublicKey());
        result.setCreated(response.getCreated());
        result.setExpires(response.getExpires());
        result.setMessage(response.getMessage());
        return result;
    }

    public EncryptionMessage toRequest() {
        EncryptionMessage request = new EncryptionMessage();
        request.setId(getId());
        request.setRandomSecret(getRandomSecret());
        request.setPassword(getPassword());
        request.setPasswordHint(getPasswordHint());
        request.setExpireHours(getExpireHours());
        request.setPrivateKey(getPrivateKey());
        request.setPublicKey(getPublicKey());
        request.setCreated(getCreated());
        request.setExpires(getExpires());
        request.setMessage(getMessage());
        return request;
    }
}
