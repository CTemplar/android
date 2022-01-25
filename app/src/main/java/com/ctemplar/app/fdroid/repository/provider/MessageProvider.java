package com.ctemplar.app.fdroid.repository.provider;

import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.net.response.messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.messages.UserDisplayResponse;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.repository.entity.AttachmentEntity;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;
import com.ctemplar.app.fdroid.repository.entity.UserDisplayEntity;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.EncryptUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageProvider {
    private long id;
    private EncryptionMessageProvider encryptionMessage;
    private String sender;
    private boolean hasAttachments;
    private List<AttachmentProvider> attachments;
    private Date createdAt;
    private UserDisplayProvider senderDisplay;
    private List<UserDisplayProvider> receiverDisplayList;
    private List<UserDisplayProvider> ccDisplayList;
    private List<UserDisplayProvider> bccDisplayList;
    private List<UserDisplayProvider> replyToDisplayList;
    private Map<String, String> participants;
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
    private boolean isVerified;
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

    public MessageProvider(long id, EncryptionMessageProvider encryptionMessage, String sender, boolean hasAttachments, List<AttachmentProvider> attachments, Date createdAt, UserDisplayProvider senderDisplay, List<UserDisplayProvider> receiverDisplayList, List<UserDisplayProvider> ccDisplayList, List<UserDisplayProvider> bccDisplayList, List<UserDisplayProvider> replyToDisplayList, Map<String, String> participants, boolean hasChildren, int childrenCount, String subject, String content, String[] receivers, String[] cc, String[] bcc, String folderName, Date updatedAt, Date destructDate, Date delayedDelivery, Long deadManDuration, boolean isRead, boolean send, boolean isStarred, Date sentAt, boolean isEncrypted, boolean isSubjectEncrypted, boolean isProtected, boolean isVerified, boolean isHtml, String hash, List<String> spamReason, String lastAction, String lastActionThread, long mailboxId, String parent, boolean isSubjectDecrypted, String decryptedSubject) {
        this.id = id;
        this.encryptionMessage = encryptionMessage;
        this.sender = sender;
        this.hasAttachments = hasAttachments;
        this.attachments = attachments;
        this.createdAt = createdAt;
        this.senderDisplay = senderDisplay;
        this.receiverDisplayList = receiverDisplayList;
        this.ccDisplayList = ccDisplayList;
        this.bccDisplayList = bccDisplayList;
        this.replyToDisplayList = replyToDisplayList;
        this.participants = participants;
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
        this.isVerified = isVerified;
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

    public void setEncryptionMessage(EncryptionMessageProvider encryptionMessage) {
        this.encryptionMessage = encryptionMessage;
    }

    public EncryptionMessageProvider getEncryptionMessage() {
        return encryptionMessage;
    }

    public void setEncryption(EncryptionMessageProvider encryptionMessage) {
        this.encryptionMessage = encryptionMessage;
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

    public List<UserDisplayProvider> getReplyToDisplayList() {
        return replyToDisplayList;
    }

    public void setReplyToDisplayList(List<UserDisplayProvider> replyToDisplayList) {
        this.replyToDisplayList = replyToDisplayList;
    }

    public Map<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, String> participants) {
        this.participants = participants;
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
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

    public String getSenderDisplayName() {
        if (senderDisplay == null) {
            if (sender == null) {
                return "";
            } else {
                return sender;
            }
        }
        if (EditTextUtils.isNotEmpty(senderDisplay.getName())) {
            return senderDisplay.getName();
        }
        if (EditTextUtils.isNotEmpty(senderDisplay.getEmail())) {
            return senderDisplay.getEmail();
        }
        return "";
    }

    private static AttachmentProvider convertFromResponseMessageAttachmentToAttachmentProvider(MessageAttachment attachment) {
        AttachmentProvider provider = new AttachmentProvider();
        provider.setId(attachment.getId());
        provider.setFileSize(attachment.getFileSize());
        provider.setDocumentUrl(attachment.getDocumentUrl());
        provider.setDeleted(attachment.isDeleted());
        provider.setDeletedAt(attachment.getDeletedAt());
        provider.setName(attachment.getName());
        provider.setInline(attachment.isInline());
        provider.setEncrypted(attachment.isEncrypted());
        provider.setForwarded(attachment.isForwarded());
        provider.setPGPMime(attachment.isPGPMime());
        provider.setContentId(attachment.getContentId());
        provider.setFileType(attachment.getFileType());
        provider.setActualSize(attachment.getActualSize());
        provider.setMessage(attachment.getMessage());
        return provider;
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
        if (userDisplayResponse == null) {
            return new UserDisplayProvider();
        }
        UserDisplayProvider userDisplayProvider = new UserDisplayProvider();
        userDisplayProvider.setEmail(userDisplayResponse.getEmail());
        userDisplayProvider.setName(userDisplayResponse.getName());
        userDisplayProvider.setEncrypted(userDisplayResponse.isEncrypted());
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

    private static AttachmentProvider convertAttachmentFromEntityToProvider(AttachmentEntity entity) {
        AttachmentProvider provider = new AttachmentProvider();
        provider.setId(entity.getId());
        provider.setFileSize(entity.getFileSize());
        provider.setDocumentUrl(entity.getDocumentUrl());
        provider.setDeleted(entity.isDeleted());
        provider.setDeletedAt(entity.getDeletedAt());
        provider.setName(entity.getName());
        provider.setInline(entity.isInline());
        provider.setEncrypted(entity.isEncrypted());
        provider.setForwarded(entity.isForwarded());
        provider.setPGPMime(entity.isPGPMime());
        provider.setContentId(entity.getContentId());
        provider.setFileType(entity.getFileType());
        provider.setActualSize(entity.getActualSize());
        provider.setMessage(entity.getMessage());
        return provider;
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
        if (message == null) {
            return new MessageProvider();
        }
        MessageProvider messageProvider = new MessageProvider();
        messageProvider.id = message.getId();
        messageProvider.encryptionMessage = EncryptionMessageProvider.fromEntityToProvider(message.getEncryptionMessage());
        messageProvider.sender = message.getSender();
        messageProvider.hasAttachments = message.isHasAttachments();
        messageProvider.attachments = convertAttachmentsListFromEntityToProvider(message.getAttachments());
        messageProvider.createdAt = message.getCreatedAt();
        messageProvider.senderDisplay = convertUserDisplayFromEntityToProvider(message.getSenderDisplay());
        messageProvider.receiverDisplayList = convertUserDisplayListFromEntityToProvider(message.getReceiverDisplayList());
        messageProvider.ccDisplayList = convertUserDisplayListFromEntityToProvider(message.getCcDisplayList());
        messageProvider.bccDisplayList = convertUserDisplayListFromEntityToProvider(message.getBccDisplayList());
        messageProvider.replyToDisplayList = convertUserDisplayListFromEntityToProvider(message.getReplyToDisplayList());
        messageProvider.participants = message.getParticipants();
        messageProvider.hasChildren = message.isHasChildren();
        messageProvider.childrenCount = message.getChildrenCount();
        if (message.getEncryptionMessage() == null) {
            if (!decryptSubject) {
                messageProvider.subject = message.getSubject();
            } else if (!message.isSubjectEncrypted()) {
                messageProvider.subject = message.getSubject();
            } else if (message.getDecryptedSubject() != null) {
                messageProvider.subject = message.getDecryptedSubject();
            } else {
                messageProvider.subject = EncryptUtils.decryptSubject(message.getSubject(), message.getMailboxId());
            }
        } else {
            messageProvider.subject = message.getSubject();
        }
        messageProvider.content = EncryptUtils.decryptContent(
                message.getContent(),
                message.getMailboxId(),
                decryptContent && message.getEncryptionMessage() == null
        );
        messageProvider.receivers = listToArray(message.getReceivers());
        messageProvider.cc = listToArray(message.getCc());
        messageProvider.bcc = listToArray(message.getBcc());
        messageProvider.folderName = message.getFolderName();
        messageProvider.updatedAt = message.getUpdatedAt();
        messageProvider.destructDate = message.getDestructDate();
        messageProvider.delayedDelivery = message.getDelayedDelivery();
        messageProvider.deadManDuration = message.getDeadManDuration();
        messageProvider.isRead = message.isRead();
        messageProvider.send = message.isSend();
        messageProvider.isStarred = message.isStarred();
        messageProvider.sentAt = message.getSentAt();
        messageProvider.isEncrypted = message.isEncrypted();
        messageProvider.isSubjectEncrypted = message.isSubjectEncrypted();
        messageProvider.isProtected = message.isProtected();
        messageProvider.isVerified = message.isVerified();
        messageProvider.isHtml = message.isHtml();
        messageProvider.hash = message.getHash();
        messageProvider.spamReason = message.getSpamReason();
        messageProvider.lastAction = message.getLastAction();
        messageProvider.lastActionThread = message.getLastActionThread();
        messageProvider.mailboxId = message.getMailboxId();
        messageProvider.parent = message.getParent();

        messageProvider.decryptedSubject = message.getDecryptedSubject();

        return messageProvider;
    }

    public static List<MessageProvider> fromMessageEntities(List<MessageEntity> messages, boolean decryptContent, boolean decryptSubject) {
        List<MessageProvider> result = new ArrayList<>(messages.size());
        for (MessageEntity message : messages) {
            result.add(MessageProvider.fromMessageEntity(message, decryptContent, decryptSubject));
        }
        return result;
    }

    private static AttachmentEntity convertAttachmentFromResponseToEntity(MessageAttachment attachment) {
        AttachmentEntity entity = new AttachmentEntity();
        entity.setId(attachment.getId());
        entity.setFileSize(attachment.getFileSize());
        entity.setDocumentUrl(attachment.getDocumentUrl());
        entity.setDeleted(attachment.isDeleted());
        entity.setDeletedAt(attachment.getDeletedAt());
        entity.setName(attachment.getName());
        entity.setInline(attachment.isInline());
        entity.setEncrypted(attachment.isEncrypted());
        entity.setForwarded(attachment.isForwarded());
        entity.setPGPMime(attachment.isPGPMime());
        entity.setContentId(attachment.getContentId());
        entity.setFileType(attachment.getFileType());
        entity.setActualSize(attachment.getActualSize());
        entity.setMessage(attachment.getMessage());
        return entity;
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
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(message.getId());
        messageEntity.setEncryptionMessage(EncryptionMessageProvider.fromResponseToEntity(message.getEncryptionMessage()));
        messageEntity.setSender(message.getSender());
        messageEntity.setHasAttachments(message.isHasAttachments());
        messageEntity.setAttachments(convertAttachmentsListFromResponsesToEntities(message.getAttachments()));
        messageEntity.setCreatedAt(message.getCreatedAt());
        messageEntity.setSenderDisplay(convertUserDisplayFromResponseToEntity(message.getSenderDisplay()));
        messageEntity.setReceiverDisplayList(convertUserDisplayListFromResponseToEntities(message.getReceiverDisplay()));
        messageEntity.setCcDisplayList(convertUserDisplayListFromResponseToEntities(message.getCcDisplay()));
        messageEntity.setBccDisplayList(convertUserDisplayListFromResponseToEntities(message.getBccDisplay()));
        messageEntity.setReplyToDisplayList(convertUserDisplayListFromResponseToEntities(message.getReplyToDisplay()));
        messageEntity.setParticipants(message.getParticipants());
        messageEntity.setHasChildren(message.isHasChildren());
        messageEntity.setChildrenCount(message.getChildrenCount());
        messageEntity.setSubject(message.getSubject());
        messageEntity.setContent(message.getContent());
        messageEntity.setReceivers(arrayToList(message.getReceivers()));
        messageEntity.setCc(arrayToList(message.getCc()));
        messageEntity.setBcc(arrayToList(message.getBcc()));
        messageEntity.setFolderName(message.getFolderName());
        messageEntity.setUpdatedAt(message.getUpdatedAt().after(message.getCreatedAt())
                ? message.getUpdatedAt() : message.getCreatedAt());
        messageEntity.setDestructDate(message.getDestructDate());
        messageEntity.setDelayedDelivery(message.getDelayedDelivery());
        messageEntity.setDeadManDuration(message.getDeadManDuration());
        messageEntity.setRead(message.isRead());
        messageEntity.setSend(message.isSend());
        messageEntity.setStarred(message.isStarred());
        messageEntity.setSentAt(message.getSentAt());
        messageEntity.setEncrypted(message.isEncrypted());
        messageEntity.setSubjectEncrypted(message.isSubjectEncrypted());
        messageEntity.setProtected(message.isProtected());
        messageEntity.setVerified(message.isVerified());
        messageEntity.setHtml(message.isHtml());
        messageEntity.setHash(message.getHash());
        messageEntity.setSpamReason(message.getSpamReason());
        messageEntity.setLastAction(message.getLastAction());
        messageEntity.setLastActionThread(message.getLastActionThread());
        messageEntity.setMailboxId(message.getMailboxId());
        messageEntity.setParent(message.getParent());

        if (MainFolderNames.SENT.equals(requestFolder)) {
            if (!message.isSend() && message.getChildrenCount() > 0) {
                messageEntity.setHasSentChild(true);
            }
        }
        if (MainFolderNames.INBOX.equals(requestFolder)) {
            if (message.isSend() && message.getChildrenCount() > 0) {
                messageEntity.setHasInboxChild(true);
            }
        }

        return messageEntity;
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
