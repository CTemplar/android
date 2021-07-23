package mobileapp.ctemplar.com.ctemplarapp.billing.model;

import com.google.gson.annotations.SerializedName;

public class PlanData {
    @SerializedName("email_count")
    private final int emailCount;

    @SerializedName("domain_count")
    private final int domainCount;

    @SerializedName("storage")
    private final int storage;

    @SerializedName("monthly_price")
    private final int monthlyPrice;

    @SerializedName("annually_price")
    private final int annuallyPrice;

    @SerializedName("background")
    private final String background;

    @SerializedName("messages_per_day")
    private final String messagesPerDay;

    @SerializedName("gb")
    private final int gb;

    @SerializedName("aliases")
    private final int aliases;

    @SerializedName("custom_domains")
    private final int customDomains;

    @SerializedName("encryption_in_transit")
    private final boolean encryptionInTransit;

    @SerializedName("encryption_at_rest")
    private final boolean encryptionAtRest;

    @SerializedName("encrypted_attachments")
    private final boolean encryptedAttachments;

    @SerializedName("encrypted_content")
    private final boolean encryptedContent;

    @SerializedName("encrypted_contacts")
    private final boolean encryptedContacts;

    @SerializedName("encrypted_subjects")
    private final boolean encryptedSubjects;

    @SerializedName("encrypted_body")
    private final boolean encryptedBody;

    @SerializedName("encrypted_metadata")
    private final boolean encryptedMetadata;

    @SerializedName("two_fa")
    private final boolean twoFa;

    @SerializedName("anti_phishing")
    private final boolean antiPhishing;

    @SerializedName("attachment_upload_limit")
    private final int attachmentUploadLimit;

    @SerializedName("brute_force_protection")
    private final boolean bruteForceProtection;

    @SerializedName("anonymized_ip")
    private final boolean anonymizedIp;

    @SerializedName("remote_encrypted_link")
    private final boolean remoteEncryptedLink;

    @SerializedName("zero_knowledge_password")
    private final boolean zeroKnowledgePassword;

    @SerializedName("strip_ips")
    private final boolean stripIps;

    @SerializedName("sri")
    private final boolean sri;

    @SerializedName("checksums")
    private final boolean checksums;

    @SerializedName("multi_user_support")
    private final boolean multiUserSupport;

    @SerializedName("self_destructing_emails")
    private final boolean selfDestructingEmails;

    @SerializedName("dead_man_timer")
    private final boolean deadManTimer;

    @SerializedName("delayed_delivery")
    private final boolean delayedDelivery;

    @SerializedName("four_data_deletion_methods")
    private final boolean fourDataDeletionMethods;

    @SerializedName("virus_detection_tool")
    private final boolean virusDetectionTool;

    @SerializedName("catch_all_email")
    private final boolean catchAllEmail;

    @SerializedName("unlimited_folders")
    private final boolean unlimitedFolders;

    @SerializedName("exclusive_access")
    private final boolean exclusiveAccess;

    public PlanData(
            int emailCount, int domainCount, int storage, int monthlyPrice,
            int annuallyPrice, String background, String messagesPerDay, int gb,
            int aliases, int customDomains, boolean encryptionInTransit,
            boolean encryptionAtRest, boolean encryptedAttachments, boolean encryptedContent,
            boolean encryptedContacts, boolean encryptedSubjects, boolean encryptedBody,
            boolean encryptedMetadata, boolean twoFa, boolean antiPhishing,
            int attachmentUploadLimit, boolean bruteForceProtection, boolean anonymizedIp,
            boolean remoteEncryptedLink, boolean zeroKnowledgePassword, boolean stripIps,
            boolean sri, boolean checksums, boolean multiUserSupport,
            boolean selfDestructingEmails, boolean deadManTimer, boolean delayedDelivery,
            boolean fourDataDeletionMethods, boolean virusDetectionTool, boolean catchAllEmail,
            boolean unlimitedFolders, boolean exclusiveAccess
    ) {
        this.emailCount = emailCount;
        this.domainCount = domainCount;
        this.storage = storage;
        this.monthlyPrice = monthlyPrice;
        this.annuallyPrice = annuallyPrice;
        this.background = background;
        this.messagesPerDay = messagesPerDay;
        this.gb = gb;
        this.aliases = aliases;
        this.customDomains = customDomains;
        this.encryptionInTransit = encryptionInTransit;
        this.encryptionAtRest = encryptionAtRest;
        this.encryptedAttachments = encryptedAttachments;
        this.encryptedContent = encryptedContent;
        this.encryptedContacts = encryptedContacts;
        this.encryptedSubjects = encryptedSubjects;
        this.encryptedBody = encryptedBody;
        this.encryptedMetadata = encryptedMetadata;
        this.twoFa = twoFa;
        this.antiPhishing = antiPhishing;
        this.attachmentUploadLimit = attachmentUploadLimit;
        this.bruteForceProtection = bruteForceProtection;
        this.anonymizedIp = anonymizedIp;
        this.remoteEncryptedLink = remoteEncryptedLink;
        this.zeroKnowledgePassword = zeroKnowledgePassword;
        this.stripIps = stripIps;
        this.sri = sri;
        this.checksums = checksums;
        this.multiUserSupport = multiUserSupport;
        this.selfDestructingEmails = selfDestructingEmails;
        this.deadManTimer = deadManTimer;
        this.delayedDelivery = delayedDelivery;
        this.fourDataDeletionMethods = fourDataDeletionMethods;
        this.virusDetectionTool = virusDetectionTool;
        this.catchAllEmail = catchAllEmail;
        this.unlimitedFolders = unlimitedFolders;
        this.exclusiveAccess = exclusiveAccess;
    }

    public int getEmailCount() {
        return emailCount;
    }

    public int getDomainCount() {
        return domainCount;
    }

    public int getStorage() {
        return storage;
    }

    public int getMonthlyPrice() {
        return monthlyPrice;
    }

    public int getAnnuallyPrice() {
        return annuallyPrice;
    }

    public String getBackground() {
        return background;
    }

    public String getMessagesPerDay() {
        return messagesPerDay;
    }

    public int getGb() {
        return gb;
    }

    public int getAliases() {
        return aliases;
    }

    public int getCustomDomains() {
        return customDomains;
    }

    public boolean isEncryptionInTransit() {
        return encryptionInTransit;
    }

    public boolean isEncryptionAtRest() {
        return encryptionAtRest;
    }

    public boolean isEncryptedAttachments() {
        return encryptedAttachments;
    }

    public boolean isEncryptedContent() {
        return encryptedContent;
    }

    public boolean isEncryptedContacts() {
        return encryptedContacts;
    }

    public boolean isEncryptedSubjects() {
        return encryptedSubjects;
    }

    public boolean isEncryptedBody() {
        return encryptedBody;
    }

    public boolean isEncryptedMetadata() {
        return encryptedMetadata;
    }

    public boolean isTwoFa() {
        return twoFa;
    }

    public boolean isAntiPhishing() {
        return antiPhishing;
    }

    public int getAttachmentUploadLimit() {
        return attachmentUploadLimit;
    }

    public boolean isBruteForceProtection() {
        return bruteForceProtection;
    }

    public boolean isAnonymizedIp() {
        return anonymizedIp;
    }

    public boolean isRemoteEncryptedLink() {
        return remoteEncryptedLink;
    }

    public boolean isZeroKnowledgePassword() {
        return zeroKnowledgePassword;
    }

    public boolean isStripIps() {
        return stripIps;
    }

    public boolean isSri() {
        return sri;
    }

    public boolean isChecksums() {
        return checksums;
    }

    public boolean isMultiUserSupport() {
        return multiUserSupport;
    }

    public boolean isSelfDestructingEmails() {
        return selfDestructingEmails;
    }

    public boolean isDeadManTimer() {
        return deadManTimer;
    }

    public boolean isDelayedDelivery() {
        return delayedDelivery;
    }

    public boolean isFourDataDeletionMethods() {
        return fourDataDeletionMethods;
    }

    public boolean isVirusDetectionTool() {
        return virusDetectionTool;
    }

    public boolean isCatchAllEmail() {
        return catchAllEmail;
    }

    public boolean isUnlimitedFolders() {
        return unlimitedFolders;
    }

    public boolean isExclusiveAccess() {
        return exclusiveAccess;
    }
}
