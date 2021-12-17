package mobileapp.ctemplar.com.ctemplarapp.net.response.domains;

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

    @SerializedName("domain")
    private String domain;

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

    @SerializedName("created")
    private Date created;

    @SerializedName("verified_at")
    private Date verifiedAt;


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

    public String getDomain() {
        return domain;
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

    public Date getCreated() {
        return created;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }
}
