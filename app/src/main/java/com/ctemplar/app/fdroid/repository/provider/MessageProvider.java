package com.ctemplar.app.fdroid.repository.provider;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.ctemplar.app.fdroid.net.response.messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.messages.UserDisplayResponse;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.repository.entity.AttachmentEntity;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.entity.UserDisplayEntity;
import com.ctemplar.app.fdroid.utils.EncryptUtils;

public class MessageProvider {
    private long id;
    private String encryption;
    private String sender;
    private boolean hasAttachments;
    private List<AttachmentProvider> attachments;
    private Date createdAt;
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
    private Date updatedAt;
    private Date destructDate;
    private Date delayedDelivery;
    private Long deadManDuration;
    private boolean isRead;
    private boolean send;
    private boolean isStarred;
    private Date sentAt;
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
    private boolean isSubjectDecrypted;

    private String decryptedSubject;

    public MessageProvider() {

    }

    public MessageProvider(long id, String encryption, String sender, boolean hasAttachments, List<AttachmentProvider> attachments, Date createdAt, UserDisplayProvider senderDisplay, List<UserDisplayProvider> receiverDisplayList, List<UserDisplayProvider> ccDisplayList, List<UserDisplayProvider> bccDisplayList, boolean hasChildren, int childrenCount, String subject, String content, String[] receivers, String[] cc, String[] bcc, String folderName, Date updatedAt, Date destructDate, Date delayedDelivery, Long deadManDuration, boolean isRead, boolean send, boolean isStarred, Date sentAt, boolean isEncrypted, boolean isSubjectEncrypted, boolean isProtected, boolean isHtml, String hash, List<String> spamReason, String lastAction, String lastActionThread, long mailboxId, String parent, boolean isSubjectDecrypted, String decryptedSubject) {
        this.id = id;
        this.encryption = encryption;
        this.sender = sender;
        this.hasAttachments = hasAttachments;
        this.attachments = attachments;
        this.createdAt = createdAt;
        this.senderDisplay = senderDisplay;
        this.receiverDisplayList = receiverDisplayList;
        this.ccDisplayList = ccDisplayList;
        this.bccDisplayList = bccDisplayList;
        this.hasChildren = hasChildren;
        this.childrenCount = childrenCount;
        this.subject = subject;
        this.content = content;
        this.receivers = receivers;
        this.cc = cc;
        this.bcc = bcc;
        this.folderName = folderName;
        this.updatedAt = updatedAt;
        this.destructDate = destructDate;
        this.delayedDelivery = delayedDelivery;
        this.deadManDuration = deadManDuration;
        this.isRead = isRead;
        this.send = send;
        this.isStarred = isStarred;
        this.sentAt = sentAt;
        this.isEncrypted = isEncrypted;
        this.isSubjectEncrypted = isSubjectEncrypted;
        this.isProtected = isProtected;
        this.isHtml = isHtml;
        this.hash = hash;
        this.spamReason = spamReason;
        this.lastAction = lastAction;
        this.lastActionThread = lastActionThread;
        this.mailboxId = mailboxId;
        this.parent = parent;
        this.isSubjectDecrypted = isSubjectDecrypted;

        this.decryptedSubject = decryptedSubject;
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDestructDate() {
        return destructDate;
    }

    public void setDestructDate(Date destructDate) {
        this.destructDate = destructDate;
    }

    public Date getDelayedDelivery() {
        return delayedDelivery;
    }

    public void setDelayedDelivery(Date delayedDelivery) {
        this.delayedDelivery = delayedDelivery;
    }

    public Long getDeadManDuration() {
        return deadManDuration;
    }

    public void setDeadManDuration(Long deadManDuration) {
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

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
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

    public boolean isSubjectDecrypted() {
        return isSubjectDecrypted;
    }

    public void setSubjectDecrypted(boolean subjectDecrypted) {
        isSubjectDecrypted = subjectDecrypted;
    }

    public String getDecryptedSubject() {
        return decryptedSubject;
    }

    public void setDecryptedSubject(String decryptedSubject) {
        this.decryptedSubject = decryptedSubject;
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

    private static UserDisplayProvider convertUserDisplayFromResponseToProvider(UserDisplayResponse userDisplayResponse) {
        UserDisplayProvider userDisplayProvider = new UserDisplayProvider();
        if (userDisplayResponse != null) {
            userDisplayProvider.setEmail(userDisplayResponse.getEmail());
            userDisplayProvider.setName(userDisplayResponse.getName());
            userDisplayProvider.setEncrypted(userDisplayResponse.isEncrypted());
        }
        return userDisplayProvider;
    }

    public static List<UserDisplayProvider> convertUserDisplayListFromResponseToProvider(List<UserDisplayResponse> userDisplayResponseList) {
        if (userDisplayResponseList == null || userDisplayResponseList.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserDisplayProvider> userDisplayProviderList = new ArrayList<>(userDisplayResponseList.size());
        for (UserDisplayResponse userDisplayResponse : userDisplayResponseList) {
            userDisplayProviderList.add(convertUserDisplayFromResponseToProvider(userDisplayResponse));
        }
        return userDisplayProviderList;
    }

    private static String[] listToArray(List<String> list) {
        if (list == null) {
            return new String[0];
        }
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static List<String> arrayToList(String[] array) {
        if (array == null) {
            return new ArrayList<>(0);
        }
        return new ArrayList<>(Arrays.asList(array));
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

    public static MessageProvider fromMessageEntity(MessageEntity message, boolean decryptContent, boolean decryptSubject) {
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
        if (!decryptSubject) {
            result.subject = message.getSubject();
        } else if (!message.isSubjectEncrypted()) {
            result.subject = message.getSubject();
        } else if (message.getDecryptedSubject() != null) {
            result.subject = message.getDecryptedSubject();
        } else {
            result.subject = EncryptUtils.decryptSubject(message.getSubject(), message.getMailboxId());
        }
        result.content = EncryptUtils.decryptContent(message.getContent(), message.getMailboxId(),
                decryptContent);
        result.receivers = listToArray(message.getReceivers());
        result.cc = listToArray(message.getCc());
        result.bcc = listToArray(message.getBcc());
        result.folderName = message.getFolderName();
        result.updatedAt = message.getUpdatedAt();
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

        result.decryptedSubject = message.getDecryptedSubject();

        return result;
    }

    public static List<MessageProvider> fromMessageEntities(List<MessageEntity> messages, boolean decryptContent, boolean decryptSubject) {
        List<MessageProvider> result = new ArrayList<>(messages.size());
        for (MessageEntity message : messages) {
            result.add(MessageProvider.fromMessageEntity(message, decryptContent, decryptSubject));
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

    private static UserDisplayEntity convertUserDisplayFromResponseToEntity(UserDisplayResponse userDisplayResponse) {
        UserDisplayEntity userDisplayEntity = new UserDisplayEntity();
        if (userDisplayResponse != null) {
            userDisplayEntity.setEmail(userDisplayResponse.getEmail());
            userDisplayEntity.setName(userDisplayResponse.getName());
            userDisplayEntity.setEncrypted(userDisplayResponse.isEncrypted());
        }
        return userDisplayEntity;
    }

    private static List<UserDisplayEntity> convertUserDisplayListFromResponseToEntities(List<UserDisplayResponse> userDisplayResponseList) {
        if (userDisplayResponseList == null || userDisplayResponseList.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserDisplayEntity> userDisplayEntityList = new ArrayList<>();
        for (UserDisplayResponse userDisplayResponse : userDisplayResponseList) {
            userDisplayEntityList.add(convertUserDisplayFromResponseToEntity(userDisplayResponse));
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
        result.setSubject(message.getSubject());
        result.setContent(message.getContent());
        result.setReceivers(arrayToList(message.getReceivers()));
        result.setCc(arrayToList(message.getCc()));
        result.setBcc(arrayToList(message.getBcc()));
        result.setFolderName(message.getFolderName());
        result.setUpdatedAt(message.getUpdatedAt().after(message.getCreatedAt()) ? message.getUpdatedAt() : message.getCreatedAt());
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

        if (MainFolderNames.SENT.equals(requestFolder)) {
            if (!message.isSend() && message.getChildrenCount() > 0) {
                result.setHasSentChild(true);
            }
        }
        if (MainFolderNames.INBOX.equals(requestFolder)) {
            if (message.isSend() && message.getChildrenCount() > 0) {
                result.setHasInboxChild(true);
            }
        }

        return result;
    }

    public static MessageEntity fromMessagesResultToEntity(@Nullable MessagesResult message) {
        if (message == null) {
            return new MessageEntity();
        }
        return fromMessagesResultToEntity(message, "");
    }

    public static List<MessageEntity> fromMessagesResultsToEntities(@Nullable List<MessagesResult> messages) {
        if (messages == null) {
            return new ArrayList<>();
        }
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
