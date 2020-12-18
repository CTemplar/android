package mobileapp.ctemplar.com.ctemplarapp.repository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.net.response.contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import okhttp3.ResponseBody;

public class ContactsRepository {
    private final RestService service;

    private static ContactsRepository instance = new ContactsRepository();

    public static ContactsRepository getInstance() {
        return instance;
    }

    public ContactsRepository() {
        service = CTemplarApp.getRestClient().getRestService();
    }

    public Observable<ContactsResponse> getContacts(int limit, int offset, int[] ids) {
        StringBuilder id__in = new StringBuilder();
        for (int id : ids) {
            id__in.append(id);
            id__in.append(',');
        }
        id__in.deleteCharAt(id__in.length() - 1);

        return service.getContacts(limit, offset, id__in.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ContactsResponse> getContactsList(int limit, int offset) {
        return service.getContacts(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Observable<ContactsResponse> getContact(long id) {
        return service.getContact(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ContactData> createContact(ContactData contactData) {
        return service.createContact(contactData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ContactData> updateContact(ContactData contactData) {
        if (contactData.getId() == 0) {
            throw new IllegalArgumentException("Contact ID should not be 0");
        }
        return updateContact(contactData.getId(), contactData);
    }

    public Observable<ContactData> updateContact(long id, ContactData contactData) {
        return service.updateContact(id, contactData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> deleteContact(long id) {
        return service.deleteContact(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveContact(ContactEntity contactEntity) {
        CTemplarApp.getAppDatabase().contactDao().save(contactEntity);
    }

    public void saveContacts(ContactEntity[] contactEntities) {
        CTemplarApp.getAppDatabase().contactDao().saveAll(contactEntities);
    }

    public ContactEntity getLocalContact(long id) {
        return CTemplarApp.getAppDatabase().contactDao().getById(id);
    }

    public List<ContactEntity> getLocalContacts() {
        return CTemplarApp.getAppDatabase().contactDao().getAll();
    }

    public void deleteLocalContact(long id) {
        CTemplarApp.getAppDatabase().contactDao().delete(id);
    }
}
