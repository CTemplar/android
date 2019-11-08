package mobileapp.ctemplar.com.ctemplarapp.contacts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.EncryptContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import timber.log.Timber;

public class EditContactViewModel extends ViewModel {

    private ContactsRepository contactsRepository;
    private UserStore userStore;

    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<Contact> contactResponse = new MutableLiveData<>();

    public EditContactViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
        userStore = CTemplarApp.getUserStore();
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    MutableLiveData<Contact> getContactResponse() {
        return contactResponse;
    }

    void getContact(long id) {
        ContactEntity contactEntity = contactsRepository.getLocalContact(id);
        if (contactEntity == null) {
            contactResponse.postValue(null);
        } else {
            contactResponse.postValue(Contact.fromEntity(contactEntity));
        }
    }

    void updateContact(ContactData contactData) {
        boolean contactsEncryption = userStore.getContactsEncryptionEnabled();
        if (contactsEncryption) {
            EncryptContact encryptContact = new EncryptContact();
            encryptContact.setEmail(contactData.getEmail());
            encryptContact.setName(contactData.getName());
            encryptContact.setAddress(contactData.getAddress());
            encryptContact.setNote(contactData.getNote());
            encryptContact.setPhone(contactData.getPhone());
            encryptContact.setPhone2(contactData.getPhone2());
            encryptContact.setProvider(contactData.getProvider());

            String contactString = new Gson().toJson(encryptContact);
            String encryptedContactString = Contact.encryptData(contactString);
            contactData.setEncryptedData(encryptedContactString);
            contactData.setEncrypted(true);
            contactsRepository.saveLocalContact(contactData);

            contactData.setEmail(null);
            contactData.setName(null);
            contactData.setAddress(null);
            contactData.setNote(null);
            contactData.setPhone(null);
            contactData.setPhone2(null);
            contactData.setProvider(null);
        } else {
            contactsRepository.saveLocalContact(contactData);
        }

        contactsRepository.updateContact(contactData)
                .subscribe(new Observer<ContactData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactData contactData) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
