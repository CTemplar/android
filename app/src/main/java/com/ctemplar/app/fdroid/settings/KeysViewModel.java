package com.ctemplar.app.fdroid.settings;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.repository.MailboxDao;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;

public class KeysViewModel extends ViewModel {
    private MailboxDao mailboxDao;

    public KeysViewModel() {
        mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    }

    List<MailboxEntity> getMailboxEntityList() {
        return mailboxDao.getAll();
    }
}
