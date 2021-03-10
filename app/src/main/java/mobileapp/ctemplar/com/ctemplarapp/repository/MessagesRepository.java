package mobileapp.ctemplar.com.ctemplarapp.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import timber.log.Timber;

public class MessagesRepository {
    private static final MessagesRepository instance = new MessagesRepository();

    public static MessagesRepository getInstance() {
        return instance;
    }

    private final MessageDao messageDao;

    private MessagesRepository() {
        messageDao = CTemplarApp.getAppDatabase().messageDao();
    }

    public List<MessageEntity> getMessagesByFolder(String folder, int limit, int offset) {
        return messageDao.getAllByFolder(folder, limit, offset);
    }

    public List<MessageEntity> getStarredMessages(int limit, int offset) {
        return messageDao.getAllStarred(limit, offset);
    }

    public List<MessageEntity> getMessagesByFolderAndCreatedAt(String folder, int limit, int offset) {
        return messageDao.getAllByFolderAndCreatedAt(folder, limit, offset);
    }

    public List<MessageEntity> getSentMessages(int limit, int offset) {
        return messageDao.getSent(limit, offset);
    }

    public List<MessageEntity> getInboxMessages(int limit, int offset) {
        return messageDao.getInbox(limit, offset);
    }

    public List<MessageEntity> getUnreadMessages(int limit, int offset) {
        return messageDao.getAllUnread(limit, offset);
    }

    public List<MessageEntity> getAllMailsMessages(int limit, int offset) {
        return messageDao.getAllMails(limit, offset);
    }

    public List<MessageEntity> updateAllMails(List<MessageEntity> entities, Date previousMessageUpdateTime) {
        if (entities.isEmpty()) {
            if (previousMessageUpdateTime != null) {
                messageDao.deleteAllEmailsInPeriod(new Date(0), previousMessageUpdateTime);
            }
            return new ArrayList<>();
        }
        List<MessageEntity> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            MessageEntity entity = entities.get(i);
            int deletedCount = messageDao.deleteAllEmailsInPeriod(entity.getUpdatedAt(), previousMessageUpdateTime);
            previousMessageUpdateTime = entity.getUpdatedAt();
            MessageEntity entityFromDb = messageDao.getById(entity.getId());
            if (entityFromDb == null) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            if (entityFromDb.hasUpdate(entity)) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            result.add(entityFromDb);
        }
        return result;
    }

    public List<MessageEntity> updateFolder(String folder, List<MessageEntity> entities, Date previousMessageUpdateTime) {
        if (entities.isEmpty()) {
            if (previousMessageUpdateTime != null) {
                messageDao.deleteByFolderInPeriod(folder, new Date(0), previousMessageUpdateTime);
            }
            return new ArrayList<>();
        }
        List<MessageEntity> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            MessageEntity entity = entities.get(i);
            int deletedCount = messageDao.deleteByFolderInPeriod(folder, entity.getUpdatedAt(), previousMessageUpdateTime);
            previousMessageUpdateTime = entity.getUpdatedAt();
            MessageEntity entityFromDb = messageDao.getById(entity.getId());
            if (entityFromDb == null) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            if (entityFromDb.hasUpdate(entity)) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            result.add(entityFromDb);
        }
        return result;
    }

    public List<MessageEntity> updateFolderByCreatedAt(String folder, List<MessageEntity> entities, Date previousMessageUpdateTime) {
        if (entities.isEmpty()) {
            if (previousMessageUpdateTime != null) {
                messageDao.deleteByFolderInPeriodByCreatedAt(folder, new Date(0), previousMessageUpdateTime);
            }
            return new ArrayList<>();
        }
        List<MessageEntity> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            MessageEntity entity = entities.get(i);
            int deletedCount = messageDao.deleteByFolderInPeriodByCreatedAt(folder, entity.getCreatedAt(), previousMessageUpdateTime);
            previousMessageUpdateTime = entity.getCreatedAt();
            MessageEntity entityFromDb = messageDao.getById(entity.getId());
            if (entityFromDb == null) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            if (entityFromDb.hasUpdate(entity)) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            result.add(entityFromDb);
        }
        return result;
    }

    public List<MessageEntity> updateUnread(List<MessageEntity> entities, Date previousMessageUpdateTime) {
        if (entities.isEmpty()) {
            if (previousMessageUpdateTime != null) {
                messageDao.markUnreadAsReadInPeriod(new Date(0), previousMessageUpdateTime);
            }
            return new ArrayList<>();
        }
        List<MessageEntity> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            MessageEntity entity = entities.get(i);
            int deletedCount = messageDao.markUnreadAsReadInPeriod(entity.getUpdatedAt(), previousMessageUpdateTime);
            previousMessageUpdateTime = entity.getUpdatedAt();
            MessageEntity entityFromDb = messageDao.getById(entity.getId());
            if (entityFromDb == null) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            if (entityFromDb.hasUpdate(entity)) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            result.add(entityFromDb);
        }
        return result;
    }

    public List<MessageEntity> updateStarred(List<MessageEntity> entities, Date previousMessageUpdateTime) {
        if (entities.isEmpty()) {
            if (previousMessageUpdateTime != null) {
                messageDao.markStarredAsUnstarredInPeriod(new Date(0), previousMessageUpdateTime);
            }
            return new ArrayList<>();
        }
        List<MessageEntity> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            MessageEntity entity = entities.get(i);
            int deletedCount = messageDao.markStarredAsUnstarredInPeriod(entity.getCreatedAt(), previousMessageUpdateTime);
            previousMessageUpdateTime = entity.getCreatedAt();
            MessageEntity entityFromDb = messageDao.getById(entity.getId());
            if (entityFromDb == null) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            if (entityFromDb.hasUpdate(entity)) {
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            result.add(entityFromDb);
        }
        return result;
    }

    public void saveMessage(MessageEntity entity) {
        messageDao.save(entity);
    }

    public void saveAllMessages(List<MessageEntity> entities) {
        messageDao.saveAll(entities);
    }

    public void saveAllMessagesWithIgnore(List<MessageEntity> entities) {
        messageDao.saveAllWithIgnore(entities);
    }

    public MessageEntity getLocalMessage(long id) {
        return messageDao.getById(id);
    }

    public void addMessageToDatabase(MessageEntity entity) {
        messageDao.save(entity);
    }

    public void updateMessageFolderName(long messageId, String newFolderName) {
        messageDao.updateFolderName(messageId, newFolderName);
    }

    public void markMessageIsStarred(long id, boolean isStarred) {
        messageDao.updateIsStarred(id, isStarred);
    }

    public void markMessageAsRead(long id, boolean isRead) {
        messageDao.updateIsRead(id, isRead);
    }

    public void deleteMessageById(long id) {
        messageDao.deleteById(id);
    }

    public void deleteMessagesByParentId(long id) {
        messageDao.deleteAllByParentId(String.valueOf(id));
    }

    public void deleteMessagesByFolderName(String folder) {
        messageDao.deleteAllByFolder(folder);
    }

    public void deleteStarred() {
        messageDao.deleteStarred();
    }

    public void deleteUnread() {
        messageDao.deleteUnread();
    }

    public void deleteAllMails() {
        messageDao.deleteAllMails();
    }

    public List<MessageEntity> getChildMessages(long parentMessageId) {
        return messageDao.getByParentId(String.valueOf(parentMessageId));
    }

    public void updateDecryptedSubject(long messageId, String decryptedSubject) {
        messageDao.updateDecryptedSubject(messageId, decryptedSubject);
    }

    public void clearAllDecryptedSubjects() {
        messageDao.clearAllDecryptedSubjects();
    }
}
