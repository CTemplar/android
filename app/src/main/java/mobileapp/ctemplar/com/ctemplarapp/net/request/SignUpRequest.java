package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class SignUpRequest {

    // plain password in case if user don't want to return to change it
    private transient String password;

    // this is hashed password. It will not override plain password
    @SerializedName("password")
    private String passwordHashed;

    @SerializedName("username")
    private String username;

    @SerializedName("private_key")
    private String private_key;

    @SerializedName("public_key")
    private String public_key;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("recaptcha")
    private String recaptcha = "text_recaptcha";

    @SerializedName("recovery_email")
    private String recovery_email;

    @SerializedName("from_address")
    private String from_address;

    @SerializedName("redeem_code")
    private String redeem_code;

    @SerializedName("stripe_token")
    private String stripe_token;

    @SerializedName("memory")
    private String memory;

    @SerializedName("email_count")
    private String email_count;

    @SerializedName("payment_type")
    private String payment_type;

    @SerializedName("captcha_key")
    private String captcha_key;

    @SerializedName("captcha_value")
    private String captcha_value;

    public void SignUpRequest(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }

    public String getPasswordHashed() {
        return this.passwordHashed;
    }

    public void setRecoveryEmail(String email) {
        this.recovery_email = email;
    }

    public String getRecoveryEmail() {
        return this.recovery_email;
    }

    public void setPrivateKey(String key) {
        this.private_key = key;
    }

    public void setPublicKey(String key) {
        this.public_key = key;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getCaptchaKey() {
        return captcha_key;
    }

    public void setCaptchaKey(String captcha_key) {
        this.captcha_key = captcha_key;
    }

    public String getCaptchaValue() {
        return captcha_value;
    }

    public void setCaptchaValue(String captcha_value) {
        this.captcha_value = captcha_value;
    }
}
