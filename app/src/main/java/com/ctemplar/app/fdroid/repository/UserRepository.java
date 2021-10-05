package com.ctemplar.app.fdroid.repository;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.RestService;
import com.ctemplar.app.fdroid.net.request.AddAppTokenRequest;
import com.ctemplar.app.fdroid.net.request.AntiPhishingPhraseRequest;
import com.ctemplar.app.fdroid.net.request.AutoSaveContactEnabledRequest;
import com.ctemplar.app.fdroid.net.request.CaptchaVerifyRequest;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.CheckUsernameRequest;
import com.ctemplar.app.fdroid.net.request.DarkModeRequest;
import com.ctemplar.app.fdroid.net.request.DisableLoadingImagesRequest;
import com.ctemplar.app.fdroid.net.request.NotificationEmailRequest;
import com.ctemplar.app.fdroid.net.request.PublicKeysRequest;
import com.ctemplar.app.fdroid.net.request.RecoverPasswordRequest;
import com.ctemplar.app.fdroid.net.request.RecoveryEmailRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.request.SignUpRequest;
import com.ctemplar.app.fdroid.net.request.SignatureRequest;
import com.ctemplar.app.fdroid.net.request.SubjectEncryptedRequest;
import com.ctemplar.app.fdroid.net.request.UpdateReportBugsRequest;
import com.ctemplar.app.fdroid.net.request.contacts.ContactsEncryptionRequest;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterRequest;
import com.ctemplar.app.fdroid.net.request.folders.EmptyFolderRequest;
import com.ctemplar.app.fdroid.net.request.folders.MoveToFolderRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DefaultMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DeleteMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.EnabledMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.UpdateMailboxPrimaryKeyRequest;
import com.ctemplar.app.fdroid.net.request.messages.MarkMessageAsReadRequest;
import com.ctemplar.app.fdroid.net.request.messages.MarkMessageIsStarredRequest;
import com.ctemplar.app.fdroid.net.request.messages.SendMessageRequest;
import com.ctemplar.app.fdroid.net.response.AddAppTokenResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaVerifyResponse;
import com.ctemplar.app.fdroid.net.response.CheckUsernameResponse;
import com.ctemplar.app.fdroid.net.response.RecoverPasswordResponse;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.net.response.SignUpResponse;
import com.ctemplar.app.fdroid.net.response.domains.DomainsResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterResult;
import com.ctemplar.app.fdroid.net.response.keys.KeysResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeyResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeysResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.net.response.messages.EmptyFolderResponse;
import com.ctemplar.app.fdroid.net.response.messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResponse;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.myself.BlackListContact;
import com.ctemplar.app.fdroid.net.response.myself.MyselfResponse;
import com.ctemplar.app.fdroid.net.response.myself.SettingsResponse;
import com.ctemplar.app.fdroid.net.response.myself.WhiteListContact;
import com.ctemplar.app.fdroid.net.response.whiteBlackList.BlackListResponse;
import com.ctemplar.app.fdroid.net.response.whiteBlackList.WhiteListResponse;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class UserRepository {
    private static UserRepository instance = new UserRepository();
    private RestService service;
    private final UserStore userStore;

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public UserRepository() {
        CTemplarApp.getRestClientLiveData().observeForever(instance -> service = instance.getRestService());
        userStore = CTemplarApp.getUserStore();
    }

    public RestService getRestService() {
        return service;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void clearToken() {
        userStore.clearToken();
    }

    public void saveUserToken(String token) {
        userStore.saveUserToken(token);
    }

    public String getUserToken() {
        return userStore.getUserToken();
    }

    public boolean isAuthorized() {
        return EditTextUtils.isNotEmpty(userStore.getUserToken());
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
        return userStore.isSignatureEnabled();
    }

    public boolean isDraftsAutoSaveEnabled() {
        return userStore.isDraftsAutoSaveEnabled();
    }

    public void setNotificationsEnabled(boolean isEnabled) {
        userStore.setNotificationsEnabled(isEnabled);
    }

    public boolean isNotificationsEnabled() {
        return userStore.isNotificationsEnabled();
    }

    public void setContactsEncryptionEnabled(boolean isContactsEncryptionEnabled) {
        userStore.setContactsEncryptionEnabled(isContactsEncryptionEnabled);
    }

    public boolean getContactsEncryptionEnabled() {
        return userStore.isContactsEncryptionEnabled();
    }

    public void setBlockExternalImagesEnabled(boolean isEnabled) {
        userStore.setBlockExternalImagesEnabled(isEnabled);
    }

    public boolean isBlockExternalImagesEnabled() {
        return userStore.isBlockExternalImagesEnabled();
    }

    public void setReportBugsEnabled(boolean isEnabled) {
        userStore.setReportBugsEnabled(isEnabled);
    }

    public void setDarkModeValue(int value) {
        userStore.setDarkModeValue(value);
    }

    public boolean isKeepDecryptedSubjectsEnabled() {
        return userStore.isKeepDecryptedSubjectsEnabled();
    }

    public void clearData() {
        userStore.logout();
        CTemplarApp.getAppDatabase().clearAllTables();
    }

    public void saveMailbox(MailboxEntity mailbox) {
        if (mailbox == null) {
            Timber.e("Mailbox is null");
            return;
        }
        CTemplarApp.getAppDatabase().mailboxDao().save(mailbox);
    }

    public void saveMailboxes(List<MailboxEntity> mailboxes) {
        if (mailboxes == null) {
            Timber.e("Mailboxes is null");
            return;
        }
        MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
        mailboxDao.deleteAll();
        mailboxDao.saveAll(mailboxes);
    }

    public void saveMailboxKey(MailboxKeyEntity mailboxKey) {
        if (mailboxKey == null) {
            Timber.e("MailboxKey is null");
            return;
        }
        CTemplarApp.getAppDatabase().mailboxKeyDao().save(mailboxKey);
    }

    public void saveMailboxKeys(List<MailboxKeyEntity> mailboxKeys) {
        if (mailboxKeys == null) {
            Timber.e("Mailbox keys is null");
            return;
        }
        MailboxKeyDao mailboxKeyDao = CTemplarApp.getAppDatabase().mailboxKeyDao();
        mailboxKeyDao.deleteAll();
        mailboxKeyDao.saveAll(mailboxKeys);
    }

    // Requests
    public Observable<SignInResponse> signIn(SignInRequest request) {
        return service.signIn(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<SignUpResponse> signUp(SignUpRequest request) {
        return service.signUp(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> signOut(String platform, String deviceToken) {
        return service.signOut(platform, deviceToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CheckUsernameResponse> checkUsername(CheckUsernameRequest request) {
        return service.checkUsername(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RecoverPasswordResponse> recoverPassword(RecoverPasswordRequest request) {
        return service.recoverPassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ResponseBody> changePassword(ChangePasswordRequest request) {
        return service.changePassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<RecoverPasswordResponse> resetPassword(RecoverPasswordRequest request) {
        return service.resetPassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getMessagesList(int limit, int offset, String folder) {
        return service.getMessages(limit, offset, folder)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Observable<MessagesResponse> getStarredMessagesList(int limit, int offset) {
        return service.getStarredMessages(limit, offset, true)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Observable<MessagesResponse> searchMessages(String query, int limit, int offset) {
        return service.searchMessages(query, limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Observable<MessagesResponse> getMessage(long id) {
        return service.getMessage(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteMessages(String messageIds) {
        return service.deleteMessages(messageIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<EmptyFolderResponse> emptyFolder(EmptyFolderRequest request) {
        return service.emptyFolder(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> toFolder(long id, String folder) {
        return service.toFolder(id, new MoveToFolderRequest(folder))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getChainMessages(long id) {
        return service.getChainMessages(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Observable<Response<Void>> markMessageAsRead(long id, MarkMessageAsReadRequest request) {
        return service.markMessageAsRead(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> markMessageIsStarred(long id, boolean starred) {
        return service.markMessageIsStarred(id, new MarkMessageIsStarredRequest(starred))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MyselfResponse> getMyselfInfo() {
        return service.getMyself()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxesResponse> getMailboxes(int limit, int offset) {
        return service.getMailboxes(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MailboxKeysResponse> getMailboxKeys(int limit, int offset) {
        return service.getMailboxKeys(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<MailboxKeyResponse>> createMailboxKey(CreateMailboxKeyRequest request) {
        return service.createMailboxKey(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<Void>> deleteMailboxKey(long id, DeleteMailboxKeyRequest request) {
        return service.deleteMailboxKey(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MessagesResult> updateMessage(long id, SendMessageRequest request) {
        return service.updateMessage(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public MessagesResult updateMessageSync(long id, SendMessageRequest request) {
        return service.updateMessage(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingGet();
    }

    public Observable<MessagesResult> sendMessage(SendMessageRequest request) {
        return service.sendMessage(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessageAttachment> uploadAttachment(
            MultipartBody.Part document,
            long message,
            boolean isInline,
            boolean isEncrypted,
            String fileType,
            String name,
            long actualSize
    ) {
        return service.uploadAttachment(
                document, message, isInline, isEncrypted, fileType, name, actualSize
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MessageAttachment> updateAttachment(
            long id,
            MultipartBody.Part document,
            long message,
            boolean isInline,
            boolean isEncrypted,
            String fileType,
            String name,
            long actualSize
    ) {
        return service.updateAttachment(
                id, document, message, isInline, isEncrypted, fileType, name, actualSize
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public MessageAttachment updateAttachmentSync(
            long id,
            MultipartBody.Part document,
            long messageId,
            boolean isInline,
            boolean isEncrypted,
            String fileType,
            String name,
            long actualSize
    ) {
        return service.updateAttachment(
                id, document, messageId, isInline, isEncrypted, fileType, name, actualSize
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingGet();
    }

    public Observable<Response<Void>> deleteAttachment(long id) {
        return service.deleteAttachment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<KeysResponse> getEmailPublicKeys(PublicKeysRequest request) {
        return service.getKeys(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<EmailFilterResponse> getFilterList() {
        return service.getFilterList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<EmailFilterResult> createFilter(EmailFilterRequest emailFilterRequest) {
        return service.createFilter(emailFilterRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<EmailFilterResult> updateFilter(long id, EmailFilterRequest emailFilterRequest) {
        return service.updateFilter(id, emailFilterRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteFilter(long id) {
        return service.deleteFilter(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> deleteBlacklistContact(BlackListContact contact) {
        return service.deleteBlacklistContact(contact.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> deleteWhitelistContact(WhiteListContact contact) {
        return service.deleteWhitelistContact(contact.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BlackListResponse> getBlackListContacts() {
        return service.getBlackListContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BlackListContact> addBlacklistContact(BlackListContact contact) {
        return service.addBlacklistContact(contact)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WhiteListResponse> getWhiteListContacts() {
        return service.getWhiteListContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WhiteListContact> addWhitelistContact(WhiteListContact contact) {
        return service.addWhitelistContact(contact)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxResponse> updateDefaultMailbox(
            long mailboxId,
            DefaultMailboxRequest request
    ) {
        return service.updateDefaultMailbox(mailboxId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxResponse> updateEnabledMailbox(
            long mailboxId,
            EnabledMailboxRequest request
    ) {
        return service.updateEnabledMailbox(mailboxId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<MailboxResponse>> createMailbox(CreateMailboxRequest request) {
        return service.createMailbox(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<Void>> updateMailboxPrimaryKey(UpdateMailboxPrimaryKeyRequest request) {
        return service.updateMailboxPrimaryKey(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DomainsResponse> getDomains() {
        return service.getDomains()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateRecoveryEmail(
            long settingId,
            RecoveryEmailRequest request
    ) {
        return service.updateRecoveryEmail(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateNotificationEmail(
            long settingId,
            NotificationEmailRequest request
    ) {
        return service.updateNotificationEmail(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateSubjectEncrypted(
            long settingId,
            SubjectEncryptedRequest request
    ) {
        return service.updateSubjectEncrypted(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateContactsEncryption(
            long settingId,
            ContactsEncryptionRequest request
    ) {
        return service.updateContactsEncryption(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateAutoSaveEnabled(
            long settingId,
            AutoSaveContactEnabledRequest request
    ) {
        return service.updateAutoSaveEnabled(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateAntiPhishingPhrase(
            long settingId,
            AntiPhishingPhraseRequest request
    ) {
        return service.updateAntiPhishingPhrase(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateDarkMode(
            long settingId,
            DarkModeRequest request
    ) {
        return service.updateDarkMode(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateDisableLoadingImages(
            long settingId,
            DisableLoadingImagesRequest request
    ) {
        return service.updateDisableLoadingImages(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SettingsResponse> updateReportBugs(
            long settingId,
            UpdateReportBugsRequest request
    ) {
        return service.updateReportBugs(settingId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MailboxResponse> updateSignature(long mailboxId, SignatureRequest request) {
        return service.updateSignature(mailboxId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CaptchaResponse> getCaptcha() {
        return service.getCaptcha()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CaptchaVerifyResponse> captchaVerify(CaptchaVerifyRequest request) {
        return service.captchaVerify(request)
                .subscribeOn(Schedulers.io())
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
