package com.ctemplar.app.fdroid.net.request;

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
    private String privateKey;

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("recovery_email")
    private String recoveryEmail;

    @SerializedName("from_address")
    private String fromAddress;

    @SerializedName("redeem_code")
    private String redeem_code;

    @SerializedName("stripe_token")
    private String stripeToken;

    @SerializedName("memory")
    private String memory;

    @SerializedName("email_count")
    private String emailCount;

    @SerializedName("payment_type")
    private String paymentType;

    @SerializedName("captcha_key")
    private String captchaKey;

    @SerializedName("captcha_value")
    private String captchaValue;

    @SerializedName("invite_code")
    private String inviteCode;

    @SerializedName("language")
    private String language = "English";

    public SignUpRequest() {

    }

    public void SignUpRequest(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getRedeem_code() {
        return redeem_code;
    }

    public void setRedeem_code(String redeem_code) {
        this.redeem_code = redeem_code;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getEmailCount() {
        return emailCount;
    }

    public void setEmailCount(String emailCount) {
        this.emailCount = emailCount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptchaValue() {
        return captchaValue;
    }

    public void setCaptchaValue(String captchaValue) {
        this.captchaValue = captchaValue;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
