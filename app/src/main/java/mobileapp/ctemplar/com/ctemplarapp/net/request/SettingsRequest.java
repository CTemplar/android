package mobileapp.ctemplar.com.ctemplarapp.net.request;

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

    public int getEmailsPerPage() {
        return emailsPerPage;
    }

    public void setEmailsPerPage(int emailsPerPage) {
        this.emailsPerPage = emailsPerPage;
    }

    public String getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
    }

    public boolean getEmbedContent() {
        return embedContent;
    }

    public void setEmbedContent(boolean embedContent) {
        this.embedContent = embedContent;
    }

    public boolean getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
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

    public String getStripeCustomerCode() {
        return stripeCustomerCode;
    }

    public void setStripeCustomerCode(String stripeCustomerCode) {
        this.stripeCustomerCode = stripeCustomerCode;
    }

    public int getAllocatedStorage() {
        return allocatedStorage;
    }

    public void setAllocatedStorage(int allocatedStorage) {
        this.allocatedStorage = allocatedStorage;
    }

    public int getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(int usedStorage) {
        this.usedStorage = usedStorage;
    }

    public boolean getRecurrenceBilling() {
        return recurrenceBilling;
    }

    public void setRecurrenceBilling(boolean recurrenceBilling) {
        this.recurrenceBilling = recurrenceBilling;
    }

    public int getEmailCount() {
        return emailCount;
    }

    public void setEmailCount(int emailCount) {
        this.emailCount = emailCount;
    }

    public int getDomainCount() {
        return domainCount;
    }

    public void setDomainCount(int domainCount) {
        this.domainCount = domainCount;
    }

    public boolean isEnableForwarding() {
        return enableForwarding;
    }

    public void setEnableForwarding(boolean enableForwarding) {
        this.enableForwarding = enableForwarding;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public boolean isContactsEncrypted() {
        return isContactsEncrypted;
    }

    public void setContactsEncrypted(boolean contactsEncrypted) {
        isContactsEncrypted = contactsEncrypted;
    }

    public boolean isAttachmentsEncrypted() {
        return isAttachmentsEncrypted;
    }

    public void setAttachmentsEncrypted(boolean attachmentsEncrypted) {
        isAttachmentsEncrypted = attachmentsEncrypted;
    }
}
