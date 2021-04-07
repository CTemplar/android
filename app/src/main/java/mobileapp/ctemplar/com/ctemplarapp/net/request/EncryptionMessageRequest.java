package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class EncryptionMessageRequest {
    @SerializedName("id")
    private long id;

    @SerializedName("random_secret")
    private String randomSecret;

    @SerializedName("password_hint")
    private String passwordHint;

    @SerializedName("expiry_hours")
    private int expiryHours;

    @SerializedName("message")
    private long message;

    private transient String password;

    public EncryptionMessageRequest() { }

    public EncryptionMessageRequest(long id, String randomSecret, String passwordHint, int expiryHours, long message) {
        this.id = id;
        this.randomSecret = randomSecret;
        this.passwordHint = passwordHint;
        this.expiryHours = expiryHours;
        this.message = message;
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

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
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
