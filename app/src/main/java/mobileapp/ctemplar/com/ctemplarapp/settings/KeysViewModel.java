package mobileapp.ctemplar.com.ctemplarapp.settings;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class KeysViewModel extends ViewModel {
    private MailboxDao mailboxDao;

    public KeysViewModel() {
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    }

    List<MailboxEntity> getMailboxEntityList() {
        return mailboxDao.getAll();
    }
}
