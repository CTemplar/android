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
}
