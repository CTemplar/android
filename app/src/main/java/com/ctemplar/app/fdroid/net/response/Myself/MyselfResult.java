package com.ctemplar.app.fdroid.net.response.myself;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxesResult;

public class MyselfResult {
    @SerializedName("id")
    private long id;

    @SerializedName("username")
    private String username;

    @SerializedName("is_prime")
    private boolean isPrime;

    @SerializedName("is_locked")
    private boolean isLocked;

    @SerializedName("is_trial")
    private boolean isTrial;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("joined_date")
    private Date joinedDate;

    @SerializedName("settings")
    private SettingsResponse settings;

    @SerializedName("mailboxes")
    private MailboxesResult[] mailboxes;

    @SerializedName("blacklist")
    private BlackListContact[] blacklist;

    @SerializedName("whitelist")
    private WhiteListContact[] whitelist;


    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPrime() {
        return isPrime;
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

    public Date getJoinedDate() {
        return joinedDate;
    }

    public SettingsResponse getSettings() {
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
