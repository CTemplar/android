package mobileapp.ctemplar.com.ctemplarapp.net.response.Myself;

import com.google.gson.annotations.SerializedName;

public class SettingsEntity {

    @SerializedName("id")
    public long id;

    @SerializedName("timezone")
    public String timezone;

    @SerializedName("language")
    public String language;

    @SerializedName("autoresponder")
    public boolean isAutoResponder;

    @SerializedName("emails_per_page")
    public int emailsPerPage;

    @SerializedName("embed_content")
    public boolean isEmbedContent;

    @SerializedName("newsletter")
    public boolean isNewsletter;

    @SerializedName("recovery_email")
    public String recoveryEmail;

    @SerializedName("save_contacts")
    public boolean saveContacts;

    @SerializedName("show_snippets")
    public boolean showSnippets;

    @SerializedName("display_name")
    public String displayName;

    @SerializedName("from_address")
    public String fromAddress;

    @SerializedName("redeem_code")
    public String redeemCode;

    @SerializedName("is_pending_payment")
    public boolean isPendingPayment;

    @SerializedName("plan_type")
    public String planType;

    @SerializedName("is_subject_encrypted")
    public boolean isSubjectEncrypted;

    @SerializedName("stripe_customer_code")
    public String stripeCustomerCode;

    @SerializedName("allocated_storage")
    public long allocatedStorage;

    @SerializedName("used_storage")
    public long usedStorage;

    @SerializedName("recurrence_billing")
    public boolean recurrenceBilling;

    @SerializedName("email_count")
    public int emailCount;

    public String getLanguage() {
        return language;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getEmailCount() {
        return emailCount;
    }

    public String getPlanType() {
        return planType;
    }

    public boolean isSubjectEncrypted() {
        return isSubjectEncrypted;
    }

    public long getAllocatedStorage() {
        return allocatedStorage;
    }

    public long getUsedStorage() {
        return usedStorage;
    }

    public long getId() {
        return id;
    }
}
