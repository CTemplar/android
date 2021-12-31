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
    private int numberOfUsers;
    private int numberOfAliases;
    private boolean isDeleted;
    private Date deletedAt;
    private String domain;
    private String ace;
    private boolean isDomainVerified;
    private boolean isMxVerified;
    private boolean isSpfVerified;
    private boolean isDkimVerified;
    private boolean isDmarcVerified;
    private boolean catchAll;
    private Date created;
    private Date verifiedAt;
    private String catchAllEmail;

    public CustomDomainDTO() {
    }

    public CustomDomainDTO(int id, DomainRecordDTO verificationRecord, DomainRecordDTO mxRecord, DomainRecordDTO spfRecord, DomainRecordDTO dkimRecord, DomainRecordDTO dmarcRecord, int numberOfUsers, int numberOfAliases, boolean isDeleted, Date deletedAt, String domain, String ace, boolean isDomainVerified, boolean isMxVerified, boolean isSpfVerified, boolean isDkimVerified, boolean isDmarcVerified, boolean catchAll, Date created, Date verifiedAt, String catchAllEmail) {
        this.id = id;
        this.verificationRecord = verificationRecord;
        this.mxRecord = mxRecord;
        this.spfRecord = spfRecord;
        this.dkimRecord = dkimRecord;
        this.dmarcRecord = dmarcRecord;
        this.numberOfUsers = numberOfUsers;
        this.numberOfAliases = numberOfAliases;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.domain = domain;
        this.ace = ace;
        this.isDomainVerified = isDomainVerified;
        this.isMxVerified = isMxVerified;
        this.isSpfVerified = isSpfVerified;
        this.isDkimVerified = isDkimVerified;
        this.isDmarcVerified = isDmarcVerified;
        this.catchAll = catchAll;
        this.created = created;
        this.verifiedAt = verifiedAt;
        this.catchAllEmail = catchAllEmail;
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

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public int getNumberOfAliases() {
        return numberOfAliases;
    }

    public void setNumberOfAliases(int numberOfAliases) {
        this.numberOfAliases = numberOfAliases;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAce() {
        return ace;
    }

    public void setAce(String ace) {
        this.ace = ace;
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

    public boolean isCatchAll() {
        return catchAll;
    }

    public void setCatchAll(boolean catchAll) {
        this.catchAll = catchAll;
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

    public String getCatchAllEmail() {
        return catchAllEmail;
    }

    public void setCatchAllEmail(String catchAllEmail) {
        this.catchAllEmail = catchAllEmail;
    }

    public static CustomDomainDTO get(CustomDomainResponse response) {
        return new CustomDomainDTO(
                response.getId(),
                DomainRecordDTO.get(response.getVerificationRecord()),
                DomainRecordDTO.get(response.getMxRecord()),
                DomainRecordDTO.get(response.getSpfRecord()),
                DomainRecordDTO.get(response.getDkimRecord()),
                DomainRecordDTO.get(response.getDmarcRecord()),
                response.getNumberOfUsers(),
                response.getNumberOfAliases(),
                response.isDeleted(),
                response.getDeletedAt(),
                response.getDomain(),
                response.getAce(),
                response.isDomainVerified(),
                response.isMxVerified(),
                response.isSpfVerified(),
                response.isDkimVerified(),
                response.isDmarcVerified(),
                response.isCatchAll(),
                response.getCreated(),
                response.getVerifiedAt(),
                response.getCatchAllEmail()
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
