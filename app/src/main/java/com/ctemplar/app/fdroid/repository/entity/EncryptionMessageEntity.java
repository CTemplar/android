package com.ctemplar.app.fdroid.repository.entity;

import java.util.Date;

public class EncryptionMessageEntity {
    private long id;
    private String randomSecret;
    private boolean isDeleted;
    private Date deletedAt;
    private String passwordHint;
    private String privateKey;
    private String publicKey;
    private Date createdAt;
    private Date expires;
    private int expiryHours;
    private long message;
    private String password;

    public EncryptionMessageEntity() {
    }

    public EncryptionMessageEntity(long id, String randomSecret, boolean isDeleted, Date deletedAt, String passwordHint, String privateKey, String publicKey, Date createdAt, Date expires, int expiryHours, long message, String password) {
        this.id = id;
        this.randomSecret = randomSecret;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.passwordHint = passwordHint;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.createdAt = createdAt;
        this.expires = expires;
        this.expiryHours = expiryHours;
        this.message = message;
        this.password = password;
    }

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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public int getExpiryHours() {
        return expiryHours;
    }

    public void setExpiryHours(int expiryHours) {
        this.expiryHours = expiryHours;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
