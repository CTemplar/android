package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResult;

public class MyselfResult {

    @SerializedName("id")
    public long id;

    @SerializedName("username")
    public String username;

    @SerializedName("is_prime")
    public boolean isPrime;

    @SerializedName("is_locked")
    public boolean isLocked;

    @SerializedName("is_trial")
    public boolean isTrial;

    @SerializedName("is_deleted")
    public boolean isDeleted;

    @SerializedName("joined_date")
    public String joinedDate;

    @SerializedName("settings")
    public SettingsEntity settings;

    @SerializedName("mailboxes")
    public MailboxesResult[] mailboxes;

    @SerializedName("blacklist")
    public BlackListContact[] blacklist;

    @SerializedName("whitelist")
    public WhiteListContact[] whitelist;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPrime() {
        return isPrime;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public SettingsEntity getSettings() {
        return settings;
    }

    public MailboxesResult[] getMailboxes() {
        return mailboxes;
    }

    public BlackListContact[] getBlacklist() {
        return blacklist;
    }

    public WhiteListContact[] getWhitelist() {
        return whitelist;
    }
}
