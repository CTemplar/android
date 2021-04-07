package mobileapp.ctemplar.com.ctemplarapp.repository.provider;

import androidx.annotation.Nullable;

import java.util.Date;

import mobileapp.ctemplar.com.ctemplarapp.net.request.EncryptionMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.EncryptionMessageResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.EncryptionMessageEntity;

public class EncryptionMessageProvider {
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

    public EncryptionMessageProvider() { }

    public EncryptionMessageProvider(long id, String randomSecret, boolean isDeleted, Date deletedAt, String passwordHint, String privateKey, String publicKey, Date createdAt, Date expires, int expiryHours, long message, String password) {
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

    @Nullable
    public static EncryptionMessageEntity fromResponseToEntity(@Nullable EncryptionMessageResponse response) {
        if (response == null) {
            return null;
        }
        EncryptionMessageEntity entity = new EncryptionMessageEntity();
        entity.setId(response.getId());
        entity.setRandomSecret(response.getRandomSecret());
        entity.setDeleted(response.isDeleted());
        entity.setDeletedAt(response.getDeletedAt());
        entity.setPasswordHint(response.getPasswordHint());
        entity.setPrivateKey(response.getPrivateKey());
        entity.setPublicKey(response.getPublicKey());
        entity.setCreatedAt(response.getCreatedAt());
        entity.setExpires(response.getExpires());
        entity.setExpiryHours(response.getExpiryHours());
        entity.setMessage(response.getMessage());
        entity.setPassword(response.getPassword());
        return entity;
    }

    @Nullable
    public static EncryptionMessageProvider fromEntityToProvider(@Nullable EncryptionMessageEntity entity) {
        if (entity == null) {
            return null;
        }
        EncryptionMessageProvider provider = new EncryptionMessageProvider();
        provider.setId(entity.getId());
        provider.setRandomSecret(entity.getRandomSecret());
        provider.setDeleted(entity.isDeleted());
        provider.setDeletedAt(entity.getDeletedAt());
        provider.setPasswordHint(entity.getPasswordHint());
        provider.setPrivateKey(entity.getPrivateKey());
        provider.setPublicKey(entity.getPublicKey());
        provider.setCreatedAt(entity.getCreatedAt());
        provider.setExpires(entity.getExpires());
        provider.setExpiryHours(entity.getExpiryHours());
        provider.setMessage(entity.getMessage());
        provider.setPassword(entity.getPassword());
        return provider;
    }

    @Nullable
    public static EncryptionMessageProvider fromResponseToProvider(@Nullable EncryptionMessageResponse response) {
        if (response == null) {
            return null;
        }
        EncryptionMessageProvider provider = new EncryptionMessageProvider();
        provider.setId(response.getId());
        provider.setRandomSecret(response.getRandomSecret());
        provider.setDeleted(response.isDeleted());
        provider.setDeletedAt(response.getDeletedAt());
        provider.setPasswordHint(response.getPasswordHint());
        provider.setPrivateKey(response.getPrivateKey());
        provider.setPublicKey(response.getPublicKey());
        provider.setCreatedAt(response.getCreatedAt());
        provider.setExpires(response.getExpires());
        provider.setExpiryHours(response.getExpiryHours());
        provider.setMessage(response.getMessage());
        provider.setPassword(response.getPassword());
        return provider;
    }

    public EncryptionMessageRequest toRequest() {
        EncryptionMessageRequest request = new EncryptionMessageRequest();
        request.setId(id);
        request.setRandomSecret(randomSecret);
        request.setPasswordHint(passwordHint);
        request.setExpiryHours(expiryHours);
        request.setMessage(message);
        request.setPassword(password);
        return request;
    }
}
