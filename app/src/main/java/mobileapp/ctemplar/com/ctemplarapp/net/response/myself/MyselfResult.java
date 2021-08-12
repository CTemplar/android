package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxResponse;

public class MyselfResult {
    @SerializedName("id")
    private long id;

    @SerializedName("username")
    private String username;

    @SerializedName("is_prime")
    private boolean isPrime;

    @SerializedName("is_lifetime_prime")
    private boolean isLifetimePrime;

    @SerializedName("is_locked")
    private boolean isLocked;

    @SerializedName("is_trial")
    private boolean isTrial;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("email_limit_notice")
    private boolean emailLimitNotice;

    @SerializedName("has_notification")
    private boolean hasNotification;

    @SerializedName("joined_date")
    private Date joinedDate;

    @SerializedName("settings")
    private SettingsResponse settings;

    @SerializedName("mailboxes")
    private MailboxResponse[] mailboxes;

    @SerializedName("blacklist")
    private BlackListContact[] blacklist;

    @SerializedName("whitelist")
    private WhiteListContact[] whitelist;

    @SerializedName("custom_folders")
    private Object customFolders;

    @SerializedName("abuse_warning_count")
    private int abuseWarningCount;

    @SerializedName("payment_transaction")
    private PaymentTransactionResponse paymentTransaction;

    @SerializedName("deleted_at")
    private Date dateAt;

    @SerializedName("user_uuid")
    private String userUUID;


    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPrime() {
        return isPrime;
    }

    public boolean isLifetimePrime() {
        return isLifetimePrime;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isEmailLimitNotice() {
        return emailLimitNotice;
    }

    public boolean isHasNotification() {
        return hasNotification;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public SettingsResponse getSettings() {
        return settings;
    }

    public MailboxResponse[] getMailboxes() {
        return mailboxes;
    }

    public BlackListContact[] getBlacklist() {
        return blacklist;
    }

    public WhiteListContact[] getWhitelist() {
        return whitelist;
    }

    public Object getCustomFolders() {
        return customFolders;
    }

    public int getAbuseWarningCount() {
        return abuseWarningCount;
    }

    public PaymentTransactionResponse getPaymentTransaction() {
        return paymentTransaction;
    }

    public Date getDateAt() {
        return dateAt;
    }

    public String getUserUUID() {
        return userUUID;
    }
}
