package com.ctemplar.app.fdroid.contacts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.contacts.ContactData;
import com.ctemplar.app.fdroid.net.response.contacts.ContactsResponse;
import com.ctemplar.app.fdroid.repository.ContactsRepository;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.entity.Contact;
import com.ctemplar.app.fdroid.repository.entity.ContactEntity;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class ContactsViewModel extends ViewModel {
    private final ContactsRepository contactsRepository;
    private final UserStore userStore;

    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Contact>> contactsResponse = new MutableLiveData<>();
    private final MutableLiveData<Contact> contactResponse = new MutableLiveData<>();

    public ContactsViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
        userStore = CTemplarApp.getUserStore();
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public MutableLiveData<List<Contact>> getContactsResponse() {
        return contactsResponse;
    }

    public MutableLiveData<Contact> getContactResponse() {
        return contactResponse;
    }

    public void getContacts(int limit, int offset) {
        List<ContactEntity> contactLocalEntities = contactsRepository.getLocalContacts();
        Single.fromCallable(() -> Contact.fromEntities(contactLocalEntities))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new SingleObserver<List<Contact>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Contact> contacts) {
                        contactsResponse.postValue(contacts.isEmpty() ? null : contacts);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                    }
                });

        contactsRepository.getContactsList(limit, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(final @NotNull ContactsResponse response) {
                        ContactData[] contactData = response.getResults();
                        ContactEntity[] contactEntities = Contact.fromContactDataToEntities(contactData);
                        contactsRepository.saveContacts(contactEntities);

                        List<ContactEntity> localEntities = contactsRepository.getLocalContacts();
                        List<Contact> contactList = Contact.fromEntities(localEntities);

                        contactsResponse.postValue(contactList);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_CONTACTS);
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

    public void deleteContact(final Contact contact) {
        contactsRepository.deleteContact(contact.getId())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull ResponseBody responseBody) {
                        contactsRepository.deleteLocalContact(contact.getId());
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getContact(long id) {
        ContactEntity contactEntity = contactsRepository.getLocalContact(id);
        Single.fromCallable(() -> Contact.fromEntity(contactEntity))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new SingleObserver<Contact>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Contact contact) {
                        contactResponse.postValue(contact);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        contactResponse.postValue(null);
                        Timber.e(e);
                    }
                });
    }

    public void updateContact(ContactData contactData) {
        boolean contactsEncryption = userStore.isContactsEncryptionEnabled();
        if (contactsEncryption) {
            contactData.encryptContactData();
        }

        contactsRepository.updateContact(contactData)
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
