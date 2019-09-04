package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import timber.log.Timber;

public class SettingsActivityViewModel extends ViewModel {

    private ContactsRepository contactsRepository;

    public SettingsActivityViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
    }
}
