package com.ctemplar.app.fdroid.net.response.myself;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

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

    @SerializedName("auto_read")
    private boolean autoRead;

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

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("deleted_at")
    private Date deletedAt;

    @SerializedName("autosave_duration")
    private int autoSaveDuration;

    @SerializedName("notification_bounces")
    private int notificationBounces;

    @SerializedName("enable_copy_forwarding")
    private boolean enableCopyForwarding;

    @SerializedName("is_attachments_encrypted")
    private boolean isAttachmentsEncrypted;

    @SerializedName("include_original_message")
    private boolean includeOriginalMessage;

    @SerializedName("is_composer_full_screen")
    private boolean isComposerFullScreen;

    @SerializedName("is_conversation_mode")
    private boolean isConversationMode;

    @SerializedName("custom_css")
    private boolean customCSS;

    @SerializedName("is_subject_auto_decrypt")
    private boolean isSubjectAutoDecrypt;

    @SerializedName("referral_code")
    private String referralCode;

    @SerializedName("universal_spam_filter")
    private boolean universalSpamFilter;

    @SerializedName("default_color")
    private boolean defaultColor;

    @SerializedName("use_local_cache")
    private String useLocalCache;

    @SerializedName("show_plain_text")
    private boolean showPlainText;

    @SerializedName("is_hard_wrap")
    private boolean isHardWrap;

    @SerializedName("plain_text_font")
    private String plainTextFont;


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

    public boolean isAutoRead() {
        return autoRead;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public int getAutoSaveDuration() {
        return autoSaveDuration;
    }

    public int getNotificationBounces() {
        return notificationBounces;
    }

    public boolean isEnableCopyForwarding() {
        return enableCopyForwarding;
    }

    public boolean isAttachmentsEncrypted() {
        return isAttachmentsEncrypted;
    }

    public boolean isIncludeOriginalMessage() {
        return includeOriginalMessage;
    }

    public boolean isComposerFullScreen() {
        return isComposerFullScreen;
    }

    public boolean isConversationMode() {
        return isConversationMode;
    }

    public boolean isCustomCSS() {
        return customCSS;
    }

    public boolean isSubjectAutoDecrypt() {
        return isSubjectAutoDecrypt;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public boolean isUniversalSpamFilter() {
        return universalSpamFilter;
    }

    public boolean isDefaultColor() {
        return defaultColor;
    }

    public String getUseLocalCache() {
        return useLocalCache;
    }

    public boolean isShowPlainText() {
        return showPlainText;
    }

    public boolean isHardWrap() {
        return isHardWrap;
    }

    public String getPlainTextFont() {
        return plainTextFont;
    }
}
