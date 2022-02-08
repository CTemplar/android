package com.ctemplar.app.fdroid.net.response.invites;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class InviteCodeResponse {
    @SerializedName("expiration_date")
    private Date expirationDate;

    @SerializedName("code")
    private String code;

    @SerializedName("is_used")
    private boolean used;

    @SerializedName("premium")
    private boolean premium;

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getCode() {
        return code;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isPremium() {
        return premium;
    }
}
