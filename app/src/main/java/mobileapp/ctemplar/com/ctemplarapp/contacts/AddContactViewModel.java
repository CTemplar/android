package mobileapp.ctemplar.com.ctemplarapp.contacts;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import timber.log.Timber;

public class AddContactViewModel extends ViewModel {
    private ContactsRepository contactsRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();


    public AddContactViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
    }

    public void saveContact(ContactData contactData) {
        contactsRepository.createContact(contactData)
                .subscribe(new Observer<ContactData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactData contactData) {
                        contactsRepository.saveLocalContact(contactData);
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e, "Saving contact error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }
}