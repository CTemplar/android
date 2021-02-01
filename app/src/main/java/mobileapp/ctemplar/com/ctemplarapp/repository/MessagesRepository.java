package mobileapp.ctemplar.com.ctemplarapp.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import timber.log.Timber;

public class MessagesRepository {
    private static MessagesRepository instance = new MessagesRepository();

    public static MessagesRepository getInstance() {
        return instance;
    }

    private RestService service;
    private final MessageDao messageDao;

    private MessagesRepository() {
        service = CTemplarApp.getRestClient().getRestService();
        messageDao = CTemplarApp.getAppDatabase().messageDao();
    }

    public List<MessageEntity> getMessagesByFolder(String folder) {
        return CTemplarApp.getAppDatabase().messageDao().getAllByFolder(folder);
    }

    public List<MessageEntity> getStarredMessages() {
        return CTemplarApp.getAppDatabase().messageDao().getAllStarred();
    }

    public List<MessageEntity> getUnreadMessages() {
        return CTemplarApp.getAppDatabase().messageDao().getAllUnread();
    }

    public List<MessageEntity> getAllMailsMessages() {
        return CTemplarApp.getAppDatabase().messageDao().getAllMails();
    }

    public List<MessageEntity> updateMessages(List<MessageEntity> entities, Date previousMessageUpdateTime) {
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
            if (deletedCount > 0) {
                Timber.i("Deleted %d", deletedCount);
            }
            previousMessageUpdateTime = entity.getUpdatedAt();
            MessageEntity entityFromDb = messageDao.getById(entity.getId());
            if (entityFromDb == null) {
                Timber.i("Save");
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            if (entityFromDb.hasUpdate(entity)) {
                Timber.i("Update");
                messageDao.save(entity);
                result.add(messageDao.getById(entity.getId()));
                continue;
            }
            result.add(entityFromDb);
            Timber.i("Normal state");
        }
        return result;
    }

    public void saveMessage(MessageEntity entity) {
        CTemplarApp.getAppDatabase().messageDao().save(entity);
    }

    public void saveAllMessages(List<MessageEntity> entities) {
        CTemplarApp.getAppDatabase().messageDao().saveAll(entities);
    }

    public void saveAllMessagesWithIgnore(List<MessageEntity> entities) {
        CTemplarApp.getAppDatabase().messageDao().saveAllWithIgnore(entities);
    }

    public MessageEntity getLocalMessage(long id) {
        return CTemplarApp.getAppDatabase().messageDao().getById(id);
    }

    public void addMessageToDatabase(MessageEntity entity) {
        CTemplarApp.getAppDatabase().messageDao().save(entity);
    }

    public void updateMessageFolderName(long messageId, String newFolderName) {
        CTemplarApp.getAppDatabase().messageDao().updateFolderName(messageId, newFolderName);
    }

    public void markMessageIsStarred(long id, boolean isStarred) {
        CTemplarApp.getAppDatabase().messageDao().updateIsStarred(id, isStarred);
    }

    public void markMessageAsRead(long id, boolean isRead) {
        CTemplarApp.getAppDatabase().messageDao().updateIsRead(id, isRead);
    }

    public void deleteMessageById(long id) {
        CTemplarApp.getAppDatabase().messageDao().deleteById(id);
    }

    public void deleteMessagesByParentId(long id) {
        CTemplarApp.getAppDatabase().messageDao().deleteAllByParentId(String.valueOf(id));
    }

    public void deleteMessagesByFolderName(String folder) {
        CTemplarApp.getAppDatabase().messageDao().deleteAllByFolder(folder);
    }

    public void deleteStarred() {
        CTemplarApp.getAppDatabase().messageDao().deleteStarred();
    }

    public void deleteUnread() {
        CTemplarApp.getAppDatabase().messageDao().deleteUnread();
    }

    public void deleteAllMails() {
        CTemplarApp.getAppDatabase().messageDao().deleteAllMails();
    }

    public List<MessageEntity> getChildMessages(long parentMessageId) {
        return CTemplarApp.getAppDatabase().messageDao().getByParentId(String.valueOf(parentMessageId));
    }
}
