package mobileapp.ctemplar.com.ctemplarapp.repository.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.UserDisplay;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.AttachmentEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.UserDisplayEntity;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import timber.log.Timber;

public class MessageProvider {

    private long id;
    private String encryption;
    private String sender;
    private boolean hasAttachments;
    private List<AttachmentProvider> attachments;
    private String createdAt;
    private UserDisplayProvider senderDisplay;
    private List<UserDisplayProvider> receiverDisplayList;
    private List<UserDisplayProvider> ccDisplayList;
    private List<UserDisplayProvider> bccDisplayList;
    private boolean hasChildren;
    private int childrenCount;
    private String subject;
    private String content;
    private String[] receivers;
    private String[] cc;
    private String[] bcc;
    private String folderName;
    private String updated;
    private String destructDate;
    private String delayedDelivery;
    private String deadManDuration;
    private boolean isRead;
    private boolean send;
    private boolean isStarred;
    private String sentAt;
    private boolean isEncrypted;
    private boolean isSubjectEncrypted;
    private boolean isProtected;
    private boolean isHtml;
    private String hash;
    private List<String> spamReason;
    private String lastAction;
    private String lastActionThread;
    private long mailboxId;
    private String parent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public List<AttachmentProvider> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentProvider> attachments) {
        this.attachments = attachments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserDisplayProvider getSenderDisplay() {
        return senderDisplay;
    }

    public void setSenderDisplay(UserDisplayProvider senderDisplay) {
        this.senderDisplay = senderDisplay;
    }

    public List<UserDisplayProvider> getReceiverDisplayList() {
        return receiverDisplayList;
    }

    public void setReceiverDisplayList(List<UserDisplayProvider> receiverDisplayList) {
        this.receiverDisplayList = receiverDisplayList;
    }

    public List<UserDisplayProvider> getCcDisplayList() {
        return ccDisplayList;
    }

    public void setCcDisplayList(List<UserDisplayProvider> ccDisplayList) {
        this.ccDisplayList = ccDisplayList;
    }

    public List<UserDisplayProvider> getBccDisplayList() {
        return bccDisplayList;
    }

    public void setBccDisplayList(List<UserDisplayProvider> bccDisplayList) {
        this.bccDisplayList = bccDisplayList;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getDestructDate() {
        return destructDate;
    }

    public void setDestructDate(String destructDate) {
        this.destructDate = destructDate;
    }

    public String getDelayedDelivery() {
        return delayedDelivery;
    }

    public void setDelayedDelivery(String delayedDelivery) {
        this.delayedDelivery = delayedDelivery;
    }

    public String getDeadManDuration() {
        return deadManDuration;
    }

    public void setDeadManDuration(String deadManDuration) {
        this.deadManDuration = deadManDuration;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isSubjectEncrypted() {
        return isSubjectEncrypted;
    }

    public void setSubjectEncrypted(boolean subjectEncrypted) {
        isSubjectEncrypted = subjectEncrypted;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<String> getSpamReason() {
        return spamReason;
    }

    public void setSpamReason(List<String> spamReason) {
        this.spamReason = spamReason;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getLastActionThread() {
        return lastActionThread;
    }

    public void setLastActionThread(String lastActionThread) {
        this.lastActionThread = lastActionThread;
    }

    public long getMailboxId() {
        return mailboxId;
    }

    public void setMailboxId(long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
//    private MessagesResult[] children;

    private static String decryptContent(String content, long mailboxId, boolean imgDisabled) {
        if (content == null) {
            return "";
        }
        UserStore userStore = CTemplarApp.getUserStore();
        MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
        MailboxEntity mailboxEntity = mailboxDao.getById(mailboxId);
        String password = userStore.getUserPassword();
        if (mailboxEntity != null) {
            String privateKey = mailboxEntity.getPrivateKey();
            content = PGPManager.decrypt(content, privateKey, password);
        }
        return imgDisabled ? content.replaceAll("<img.+?>", "") : content;
    }

    private static String decryptContent(String content, long mailboxId) {
        return decryptContent(content, mailboxId, false);
    }

    private static String decryptSubject(String subject, long mailboxId, boolean isEncrypted) {
        if (isEncrypted) {
            return decryptContent(subject, mailboxId, true);
        } else {
            return subject;
        }
    }

    private static AttachmentProvider convertFromResponseMessageAttachmentToAttachmentProvider(MessageAttachment messageAttachment) {
        AttachmentProvider attachmentProvider = new AttachmentProvider();
        attachmentProvider.setId(messageAttachment.getId());
        attachmentProvider.setContentId(messageAttachment.getContentId());
        attachmentProvider.setDocumentLink(messageAttachment.getDocumentLink());
        attachmentProvider.setInline(messageAttachment.isInline());
        attachmentProvider.setEncrypted(messageAttachment.isEncrypted());
        attachmentProvider.setMessage(messageAttachment.getMessage());
        return attachmentProvider;
    }

    public static List<AttachmentProvider> convertResponseAttachmentsListToProviderList(List<MessageAttachment> messageAttachments) {
        if (messageAttachments == null || messageAttachments.isEmpty()) {
            return Collections.emptyList();
        }

        List<AttachmentProvider> attachmentProviders = new ArrayList<>(messageAttachments.size());
        for (MessageAttachment messageAttachment : messageAttachments) {
            attachmentProviders.add(convertFromResponseMessageAttachmentToAttachmentProvider(messageAttachment));
        }
        return attachmentProviders;
    }

    private static UserDisplayProvider convertUserDisplayFromResponseToProvider(UserDisplay userDisplay) {
        UserDisplayProvider userDisplayProvider = new UserDisplayProvider();
        if (userDisplay != null) {
            userDisplayProvider.setEmail(userDisplay.getEmail());
            userDisplayProvider.setName(userDisplay.getName());
            userDisplayProvider.setEncrypted(userDisplay.isEncrypted());
        }
        return userDisplayProvider;
    }

    public static List<UserDisplayProvider> convertUserDisplayListFromResponseToProvider(List<UserDisplay> userDisplayList) {
        if (userDisplayList == null || userDisplayList.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserDisplayProvider> userDisplayProviderList = new ArrayList<>(userDisplayList.size());
        for (UserDisplay userDisplay : userDisplayList) {
            userDisplayProviderList.add(convertUserDisplayFromResponseToProvider(userDisplay));
        }
        return userDisplayProviderList;
    }

    public static MessageProvider fromMessagesResult(MessagesResult message) {
        MessageProvider result = new MessageProvider();

        result.id = message.getId();
        result.encryption = ""; //TODO
        result.sender = message.getSender();
        result.hasAttachments = message.isHasAttachments();
        result.attachments = convertResponseAttachmentsListToProviderList(message.getAttachments());
        result.createdAt = message.getCreatedAt();
        result.senderDisplay = convertUserDisplayFromResponseToProvider(message.getSenderDisplay());
        result.receiverDisplayList = convertUserDisplayListFromResponseToProvider(message.getReceiverDisplay());
        result.ccDisplayList = convertUserDisplayListFromResponseToProvider(message.getCcDisplay());
        result.bccDisplayList = convertUserDisplayListFromResponseToProvider(message.getBccDisplay());
        result.hasChildren = message.isHasChildren();
        result.childrenCount = message.getChildrenCount();
        result.subject = decryptSubject(message.getSubject(), message.getMailboxId(), message.isSubjectEncrypted());
        result.content = decryptContent(message.getContent(), message.getMailboxId());
        result.receivers = message.getReceivers();
        result.cc = message.getCc();
        result.bcc = message.getBcc();
        result.folderName = message.getFolderName();
        result.updated = message.getUpdated();
        result.destructDate = message.getDestructDate();
        result.delayedDelivery = message.getDelayedDelivery();
        result.deadManDuration = message.getDeadManDuration();
        result.isRead = message.isRead();
        result.send = message.isSend();
        result.isStarred = message.isStarred();
        result.sentAt = message.getSentAt();
        result.isEncrypted = message.isEncrypted();
        result.isSubjectEncrypted = message.isSubjectEncrypted();
        result.isProtected = message.isProtected();
        result.isHtml = message.isHtml();
        result.hash = message.getHash();
        result.spamReason = message.getSpamReason();
        result.lastAction = message.getLastAction();
        result.lastActionThread = message.getLastActionThread();
        result.mailboxId = message.getMailboxId();
        result.parent = message.getParent();

        return result;
    }

    public static List<MessageProvider> fromMessagesResults(List<MessagesResult> messages) {
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }
        List<MessageProvider> result = new ArrayList<>(messages.size());
        for (MessagesResult message : messages) {
            result.add(MessageProvider.fromMessagesResult(message));
        }
        return result;
    }

    private static String[] listToArray(List<String> list) {
        if (list == null) {
            return new String[0];
        }
        String[] array = new String[list.size()];
        for(int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static List<String> arrayToList(String[] array) {
        if (array == null) {
            return new ArrayList<>(0);
        }
        return Arrays.asList(array);
    }

    private static AttachmentProvider convertAttachmentFromEntityToProvider(AttachmentEntity attachmentEntity) {
        AttachmentProvider attachmentProvider = new AttachmentProvider();
        attachmentProvider.setId(attachmentEntity.getId());
        attachmentProvider.setContentId(attachmentEntity.getContentId());
        attachmentProvider.setMessage(attachmentEntity.getMessage());
        attachmentProvider.setInline(attachmentEntity.isInline());
        attachmentProvider.setEncrypted(attachmentEntity.isEncrypted());
        attachmentProvider.setDocumentLink(attachmentEntity.getDocumentLink());
        return attachmentProvider;
    }

    private static List<AttachmentProvider> convertAttachmentsListFromEntityToProvider(List<AttachmentEntity> attachmentEntities) {
        if (attachmentEntities == null || attachmentEntities.isEmpty()) {
            return Collections.emptyList();
        }

        List<AttachmentProvider> attachmentProviders = new ArrayList<>(attachmentEntities.size());
        for (AttachmentEntity attachmentEntity : attachmentEntities) {
            attachmentProviders.add(convertAttachmentFromEntityToProvider(attachmentEntity));
        }
        return attachmentProviders;
    }

    private static UserDisplayProvider convertUserDisplayFromEntityToProvider(UserDisplayEntity userDisplayEntity) {
        UserDisplayProvider userDisplayProvider = new UserDisplayProvider();
        if (userDisplayEntity != null) {
            userDisplayProvider.setName(userDisplayEntity.getName());
            userDisplayProvider.setEmail(userDisplayEntity.getEmail());
            userDisplayProvider.setEncrypted(userDisplayEntity.isEncrypted());
        }
        return userDisplayProvider;
    }

    public static List<UserDisplayProvider> convertUserDisplayListFromEntityToProvider(List<UserDisplayEntity> userDisplayEntityList) {
        if (userDisplayEntityList == null || userDisplayEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserDisplayProvider> userDisplayProviderList = new ArrayList<>(userDisplayEntityList.size());
        for (UserDisplayEntity userDisplayEntity : userDisplayEntityList) {
            userDisplayProviderList.add(convertUserDisplayFromEntityToProvider(userDisplayEntity));
        }
        return userDisplayProviderList;
    }

    public static MessageProvider fromMessageEntity(MessageEntity message) {
        MessageProvider result = new MessageProvider();

        result.id = message.getId();
        result.encryption = message.getEncryption();
        result.sender = message.getSender();
        result.hasAttachments = message.isHasAttachments();
        result.attachments = convertAttachmentsListFromEntityToProvider(message.getAttachments());
        result.createdAt = message.getCreatedAt();
        result.senderDisplay = convertUserDisplayFromEntityToProvider(message.getSenderDisplay());
        result.receiverDisplayList = convertUserDisplayListFromEntityToProvider(message.getReceiverDisplayList());
        result.ccDisplayList = convertUserDisplayListFromEntityToProvider(message.getCcDisplayList());
        result.bccDisplayList = convertUserDisplayListFromEntityToProvider(message.getBccDisplayList());
        result.hasChildren = message.isHasChildren();
        result.childrenCount = message.getChildrenCount();
        result.subject = message.getSubject();
        result.content = message.getContent();
        result.receivers = listToArray(message.getReceivers());
        result.cc = listToArray(message.getCc());
        result.bcc = listToArray(message.getBcc());
        result.folderName = message.getFolderName();
        result.updated = message.getUpdated();
        result.destructDate = message.getDestructDate();
        result.delayedDelivery = message.getDelayedDelivery();
        result.deadManDuration = message.getDeadManDuration();
        result.isRead = message.isRead();
        result.send = message.isSend();
        result.isStarred = message.isStarred();
        result.sentAt = message.getSentAt();
        result.isEncrypted = message.isEncrypted();
        result.isSubjectEncrypted = message.isSubjectEncrypted();
        result.isProtected = message.isProtected();
        result.isHtml = message.isHtml();
        result.hash = message.getHash();
        result.spamReason = message.getSpamReason();
        result.lastAction = message.getLastAction();
        result.lastActionThread = message.getLastActionThread();
        result.mailboxId = message.getMailboxId();
        result.parent = message.getParent();

        return result;
    }

    public static List<MessageProvider> fromMessageEntities(List<MessageEntity> messages) {
        List<MessageProvider> result = new ArrayList<>(messages.size());
        for (MessageEntity message : messages) {
            result.add(MessageProvider.fromMessageEntity(message));
        }
        return result;
    }

    private static AttachmentEntity convertAttachmentFromResponseToEntity(MessageAttachment messageAttachment) {
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setId(messageAttachment.getId());
        attachmentEntity.setContentId(messageAttachment.getContentId());
        attachmentEntity.setDocumentLink(messageAttachment.getDocumentLink());
        attachmentEntity.setInline(messageAttachment.isInline());
        attachmentEntity.setEncrypted(messageAttachment.isEncrypted());
        attachmentEntity.setMessage(messageAttachment.getMessage());
        return attachmentEntity;
    }

    private static List<AttachmentEntity> convertAttachmentsListFromResponsesToEntities(List<MessageAttachment> messageAttachments) {
        if (messageAttachments == null || messageAttachments.isEmpty()) {
            return Collections.emptyList();
        }
        List<AttachmentEntity> attachmentEntities = new ArrayList<>(messageAttachments.size());
        for (MessageAttachment messageAttachment : messageAttachments) {
            attachmentEntities.add(convertAttachmentFromResponseToEntity(messageAttachment));
        }
        return attachmentEntities;
    }

    private static UserDisplayEntity convertUserDisplayFromResponseToEntity(UserDisplay userDisplay) {
        UserDisplayEntity userDisplayEntity = new UserDisplayEntity();
        if (userDisplay != null) {
            userDisplayEntity.setEmail(userDisplay.getEmail());
            userDisplayEntity.setName(userDisplay.getName());
            userDisplayEntity.setEncrypted(userDisplay.isEncrypted());
        }
        return userDisplayEntity;
    }

    private static List<UserDisplayEntity> convertUserDisplayListFromResponseToEntities(List<UserDisplay> userDisplayList) {
        if (userDisplayList == null || userDisplayList.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserDisplayEntity> userDisplayEntityList = new ArrayList<>();
        for (UserDisplay userDisplay : userDisplayList) {
            userDisplayEntityList.add(convertUserDisplayFromResponseToEntity(userDisplay));
        }
        return userDisplayEntityList;
    }

    public static MessageEntity fromMessagesResultToEntity(MessagesResult message, String requestFolder) {
        MessageEntity result = new MessageEntity();

        result.setId(message.getId());
        result.setEncryption(""); // TODO
        result.setSender(message.getSender());
        result.setHasAttachments(message.isHasAttachments());
        result.setAttachments(convertAttachmentsListFromResponsesToEntities(message.getAttachments()));
        result.setCreatedAt(message.getCreatedAt());
        result.setSenderDisplay(convertUserDisplayFromResponseToEntity(message.getSenderDisplay()));
        result.setReceiverDisplayList(convertUserDisplayListFromResponseToEntities(message.getReceiverDisplay()));
        result.setCcDisplayList(convertUserDisplayListFromResponseToEntities(message.getCcDisplay()));
        result.setBccDisplayList(convertUserDisplayListFromResponseToEntities(message.getBccDisplay()));
        result.setHasChildren(message.isHasChildren());
        result.setChildrenCount(message.getChildrenCount());
        result.setSubject(decryptSubject(message.getSubject(), message.getMailboxId(), message.isSubjectEncrypted()));
        result.setContent(decryptContent(message.getContent(), message.getMailboxId()));
        result.setReceivers(arrayToList(message.getReceivers()));
        result.setCc(arrayToList(message.getCc()));
        result.setBcc(arrayToList(message.getBcc()));
        result.setFolderName(message.getFolderName());
        result.setRequestFolder(requestFolder);
        result.setUpdated(message.getUpdated());
        result.setDestructDate(message.getDestructDate());
        result.setDelayedDelivery(message.getDelayedDelivery());
        result.setDeadManDuration(message.getDeadManDuration());
        result.setRead(message.isRead());
        result.setSend(message.isSend());
        result.setStarred(message.isStarred());
        result.setSentAt(message.getSentAt());
        result.setEncrypted(message.isEncrypted());
        result.setSubjectEncrypted(message.isSubjectEncrypted());
        result.setProtected(message.isProtected());
        result.setHtml(message.isHtml());
        result.setHash(message.getHash());
        result.setSpamReason(message.getSpamReason());
        result.setLastAction(message.getLastAction());
        result.setLastActionThread(message.getLastActionThread());
        result.setMailboxId(message.getMailboxId());
        result.setParent(message.getParent());

//        if (requestFolder.equals(MainFolderNames.INBOX) && message.getFolderName().equals(MainFolderNames.SENT)) {
//            result.setShowInInbox(true);
//        }

        return result;
    }

    public static List<MessageEntity> fromMessagesResultsToEntities(List<MessagesResult> messages) {
        return fromMessagesResultsToEntities(messages, "");
    }

    public static List<MessageEntity> fromMessagesResultsToEntities(List<MessagesResult> messages, String requestFolder) {
        List<MessageEntity> result = new ArrayList<>(messages.size());
        for (MessagesResult message : messages) {
            result.add(MessageProvider.fromMessagesResultToEntity(message, requestFolder));
        }
        return result;
    }
}
