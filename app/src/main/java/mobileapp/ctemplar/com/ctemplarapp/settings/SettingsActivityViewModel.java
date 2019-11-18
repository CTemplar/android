package mobileapp.ctemplar.com.ctemplarapp.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AttachmentsEncryptedRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoSaveContactEnabledRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ContactsEncryptionRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoveryEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignatureRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubjectEncryptedRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.EncryptContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import timber.log.Timber;

public class SettingsActivityViewModel extends ViewModel {

    private ContactsRepository contactsRepository;
    private UserRepository userRepository;

    public SettingsActivityViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
        userRepository = CTemplarApp.getUserRepository();
    }

    private MutableLiveData<ResponseStatus> decryptionStatus = new MutableLiveData<>();

    MutableLiveData<ResponseStatus> getDecryptionStatus() {
        return decryptionStatus;
    }

    void updateAutoSaveEnabled(long settingId, boolean isEnabled) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateAutoSaveEnabled(settingId, new AutoSaveContactEnabledRequest(isEnabled))
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void updateSignature(long settingId, long mailboxId, String signatureText) {
        if (settingId == -1 && mailboxId != -1) {
            return;
        }

        userRepository.updateSignature(mailboxId, new SignatureRequest(signatureText))
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void updateRecoveryEmail(long settingId, String newRecoveryEmail) {
        if (settingId == -1) {
            return;
        }

        RecoveryEmailRequest request = new RecoveryEmailRequest(newRecoveryEmail);
        userRepository.updateRecoveryEmail(settingId, request)
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void updateSubjectEncryption(long settingId, boolean isSubjectEncryption) {
        if (settingId == -1) {
            return;
        }

        SubjectEncryptedRequest subjectEncryptedRequest = new SubjectEncryptedRequest(isSubjectEncryption);
        userRepository.updateSubjectEncrypted(settingId, subjectEncryptedRequest)
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.i("Updating subject encryption");
                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {
                        Timber.i("Subject encryption updated");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void updateAttachmentsEncryption(long settingId, boolean isAttachmentsEncrypted) {
        if (settingId == -1) {
            return;
        }

        AttachmentsEncryptedRequest attachmentsEncryptedRequest = new AttachmentsEncryptedRequest(isAttachmentsEncrypted);
        userRepository.updateAttachmentsEncrypted(settingId, attachmentsEncryptedRequest)
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void updateContactsEncryption(long settingId, boolean isContactsEncryption) {
        if (settingId == -1) {
            return;
        }

        ContactsEncryptionRequest contactsEncryptionRequest = new ContactsEncryptionRequest(isContactsEncryption);
        userRepository.updateContactsEncryption(settingId, contactsEncryptionRequest)
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void decryptContacts(int offset) {
        contactsRepository.getContactsList(20, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactsResponse contactsResponse) {
                        ContactData[] contacts = contactsResponse.getResults();
                        for (ContactData contactData : contacts) {
                            updateContact(contactData);
                        }
                        if (contacts.length == 0) {
                            decryptionStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        } else {
                            decryptionStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateContact(ContactData contactData) {
        if (!contactData.isEncrypted()) {
            return;
        }

        Gson gson = new Gson();
        String encryptedData = contactData.getEncryptedData();
        String decryptedData = Contact.decryptData(encryptedData);
        EncryptContact decryptedContact = gson.fromJson(decryptedData, EncryptContact.class);

        contactData.setEmail(decryptedContact.getEmail());
        contactData.setName(decryptedContact.getName());
        contactData.setAddress(decryptedContact.getAddress());
        contactData.setNote(decryptedContact.getNote());
        contactData.setPhone(decryptedContact.getPhone());
        contactData.setPhone2(decryptedContact.getPhone2());
        contactData.setProvider(decryptedContact.getProvider());
        contactData.setEncrypted(false);
        contactData.setEncryptedData("");

        contactsRepository.updateContact(contactData)
                .subscribe(new Observer<ContactData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactData contactData) {
                        contactsRepository.saveLocalContact(contactData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        decryptionStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
