package com.ctemplar.app.fdroid.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.AntiPhishingPhraseRequest;
import com.ctemplar.app.fdroid.net.request.AutoSaveContactEnabledRequest;
import com.ctemplar.app.fdroid.net.request.ContactsEncryptionRequest;
import com.ctemplar.app.fdroid.net.request.DarkModeRequest;
import com.ctemplar.app.fdroid.net.request.DisableLoadingImagesRequest;
import com.ctemplar.app.fdroid.net.request.NotificationEmailRequest;
import com.ctemplar.app.fdroid.net.request.RecoveryEmailRequest;
import com.ctemplar.app.fdroid.net.request.SignatureRequest;
import com.ctemplar.app.fdroid.net.request.SubjectEncryptedRequest;
import com.ctemplar.app.fdroid.net.request.UpdateReportBugsRequest;
import com.ctemplar.app.fdroid.net.response.contacts.ContactData;
import com.ctemplar.app.fdroid.net.response.contacts.ContactsResponse;
import com.ctemplar.app.fdroid.net.response.contacts.EncryptContact;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxesResult;
import com.ctemplar.app.fdroid.net.response.myself.MyselfResponse;
import com.ctemplar.app.fdroid.net.response.myself.SettingsResponse;
import com.ctemplar.app.fdroid.repository.AppDatabase;
import com.ctemplar.app.fdroid.repository.ContactsRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.entity.Contact;
import com.ctemplar.app.fdroid.repository.entity.ContactEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.utils.EncryptUtils;
import timber.log.Timber;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

public class SettingsViewModel extends ViewModel {
    private final ContactsRepository contactsRepository;
    private final UserRepository userRepository;
    private final AppDatabase appDatabase;

    public SettingsViewModel() {
        contactsRepository = CTemplarApp.getContactsRepository();
        userRepository = CTemplarApp.getUserRepository();
        appDatabase = CTemplarApp.getAppDatabase();
    }

    private final MutableLiveData<ResponseStatus> decryptionStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> updateSignatureStatus = new MutableLiveData<>();
    private final MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();

    MutableLiveData<ResponseStatus> getDecryptionStatus() {
        return decryptionStatus;
    }

    LiveData<ResponseStatus> getUpdateSignatureStatus() {
        return updateSignatureStatus;
    }

    LiveData<MyselfResponse> getMySelfResponse() {
        return myselfResponse;
    }

    List<MailboxEntity> getAllMailboxes() {
        return appDatabase.mailboxDao().getAll();
    }

    public void setSignatureEnabled(boolean isEnabled) {
        userRepository.setSignatureEnabled(isEnabled);
    }

    public boolean isSignatureEnabled() {
        return userRepository.isSignatureEnabled();
    }

    void updateAutoSaveContactsEnabled(long settingId, boolean isEnabled) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateAutoSaveEnabled(
                settingId,
                new AutoSaveContactEnabledRequest(isEnabled)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("AutoSave contacts updated");
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

    public void updateDarkMode(long settingId, boolean isEnabled) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateDarkMode(
                settingId,
                new DarkModeRequest(isEnabled)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SettingsResponse settingsResponse) {
                        Timber.i("Dark mode state is changed");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateDisableLoadingImages(long settingId, boolean isEnabled) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateDisableLoadingImages(
                settingId,
                new DisableLoadingImagesRequest(isEnabled)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("Disable loading images updated");
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

    public void updateReportBugs(long settingId, boolean isEnabled) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateReportBugs(
                settingId,
                new UpdateReportBugsRequest(isEnabled)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("Report bugs setting updated");
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

    void updateSignature(long mailboxId, String displayName, String signatureText) {
        if (mailboxId == -1) {
            return;
        }
        userRepository.updateSignature(
                mailboxId,
                new SignatureRequest(displayName, signatureText)
        )
                .subscribe(new Observer<MailboxesResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MailboxesResult mailboxesResult) {
                        appDatabase.mailboxDao().updateSignature(mailboxId, displayName, signatureText);
                        updateSignatureStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        updateSignatureStatus.postValue(ResponseStatus.RESPONSE_ERROR);
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
        userRepository.updateRecoveryEmail(
                settingId,
                new RecoveryEmailRequest(newRecoveryEmail)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("Recovery email updated");
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

    void updateNotificationEmail(long settingId, String emailAddress) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateNotificationEmail(
                settingId,
                new NotificationEmailRequest(emailAddress)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SettingsResponse settingsResponse) {
                        Timber.i("Notification email updated");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
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
        userRepository.updateSubjectEncrypted(
                settingId,
                new SubjectEncryptedRequest(isSubjectEncryption)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Timber.i("Updating subject encryption");
                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("Subject encryption updated");
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

    void updateContactsEncryption(long settingId, boolean isContactsEncryption) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateContactsEncryption(
                settingId,
                new ContactsEncryptionRequest(isContactsEncryption)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("Contacts encryption updated");
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

    public void decryptContacts(int offset) {
        contactsRepository.getContactsList(20, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull ContactsResponse contactsResponse) {
                        ContactData[] contacts = contactsResponse.getResults();
                        for (ContactData contactData : contacts) {
                            decryptContact(contactData);
                        }
                        if (contacts.length == 0) {
                            decryptionStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        } else {
                            decryptionStatus.postValue(ResponseStatus.RESPONSE_NEXT);
                        }
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

    void updateAntiPhishingPhrase(long settingId, boolean antiPhishingEnabled, String antiPhishingPhrase) {
        if (settingId == -1) {
            return;
        }
        userRepository.updateAntiPhishingPhrase(
                settingId,
                new AntiPhishingPhraseRequest(antiPhishingEnabled, antiPhishingPhrase)
        )
                .subscribe(new Observer<SettingsResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull SettingsResponse settingsResponse) {
                        Timber.i("AntiPhishing phrase updated");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.w(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMyselfInfo() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull MyselfResponse response) {
                        myselfResponse.postValue(response);
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

    private void decryptContact(ContactData contactData) {
        if (!contactData.isEncrypted()) {
            return;
        }
        String encryptedData = contactData.getEncryptedData();
        String decryptedData = EncryptUtils.decryptData(encryptedData);
        EncryptContact decryptedContact = GENERAL_GSON.fromJson(decryptedData, EncryptContact.class);
        if (decryptedContact == null) {
            return;
        }
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull ContactData contactData) {
                        ContactEntity contactEntity = Contact.fromContactDataToEntity(contactData);
                        contactsRepository.saveContact(contactEntity);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        decryptionStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
