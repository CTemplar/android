package com.ctemplar.app.fdroid.net.response.domains;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CustomDomainResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("verification_record")
    private DomainRecordResponse verificationRecord;

    @SerializedName("mx_record")
    private DomainRecordResponse mxRecord;

    @SerializedName("spf_record")
    private DomainRecordResponse spfRecord;

    @SerializedName("dkim_record")
    private DomainRecordResponse dkimRecord;

    @SerializedName("dmarc_record")
    private DomainRecordResponse dmarcRecord;

    @SerializedName("number_of_users")
    private int numberOfUsers;

    @SerializedName("number_of_aliases")
    private int numberOfAliases;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("deleted_at")
    private Date deletedAt;

    @SerializedName("domain")
    private String domain;

    @SerializedName("ace")
    private String ace;

    @SerializedName("is_domain_verified")
    private boolean isDomainVerified;

    @SerializedName("is_mx_verified")
    private boolean isMxVerified;

    @SerializedName("is_spf_verified")
    private boolean isSpfVerified;

    @SerializedName("is_dkim_verified")
    private boolean isDkimVerified;

    @SerializedName("is_dmarc_verified")
    private boolean isDmarcVerified;

    @SerializedName("catch_all")
    private boolean catchAll;

    @SerializedName("created")
    private Date created;

    @SerializedName("verified_at")
    private Date verifiedAt;

    @SerializedName("catch_all_email")
    private String catchAllEmail;

    public int getId() {
        return id;
    }

    public DomainRecordResponse getVerificationRecord() {
        return verificationRecord;
    }

    public DomainRecordResponse getMxRecord() {
        return mxRecord;
    }

    public DomainRecordResponse getSpfRecord() {
        return spfRecord;
    }

    public DomainRecordResponse getDkimRecord() {
        return dkimRecord;
    }

    public DomainRecordResponse getDmarcRecord() {
        return dmarcRecord;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public int getNumberOfAliases() {
        return numberOfAliases;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getDomain() {
        return domain;
    }

    public String getAce() {
        return ace;
    }

    public boolean isDomainVerified() {
        return isDomainVerified;
    }

    public boolean isMxVerified() {
        return isMxVerified;
    }

    public boolean isSpfVerified() {
        return isSpfVerified;
    }

    public boolean isDkimVerified() {
        return isDkimVerified;
    }

    public boolean isDmarcVerified() {
        return isDmarcVerified;
    }

    public boolean isCatchAll() {
        return catchAll;
    }

    public Date getCreated() {
        return created;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public String getCatchAllEmail() {
        return catchAllEmail;
    }
}
