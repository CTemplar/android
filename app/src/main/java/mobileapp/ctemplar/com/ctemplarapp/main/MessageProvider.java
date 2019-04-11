package mobileapp.ctemplar.com.ctemplarapp.main;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.AttachmentEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;
import timber.log.Timber;

public class MessageProvider {

    private long id;
    private String encryption;
    private String sender;
    private boolean hasAttachments;
    private List<AttachmentProvider> attachments;
    private String createdAt;
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
    private boolean isProtected;
    private String hash;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public static MailboxEntity getDefaultMailbox() {
        if (CTemplarApp.getAppDatabase().mailboxDao().getDefault() != null) {
            return CTemplarApp.getAppDatabase().mailboxDao().getDefault();
        } else {
            Timber.e("Default mailbox is null");
            if (!CTemplarApp.getAppDatabase().mailboxDao().getAll().isEmpty()) {
                return CTemplarApp.getAppDatabase().mailboxDao().getAll().get(0);
            } else {
                Timber.e("Mailbox not found");
            }
        }
        return null;
    }

    private static String decryptContent(String content, long mailboxId) {
        String password = CTemplarApp.getInstance()
                        .getSharedPreferences("pref_user", Context.MODE_PRIVATE)
                        .getString("key_password", null);

        PGPManager pgpManager = new PGPManager();
        String messageContent = "";
        String privateKey = null;
        MailboxEntity mailboxEntity = CTemplarApp.getAppDatabase().mailboxDao().getById(mailboxId);
        if (mailboxEntity != null){
            privateKey = mailboxEntity.getPrivateKey();
        }
        if (password != null && content != null && privateKey != null) {
            messageContent = pgpManager.decryptMessage(content, privateKey, password);
        }
        return messageContent.replaceAll("<img.+?>", "");
    }

    private static AttachmentProvider convertFromResponseMessageAttachmentToAttachmentProvider(MessageAttachment messageAttachment) {
        AttachmentProvider attachmentProvider = new AttachmentProvider();
        attachmentProvider.setId(messageAttachment.getId());
        attachmentProvider.setContentId(messageAttachment.getContent_id());
        attachmentProvider.setDocumentLink(messageAttachment.getDocumentLink());
        attachmentProvider.setInline(messageAttachment.isInline());
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

    public static MessageProvider fromMessagesResult(MessagesResult message) {
        MessageProvider result = new MessageProvider();

        result.id = message.getId();
        result.encryption = ""; //TODO
        result.sender = message.getSender();
        result.hasAttachments = !isNullOrEmpty(message.getAttachments());
        result.attachments = convertResponseAttachmentsListToProviderList(message.getAttachments());
        result.createdAt = message.getCreatedAt();
        result.hasChildren = message.hasChildren();
        result.childrenCount = message.getChildrenCount();
        result.subject = message.getSubject();
        result.content = decryptContent(message.getContent(), message.getMailboxId());
        result.receivers = message.getReceivers();
        result.cc = message.getCC();
        result.bcc = message.getBCC();
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
        result.isProtected = message.isProtected();
        result.hash = message.getHash();
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

    public static MessageProvider fromMessageEntity(MessageEntity message) {
        MessageProvider result = new MessageProvider();

        result.id = message.getId();
        result.encryption = message.getEncryption();
        result.sender = message.getSender();
        result.hasAttachments = message.isHasAttachments();
        result.attachments = convertAttachmentsListFromEntityToProvider(message.getAttachments());
        result.createdAt = message.getCreatedAt();
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
        result.isProtected = message.isProtected();
        result.hash = message.getHash();
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

    private static <T> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    private static AttachmentEntity convertAttachmentFromResponseToEntity(MessageAttachment messageAttachment) {
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setId(messageAttachment.getId());
        attachmentEntity.setContentId(messageAttachment.getContent_id());
        attachmentEntity.setDocumentLink(messageAttachment.getDocumentLink());
        attachmentEntity.setInline(messageAttachment.isInline());
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

    public static MessageEntity fromMessagesResultToEntity(MessagesResult message) {
        return fromMessagesResultToEntity(message, "");
    }

    public static MessageEntity fromMessagesResultToEntity(MessagesResult message, String requestFolder) {
        MessageEntity result = new MessageEntity();

        result.setId(message.getId());
        result.setEncryption(""); // TODO
        result.setSender(message.getSender());
        result.setHasAttachments(!isNullOrEmpty(message.getAttachments()));
        result.setAttachments(convertAttachmentsListFromResponsesToEntities(message.getAttachments()));
        result.setCreatedAt(message.getCreatedAt());
        result.setHasChildren(message.hasChildren());
        result.setChildrenCount(message.getChildrenCount());
        result.setSubject(message.getSubject());
        result.setContent(decryptContent(message.getContent(), message.getMailboxId()));
        result.setReceivers(arrayToList(message.getReceivers()));
        result.setCc(arrayToList(message.getCC()));
        result.setBcc(arrayToList(message.getBCC()));
        result.setFolderName(message.getFolderName());
        result.setUpdated(message.getUpdated());
        result.setDestructDate(message.getDestructDate());
        result.setDelayedDelivery(message.getDelayedDelivery());
        result.setDeadManDuration(message.getDeadManDuration());
        result.setRead(message.isRead());
        result.setSend(message.isSend());
        result.setStarred(message.isStarred());
        result.setSentAt(message.getSentAt());
        result.setEncrypted(message.isEncrypted());
        result.setProtected(message.isProtected());
        result.setHash(message.getHash());
        result.setMailboxId(message.getMailboxId());
        result.setParent(message.getParent());

        if (requestFolder.equals("inbox") && message.getFolderName().equals("sent")) {
            result.setShowInInbox(true);
        }

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
}
