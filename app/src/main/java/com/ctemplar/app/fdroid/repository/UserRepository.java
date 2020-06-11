package com.ctemplar.app.fdroid.repository;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.RestService;
import com.ctemplar.app.fdroid.net.request.AddAppTokenRequest;
import com.ctemplar.app.fdroid.net.request.AntiPhishingPhraseRequest;
import com.ctemplar.app.fdroid.net.request.AttachmentsEncryptedRequest;
import com.ctemplar.app.fdroid.net.request.AutoSaveContactEnabledRequest;
import com.ctemplar.app.fdroid.net.request.CaptchaVerifyRequest;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.CheckUsernameRequest;
import com.ctemplar.app.fdroid.net.request.ContactsEncryptionRequest;
import com.ctemplar.app.fdroid.net.request.CreateMailboxRequest;
import com.ctemplar.app.fdroid.net.request.CustomFilterRequest;
import com.ctemplar.app.fdroid.net.request.DefaultMailboxRequest;
import com.ctemplar.app.fdroid.net.request.EmptyFolderRequest;
import com.ctemplar.app.fdroid.net.request.EnabledMailboxRequest;
import com.ctemplar.app.fdroid.net.request.MarkMessageAsReadRequest;
import com.ctemplar.app.fdroid.net.request.MarkMessageIsStarredRequest;
import com.ctemplar.app.fdroid.net.request.MoveToFolderRequest;
import com.ctemplar.app.fdroid.net.request.PublicKeysRequest;
import com.ctemplar.app.fdroid.net.request.RecoverPasswordRequest;
import com.ctemplar.app.fdroid.net.request.RecoveryEmailRequest;
import com.ctemplar.app.fdroid.net.request.SendMessageRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.request.SignUpRequest;
import com.ctemplar.app.fdroid.net.request.SignatureRequest;
import com.ctemplar.app.fdroid.net.request.SubjectEncryptedRequest;
import com.ctemplar.app.fdroid.net.response.AddAppTokenResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaVerifyResponse;
import com.ctemplar.app.fdroid.net.response.CheckUsernameResponse;
import com.ctemplar.app.fdroid.net.response.Domains.DomainsResponse;
import com.ctemplar.app.fdroid.net.response.Filters.FilterResult;
import com.ctemplar.app.fdroid.net.response.Filters.FiltersResponse;
import com.ctemplar.app.fdroid.net.response.KeyResponse;
import com.ctemplar.app.fdroid.net.response.Mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.net.response.Mailboxes.MailboxesResult;
import com.ctemplar.app.fdroid.net.response.Messages.EmptyFolderResponse;
import com.ctemplar.app.fdroid.net.response.Messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.Messages.MessagesResponse;
import com.ctemplar.app.fdroid.net.response.Messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.Myself.BlackListContact;
import com.ctemplar.app.fdroid.net.response.Myself.MyselfResponse;
import com.ctemplar.app.fdroid.net.response.Myself.SettingsEntity;
import com.ctemplar.app.fdroid.net.response.Myself.WhiteListContact;
import com.ctemplar.app.fdroid.net.response.RecoverPasswordResponse;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.net.response.SignUpResponse;
import com.ctemplar.app.fdroid.net.response.WhiteBlackLists.BlackListResponse;
import com.ctemplar.app.fdroid.net.response.WhiteBlackLists.WhiteListResponse;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;

import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class UserRepository {

    private static UserRepository instance = new UserRepository();
    private RestService service;
    private UserStore userStore;

    public static UserRepository getInstance() {
        if(instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public UserRepository() {
        service = CTemplarApp.getRestClient().getRestService();
        userStore = CTemplarApp.getUserStore();
    }

    public void clearToken() {
        userStore.clearToken();
    }

    public void saveUserToken(String token) {
        userStore.saveToken(token);
    }

    public String getUserToken() {
        return userStore.getToken();
    }

    public void saveUserPassword(String password) {
        userStore.savePassword(password);
    }

    public String getUserPassword() {
        return userStore.getUserPassword();
    }

    public void saveKeepMeLoggedIn(boolean keepMeLoggedIn) {
        userStore.saveKeepMeLoggedIn(keepMeLoggedIn);
    }

    public boolean getKeepMeLoggedIn() {
        return userStore.getKeepMeLoggedIn();
    }

    public void saveUsername(String username) {
        userStore.saveUsername(username);
    }

    public String getUsername() {
        return userStore.getUsername();
    }

    public void saveTimeZone(String timezone) {
        userStore.saveTimeZone(timezone);
    }

    public String getTimeZone() {
        return userStore.getTimeZone();
    }

    public void setSignatureEnabled(boolean isEnabled) {
        userStore.setSignatureEnabled(isEnabled);
    }

    public boolean isSignatureEnabled() {
        return userStore.getSignatureEnabled();
    }

    public void setMobileSignatureEnabled(boolean isEnabled) {
        userStore.setMobileSignatureEnabled(isEnabled);
    }

    public boolean isMobileSignatureEnabled() {
        return userStore.getMobileSignatureEnabled();
    }

    public void setMobileSignature(String signatureText) {
        userStore.saveMobileSignature(signatureText);
    }

    public String getMobileSignature() {
        return userStore.getMobileSignature();
    }

    public MailboxEntity getDefaultMailbox() {
        MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
        if (mailboxDao.getDefault() == null) {
            if (!mailboxDao.getAll().isEmpty()) {
                return mailboxDao.getAll().get(0);
            } else {
                Timber.e("Mailbox not found");
            }
        } else {
            return mailboxDao.getDefault();
        }
        return new MailboxEntity();
    }

    public void setNotificationsEnabled(boolean isEnabled) {
        userStore.setNotificationsEnabled(isEnabled);
    }

    public boolean isNotificationsEnabled() {
        return userStore.getNotificationsEnabled();
    }

    public void setAttachmentsEncryptionEnabled(boolean state) {
        userStore.setAttachmentsEncryptionEnabled(state);
    }

    public boolean getAttachmentsEncryptionEnabled() {
        return userStore.getAttachmentsEncryptionEnabled();
    }

    public void setContactsEncryptionEnabled(boolean isContactsEncryptionEnabled) {
        userStore.setContactsEncryptionEnabled(isContactsEncryptionEnabled);
    }

    public boolean getContactsEncryptionEnabled() {
        return userStore.getContactsEncryptionEnabled();
    }

    public void clearData() {
        userStore.logout();
        CTemplarApp.getAppDatabase().clearAllTables();
    }

    public void saveMailboxes(List<MailboxesResult> mailboxes) {
        if(mailboxes != null && mailboxes.size()>0) {
            for(int i = 0; i < mailboxes.size(); i++) {
                MailboxesResult result = mailboxes.get(i);

                MailboxEntity entity = new MailboxEntity();
                entity.setId(result.getId());
                entity.setDefault(result.isDefault());
                entity.setDisplayName(result.getDisplayName());
                entity.setEmail(result.getEmail());
                entity.setEnabled(result.isEnabled());
                entity.setFingerprint(result.getFingerprint());
                entity.setPrivateKey(result.getPrivateKey());
                entity.setPublicKey(result.getPublicKey());
                entity.setSignature(result.getSignature());

                CTemplarApp.getAppDatabase().mailboxDao().save(entity);
            }
        }
    }

    // Requests
    public Observable<SignInResponse> signIn(SignInRequest request) {
        return service.signIn(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SignUpResponse> signUp(SignUpRequest request) {
        return service.signUp(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> signOut(String platform, String deviceToken) {
        return service.signOut(platform, deviceToken)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CheckUsernameResponse> checkUsername(CheckUsernameRequest request) {
        return service.checkUsername(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RecoverPasswordResponse> recoverPassword(RecoverPasswordRequest request) {
        return service.recoverPassword(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> changePassword(ChangePasswordRequest request) {
        return service.changePassword(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RecoverPasswordResponse> resetPassword(RecoverPasswordRequest request) {
        return service.resetPassword(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getMessagesList(int limit, int offset, String folder) {
        return service.getMessages(limit, offset, folder)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getStarredMessagesList(int limit, int offset) {
        return service.getStarredMessages(limit, offset, true)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getMessage(long id) {
        return service.getMessage(id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteMessages(String messageIds) {
        return service.deleteMessages(messageIds)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<EmptyFolderResponse> emptyFolder(EmptyFolderRequest request) {
        return service.emptyFolder(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> toFolder(long id, String folder) {
        return service.toFolder(id, new MoveToFolderRequest(folder))
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getChainMessages(long id) {
        return service.getChainMessages(id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> markMessageAsRead(long id, MarkMessageAsReadRequest request) {
        return service.markMessageAsRead(id, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> markMessageIsStarred(long id, boolean starred) {
        return service.markMessageIsStarred(id, new MarkMessageIsStarredRequest(starred))
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MyselfResponse> getMyselfInfo() {
        return service.getMyself()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Observable<MailboxesResponse> getMailboxesList(int limit, int offset) {
        return service.getMailboxes(limit, offset)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResult> updateMessage(long id, SendMessageRequest request) {
        return service.updateMessage(id, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResult> sendMessage(SendMessageRequest request) {
        return service.sendMessage(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessageAttachment> uploadAttachment(MultipartBody.Part attachment,
                                                          long message, boolean isEncrypted) {
        return service.uploadAttachment(attachment, message, isEncrypted)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessageAttachment> updateAttachment(long id, MultipartBody.Part attachment,
                                                          long message, boolean isEncrypted) {
        return service.updateAttachment(id, attachment, message, isEncrypted)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteAttachment(long id) {
        return service.deleteAttachment(id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<KeyResponse> getEmailPublicKeys(PublicKeysRequest request) {
        return service.getKeys(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FiltersResponse> getFilterList() {
        return service.getFilterList()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FilterResult> createFilter(CustomFilterRequest customFilterRequest) {
        return service.createFilter(customFilterRequest)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FilterResult> updateFilter(long id, CustomFilterRequest customFilterRequest) {
        return service.updateFilter(id, customFilterRequest)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteFilter(long id) {
        return service.deleteFilter(id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> deleteBlacklistContact(BlackListContact contact) {
        return service.deleteBlacklistContact(contact.id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> deleteWhitelistContact(WhiteListContact contact) {
        return service.deleteWhitelistContact(contact.id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BlackListResponse> getBlackListContacts() {
        return service.getBlackListContacts()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BlackListContact> addBlacklistContact(BlackListContact contact) {
        return service.addBlacklistContact(contact)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WhiteListResponse> getWhiteListContacts() {
        return service.getWhiteListContacts()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WhiteListContact> addWhitelistContact(WhiteListContact contact) {
        return service.addWhitelistContact(contact)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxesResult> updateDefaultMailbox(long mailboxId,
                                                            DefaultMailboxRequest request) {
        return service.updateDefaultMailbox(mailboxId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxesResult> updateEnabledMailbox(long mailboxId,
                                                            EnabledMailboxRequest request) {
        return service.updateEnabledMailbox(mailboxId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<MailboxesResult>> createMailbox(CreateMailboxRequest request) {
        return service.createMailbox(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DomainsResponse> getDomains() {
        return service.getDomains()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsEntity> updateRecoveryEmail(long settingId,
                                                          RecoveryEmailRequest request) {
        return service.updateRecoveryEmail(settingId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsEntity> updateSubjectEncrypted(long settingId,
                                                             SubjectEncryptedRequest request) {
        return service.updateSubjectEncrypted(settingId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsEntity> updateAttachmentsEncrypted(
            long settingId, AttachmentsEncryptedRequest request) {
        return service.updateAttachmentsEncrypted(settingId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsEntity> updateContactsEncryption(long settingId,
                                                               ContactsEncryptionRequest request) {
        return service.updateContactsEncryption(settingId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsEntity> updateAutoSaveEnabled(long settingId,
                                                            AutoSaveContactEnabledRequest request) {
        return service.updateAutoSaveEnabled(settingId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsEntity> updateAntiPhishingPhrase(long settingId,
                                                               AntiPhishingPhraseRequest request) {
        return service.updateAntiPhishingPhrase(settingId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxesResult> updateSignature(long mailboxId, SignatureRequest request) {
        return service.updateSignature(mailboxId, request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CaptchaResponse> getCaptcha() {
        return service.getCaptcha()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CaptchaVerifyResponse> captchaVerify(CaptchaVerifyRequest request) {
        return service.captchaVerify(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AddAppTokenResponse> addAppToken(AddAppTokenRequest request) {
        return service.addAppToken(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteAppToken(String token) {
        return service.deleteAppToken(token)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}