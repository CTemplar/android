package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

public class SettingsResponse {
    @SerializedName("id")
    private long id;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("language")
    private String language;

    @SerializedName("autoresponder")
    private boolean isAutoResponder;

    @SerializedName("email_count")
    private int emailsCount;

    @SerializedName("emails_per_page")
    private int emailsPerPage;

    @SerializedName("domain_count")
    private int domainCount;

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

    @SerializedName("is_anti_phishing_enabled")
    private boolean antiPhishingEnabled;

    @SerializedName("anti_phishing_phrase")
    private String antiPhishingPhrase;

    @SerializedName("is_night_mode")
    private boolean isNightMode;

    @SerializedName("is_disable_loading_images")
    private boolean isDisableLoadingImages;

    @SerializedName("attachment_size_error")
    private String attachmentSizeError;

    @SerializedName("attachment_size_limit")
    private int attachmentSizeLimit;

    @SerializedName("default_font")
    private String defaultFont;

    @SerializedName("e2ee_nonct")
    private boolean e2nonct;

    @SerializedName("enable_2fa")
    private boolean enable2FA;

    @SerializedName("enable_forwarding")
    private boolean enableForwarding;

    @SerializedName("forwarding_address")
    private String forwardingAddress;

    @SerializedName("is_html_disabled")
    private boolean isHtmlDisabled;

    @SerializedName("is_enable_report_bugs")
    private boolean isEnableReportBugs;

    @SerializedName("notification_email")
    private String notificationEmail;


    public long getId() {
        return id;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isAutoResponder() {
        return isAutoResponder;
    }

    public int getEmailsCount() {
        return emailsCount;
    }

    public int getEmailsPerPage() {
        return emailsPerPage;
    }

    public int getDomainCount() {
        return domainCount;
    }

    public boolean isEmbedContent() {
        return isEmbedContent;
    }

    public boolean isNewsletter() {
        return isNewsletter;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public boolean isSaveContacts() {
        return saveContacts;
    }

    public boolean isShowSnippets() {
        return showSnippets;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public boolean isPendingPayment() {
        return isPendingPayment;
    }

    public String getPlanType() {
        return planType;
    }

    public boolean isSubjectEncrypted() {
        return isSubjectEncrypted;
    }

    public boolean isContactsEncrypted() {
        return isContactsEncrypted;
    }

    public String getStripeCustomerCode() {
        return stripeCustomerCode;
    }

    public long getAllocatedStorage() {
        return allocatedStorage;
    }

    public long getUsedStorage() {
        return usedStorage;
    }

    public boolean isRecurrenceBilling() {
        return recurrenceBilling;
    }

    public boolean isAntiPhishingEnabled() {
        return antiPhishingEnabled;
    }

    public String getAntiPhishingPhrase() {
        return antiPhishingPhrase;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public boolean isDisableLoadingImages() {
        return isDisableLoadingImages;
    }

    public String getAttachmentSizeError() {
        return attachmentSizeError;
    }

    public int getAttachmentSizeLimit() {
        return attachmentSizeLimit;
    }

    public String getDefaultFont() {
        return defaultFont;
    }

    public boolean isE2nonct() {
        return e2nonct;
    }

    public boolean isEnable2FA() {
        return enable2FA;
    }

    public boolean isEnableForwarding() {
        return enableForwarding;
    }

    public String getForwardingAddress() {
        return forwardingAddress;
    }

    public boolean isHtmlDisabled() {
        return isHtmlDisabled;
    }

    public boolean isEnableReportBugs() {
        return isEnableReportBugs;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }
}
