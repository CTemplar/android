package mobileapp.ctemplar.com.ctemplarapp.repository;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;

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

    public void addMessagesToDatabase(List<MessageEntity> entities) {
        CTemplarApp.getAppDatabase().messageDao().saveAll(entities);
    }

    public void deleteLocalMessagesByFolderName(String folder) {
        CTemplarApp.getAppDatabase().messageDao().deleteAllByFolder(folder);
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

    public void deleteMessagesByParentId(long id) {
        CTemplarApp.getAppDatabase().messageDao().deleteAllByParentId(((Object)id).toString());
    }

    public List<MessageEntity> getChildMessages(MessageEntity parentMessage) {
        return CTemplarApp.getAppDatabase().messageDao().getByParentId(((Object)parentMessage.getId()).toString());
    }
}
