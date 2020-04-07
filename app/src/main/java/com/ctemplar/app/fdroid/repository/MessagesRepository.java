package com.ctemplar.app.fdroid.repository;

import java.util.List;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.RestService;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;

public class MessagesRepository {

    private static MessagesRepository instance = new MessagesRepository();

    public static MessagesRepository getInstance() {
        return instance;
    }

    private RestService service;

    private MessagesRepository() {
        service = CTemplarApp.getRestClient().getRestService();
    }

    public List<MessageEntity> getLocalMessagesByFolder(String folder) {
        return CTemplarApp.getAppDatabase().messageDao().getAllByFolder(folder);
    }

    public List<MessageEntity> getLocalStarredMessages() {
        return CTemplarApp.getAppDatabase().messageDao().getAllStarred();
    }

    public void addMessagesToDatabase(List<MessageEntity> entities) {
        CTemplarApp.getAppDatabase().messageDao().saveAll(entities);
    }

    public void addStarredMessagesToDatabase(List<MessageEntity> entities) {
        CTemplarApp.getAppDatabase().messageDao().saveAllStarred(entities);
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

    public void deleteStarredMessages() {
        CTemplarApp.getAppDatabase().messageDao().deleteStarred();
    }

    public List<MessageEntity> getChildMessages(MessageEntity parentMessage) {
        return CTemplarApp.getAppDatabase().messageDao().getByParentId(String.valueOf(parentMessage.getId()));
    }
}
