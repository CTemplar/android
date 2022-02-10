package com.ctemplar.app.fdroid.repository.dto.invites;

import java.util.Date;

public class InviteCodeDTO {
    private Date expirationDate;
    private String code;
    private boolean used;
    private boolean premium;

    public InviteCodeDTO() {
    }

    public InviteCodeDTO(Date expirationDate, String code, boolean used, boolean premium) {
        this.expirationDate = expirationDate;
        this.code = code;
        this.used = used;
        this.premium = premium;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
