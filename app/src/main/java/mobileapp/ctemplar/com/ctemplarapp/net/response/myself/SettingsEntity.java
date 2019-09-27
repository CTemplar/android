package mobileapp.ctemplar.com.ctemplarapp.net.response.Myself;

import com.google.gson.annotations.SerializedName;

public class SettingsEntity {

    @SerializedName("id")
    private Long id;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("language")
    private String language;

    @SerializedName("autoresponder")
    private Boolean isAutoResponder;

    @SerializedName("email_count")
    private Integer emailsCount;

    @SerializedName("emails_per_page")
    private Integer emailsPerPage;

    @SerializedName("domain_count")
    private Integer domainCount;

    @SerializedName("embed_content")
    private boolean isEmbedContent;

    @SerializedName("newsletter")
    private boolean isNewsletter;

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

    @SerializedName("plan_type")
    private String planType;

    @SerializedName("is_subject_encrypted")
    private boolean isSubjectEncrypted;

    @SerializedName("is_attachments_encrypted")
    private boolean isAttachmentsEncrypted;

    @SerializedName("is_contacts_encrypted")
    private boolean isContactsEncrypted;

    @SerializedName("stripe_customer_code")
    private String stripeCustomerCode;

    @SerializedName("allocated_storage")
    private long allocatedStorage;

    @SerializedName("used_storage")
    private long usedStorage;

    @SerializedName("recurrence_billing")
    private boolean recurrenceBilling;

    @SerializedName("anti_phishing_phrase")
    private String antiPhishingPhrase;

    @SerializedName("attachment_size_error")
    private String attachmentSizeError;

    @SerializedName("attachment_size_limit")
    private Integer attachmentSizeLimit;

    @SerializedName("default_font")
    private String defaultFont;

    @SerializedName("e2ee_nonct")
    private Boolean e2nonct;

    @SerializedName("enable_2fa")
    private Boolean enable2FA;

    @SerializedName("enable_forwarding")
    private Boolean enableForwarding;

    @SerializedName("forwarding_address")
    private String forwardingAddress;

    @SerializedName("is_anti_phishing_enabled")
    private Boolean isAntiPhishingEnabled;

    @SerializedName("is_html_disabled")
    private Boolean isHtmlDisabled;

    @SerializedName("notification_email")
    private String notificationEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getAutoResponder() {
        return isAutoResponder;
    }

    public void setAutoResponder(Boolean autoResponder) {
        isAutoResponder = autoResponder;
    }

    public Integer getEmailsCount() {
        return emailsCount;
    }

    public void setEmailsCount(Integer emailsCount) {
        this.emailsCount = emailsCount;
    }

    public Integer getEmailsPerPage() {
        return emailsPerPage;
    }

    public void setEmailsPerPage(Integer emailsPerPage) {
        this.emailsPerPage = emailsPerPage;
    }

    public Integer getDomainCount() {
        return domainCount;
    }

    public void setDomainCount(Integer domainCount) {
        this.domainCount = domainCount;
    }

    public boolean isEmbedContent() {
        return isEmbedContent;
    }

    public void setEmbedContent(boolean embedContent) {
        isEmbedContent = embedContent;
    }

    public boolean isNewsletter() {
        return isNewsletter;
    }

    public void setNewsletter(boolean newsletter) {
        isNewsletter = newsletter;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public boolean isSaveContacts() {
        return saveContacts;
    }

    public void setSaveContacts(boolean saveContacts) {
        this.saveContacts = saveContacts;
    }

    public boolean isShowSnippets() {
        return showSnippets;
    }

    public void setShowSnippets(boolean showSnippets) {
        this.showSnippets = showSnippets;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public boolean isPendingPayment() {
        return isPendingPayment;
    }

    public void setPendingPayment(boolean pendingPayment) {
        isPendingPayment = pendingPayment;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public boolean isSubjectEncrypted() {
        return isSubjectEncrypted;
    }

    public void setSubjectEncrypted(boolean subjectEncrypted) {
        isSubjectEncrypted = subjectEncrypted;
    }

    public boolean isAttachmentsEncrypted() {
        return isAttachmentsEncrypted;
    }

    public void setAttachmentsEncrypted(boolean attachmentsEncrypted) {
        isAttachmentsEncrypted = attachmentsEncrypted;
    }

    public boolean isContactsEncrypted() {
        return isContactsEncrypted;
    }

    public void setContactsEncrypted(boolean contactsEncrypted) {
        isContactsEncrypted = contactsEncrypted;
    }

    public String getStripeCustomerCode() {
        return stripeCustomerCode;
    }

    public void setStripeCustomerCode(String stripeCustomerCode) {
        this.stripeCustomerCode = stripeCustomerCode;
    }

    public long getAllocatedStorage() {
        return allocatedStorage;
    }

    public void setAllocatedStorage(long allocatedStorage) {
        this.allocatedStorage = allocatedStorage;
    }

    public long getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(long usedStorage) {
        this.usedStorage = usedStorage;
    }

    public boolean isRecurrenceBilling() {
        return recurrenceBilling;
    }

    public void setRecurrenceBilling(boolean recurrenceBilling) {
        this.recurrenceBilling = recurrenceBilling;
    }

    public String getAntiPhishingPhrase() {
        return antiPhishingPhrase;
    }

    public void setAntiPhishingPhrase(String antiPhishingPhrase) {
        this.antiPhishingPhrase = antiPhishingPhrase;
    }

    public String getAttachmentSizeError() {
        return attachmentSizeError;
    }

    public void setAttachmentSizeError(String attachmentSizeError) {
        this.attachmentSizeError = attachmentSizeError;
    }

    public Integer getAttachmentSizeLimit() {
        return attachmentSizeLimit;
    }

    public void setAttachmentSizeLimit(Integer attachmentSizeLimit) {
        this.attachmentSizeLimit = attachmentSizeLimit;
    }

    public String getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
    }

    public Boolean getE2nonct() {
        return e2nonct;
    }

    public void setE2nonct(Boolean e2nonct) {
        this.e2nonct = e2nonct;
    }

    public Boolean getEnable2FA() {
        return enable2FA;
    }

    public void setEnable2FA(Boolean enable2FA) {
        this.enable2FA = enable2FA;
    }

    public Boolean getEnableForwarding() {
        return enableForwarding;
    }

    public void setEnableForwarding(Boolean enableForwarding) {
        this.enableForwarding = enableForwarding;
    }

    public String getForwardingAddress() {
        return forwardingAddress;
    }

    public void setForwardingAddress(String forwardingAddress) {
        this.forwardingAddress = forwardingAddress;
    }

    public Boolean getAntiPhishingEnabled() {
        return isAntiPhishingEnabled;
    }

    public void setAntiPhishingEnabled(Boolean antiPhishingEnabled) {
        isAntiPhishingEnabled = antiPhishingEnabled;
    }

    public Boolean getHtmlDisabled() {
        return isHtmlDisabled;
    }

    public void setHtmlDisabled(Boolean htmlDisabled) {
        isHtmlDisabled = htmlDisabled;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }
}
