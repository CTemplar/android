package mobileapp.ctemplar.com.ctemplarapp.contacts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.contacts.EncryptContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

public class AddContactViewModel extends ViewModel {
    private final ContactsRepository contactsRepository;
    private final UserStore userStore;

    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();

    public AddContactViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
        userStore = CTemplarApp.getUserStore();
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void saveContact(ContactData contactData) {
        boolean contactsEncryption = userStore.isContactsEncryptionEnabled();
        if (contactsEncryption) {
            contactData.encryptContactData();
        }

        contactsRepository.createContact(contactData)
                .subscribe(new Observer<ContactData>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull ContactData contactData) {
                        ContactEntity contactEntity = Contact.fromContactDataToEntity(contactData);
                        contactsRepository.saveContact(contactEntity);
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
