package mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains;

import java.util.Date;

import mobileapp.ctemplar.com.ctemplarapp.net.response.domains.CustomDomainResponse;

public class CustomDomainDTO {
    private int id;
    private DomainRecordDTO verificationRecord;
    private DomainRecordDTO mxRecord;
    private DomainRecordDTO spfRecord;
    private DomainRecordDTO dkimRecord;
    private DomainRecordDTO dmarcRecord;
    private String domain;
    private boolean isDomainVerified;
    private boolean isMxVerified;
    private boolean isSpfVerified;
    private boolean isDkimVerified;
    private boolean isDmarcVerified;
    private Date created;
    private Date verifiedAt;

    public CustomDomainDTO() {
    }

    public CustomDomainDTO(int id, DomainRecordDTO verificationRecord, DomainRecordDTO mxRecord, DomainRecordDTO spfRecord, DomainRecordDTO dkimRecord, DomainRecordDTO dmarcRecord, String domain, boolean isDomainVerified, boolean isMxVerified, boolean isSpfVerified, boolean isDkimVerified, boolean isDmarcVerified, Date created, Date verifiedAt) {
        this.id = id;
        this.verificationRecord = verificationRecord;
        this.mxRecord = mxRecord;
        this.spfRecord = spfRecord;
        this.dkimRecord = dkimRecord;
        this.dmarcRecord = dmarcRecord;
        this.domain = domain;
        this.isDomainVerified = isDomainVerified;
        this.isMxVerified = isMxVerified;
        this.isSpfVerified = isSpfVerified;
        this.isDkimVerified = isDkimVerified;
        this.isDmarcVerified = isDmarcVerified;
        this.created = created;
        this.verifiedAt = verifiedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DomainRecordDTO getVerificationRecord() {
        return verificationRecord;
    }

    public void setVerificationRecord(DomainRecordDTO verificationRecord) {
        this.verificationRecord = verificationRecord;
    }

    public DomainRecordDTO getMxRecord() {
        return mxRecord;
    }

    public void setMxRecord(DomainRecordDTO mxRecord) {
        this.mxRecord = mxRecord;
    }

    public DomainRecordDTO getSpfRecord() {
        return spfRecord;
    }

    public void setSpfRecord(DomainRecordDTO spfRecord) {
        this.spfRecord = spfRecord;
    }

    public DomainRecordDTO getDkimRecord() {
        return dkimRecord;
    }

    public void setDkimRecord(DomainRecordDTO dkimRecord) {
        this.dkimRecord = dkimRecord;
    }

    public DomainRecordDTO getDmarcRecord() {
        return dmarcRecord;
    }

    public void setDmarcRecord(DomainRecordDTO dmarcRecord) {
        this.dmarcRecord = dmarcRecord;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isDomainVerified() {
        return isDomainVerified;
    }

    public void setDomainVerified(boolean domainVerified) {
        isDomainVerified = domainVerified;
    }

    public boolean isMxVerified() {
        return isMxVerified;
    }

    public void setMxVerified(boolean mxVerified) {
        isMxVerified = mxVerified;
    }

    public boolean isSpfVerified() {
        return isSpfVerified;
    }

    public void setSpfVerified(boolean spfVerified) {
        isSpfVerified = spfVerified;
    }

    public boolean isDkimVerified() {
        return isDkimVerified;
    }

    public void setDkimVerified(boolean dkimVerified) {
        isDkimVerified = dkimVerified;
    }

    public boolean isDmarcVerified() {
        return isDmarcVerified;
    }

    public void setDmarcVerified(boolean dmarcVerified) {
        isDmarcVerified = dmarcVerified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Date verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public static CustomDomainDTO get(CustomDomainResponse response) {
        return new CustomDomainDTO(
                response.getId(),
                DomainRecordDTO.get(response.getVerificationRecord()),
                DomainRecordDTO.get(response.getMxRecord()),
                DomainRecordDTO.get(response.getSpfRecord()),
                DomainRecordDTO.get(response.getDkimRecord()),
                DomainRecordDTO.get(response.getDmarcRecord()),
                response.getDomain(),
                response.isDomainVerified(),
                response.isMxVerified(),
                response.isSpfVerified(),
                response.isDkimVerified(),
                response.isDmarcVerified(),
                response.getCreated(),
                response.getVerifiedAt()
        );
    }

    public static CustomDomainDTO[] get(CustomDomainResponse[] arrayResponse) {
        if (arrayResponse == null) {
            return new CustomDomainDTO[0];
        }
        int length = arrayResponse.length;
        CustomDomainDTO[] result = new CustomDomainDTO[length];
        for (int i = 0; i < length; ++i) {
            result[i] = get(arrayResponse[i]);
        }
        return result;
    }
}
