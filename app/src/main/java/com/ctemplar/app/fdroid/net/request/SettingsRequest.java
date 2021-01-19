package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class SettingsRequest {
    @SerializedName("timezone")
    private String timezone;

    @SerializedName("language")
    private String language;

    @SerializedName("emails_per_page")
    private int emailsPerPage;

    @SerializedName("default_font")
    private String defaultFont;

    @SerializedName("embed_content")
    private boolean embedContent;

    @SerializedName("newsletter")
    private boolean newsletter;

    @SerializedName("recovery_email")
    private String recoveryEmail;

    @SerializedName("save_contacts")
    private boolean saveContacts;

    @SerializedName("show_snippets")
    private boolean showSnippets;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("from_address")
    private String fromAddress;

    @SerializedName("redeem_code")
    private String redeemCode;

    @SerializedName("is_pending_payment")
    private boolean isPendingPayment;

    @SerializedName("stripe_customer_code")
    private String stripeCustomerCode;

    @SerializedName("allocated_storage")
    private int allocatedStorage;

    @SerializedName("used_storage")
    private int usedStorage;

    @SerializedName("recurrence_billing")
    private boolean recurrenceBilling;

    @SerializedName("email_count")
    private int emailCount;

    @SerializedName("domain_count")
    private int domainCount;

    @SerializedName("enable_forwarding")
    private boolean enableForwarding;

    @SerializedName("plan_type")
    private String planType;

    @SerializedName("is_contacts_encrypted")
    private boolean isContactsEncrypted;

    @SerializedName("is_attachments_encrypted")
    private boolean isAttachmentsEncrypted;

    public SettingsRequest() {

    }

    public SettingsRequest(String timezone, String language, int emailsPerPage, String defaultFont, boolean embedContent, boolean newsletter, String recoveryEmail, boolean saveContacts, boolean showSnippets, String displayName, String fromAddress, String redeemCode, boolean isPendingPayment, String stripeCustomerCode, int allocatedStorage, int usedStorage, boolean recurrenceBilling, int emailCount, int domainCount, boolean enableForwarding, String planType, boolean isContactsEncrypted, boolean isAttachmentsEncrypted) {
        this.timezone = timezone;
        this.language = language;
        this.emailsPerPage = emailsPerPage;
        this.defaultFont = defaultFont;
        this.embedContent = embedContent;
        this.newsletter = newsletter;
        this.recoveryEmail = recoveryEmail;
        this.saveContacts = saveContacts;
        this.showSnippets = showSnippets;
        this.displayName = displayName;
        this.fromAddress = fromAddress;
        this.redeemCode = redeemCode;
        this.isPendingPayment = isPendingPayment;
        this.stripeCustomerCode = stripeCustomerCode;
        this.allocatedStorage = allocatedStorage;
        this.usedStorage = usedStorage;
        this.recurrenceBilling = recurrenceBilling;
        this.emailCount = emailCount;
        this.domainCount = domainCount;
        this.enableForwarding = enableForwarding;
        this.planType = planType;
        this.isContactsEncrypted = isContactsEncrypted;
        this.isAttachmentsEncrypted = isAttachmentsEncrypted;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setEmailsPerPage(int emailsPerPage) {
        this.emailsPerPage = emailsPerPage;
    }

    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
    }

    public void setEmbedContent(boolean embedContent) {
        this.embedContent = embedContent;
    }

    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public void setSaveContacts(boolean saveContacts) {
        this.saveContacts = saveContacts;
    }

    public void setShowSnippets(boolean showSnippets) {
        this.showSnippets = showSnippets;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public void setPendingPayment(boolean pendingPayment) {
        isPendingPayment = pendingPayment;
    }

    public void setStripeCustomerCode(String stripeCustomerCode) {
        this.stripeCustomerCode = stripeCustomerCode;
    }

    public void setAllocatedStorage(int allocatedStorage) {
        this.allocatedStorage = allocatedStorage;
    }

    public void setUsedStorage(int usedStorage) {
        this.usedStorage = usedStorage;
    }

    public void setRecurrenceBilling(boolean recurrenceBilling) {
        this.recurrenceBilling = recurrenceBilling;
    }

    public void setEmailCount(int emailCount) {
        this.emailCount = emailCount;
    }

    public void setDomainCount(int domainCount) {
        this.domainCount = domainCount;
    }

    public void setEnableForwarding(boolean enableForwarding) {
        this.enableForwarding = enableForwarding;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public void setContactsEncrypted(boolean contactsEncrypted) {
        isContactsEncrypted = contactsEncrypted;
    }

    public void setAttachmentsEncrypted(boolean attachmentsEncrypted) {
        isAttachmentsEncrypted = attachmentsEncrypted;
    }
}
