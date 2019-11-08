package mobileapp.ctemplar.com.ctemplarapp.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {

    private UserRepository userRepository;
    private ContactsRepository contactsRepository;
    private MessagesRepository messagesRepository;
    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<MainActivityActions> actions = new SingleLiveEvent<>();
    private MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseMessagesData> messagesResponse = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> toFolderStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteSeveralMessagesStatus = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> unreadFoldersBody = new MutableLiveData<>();
    private MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();
    MutableLiveData<String> currentFolder = new MutableLiveData<>();
    MutableLiveData<SignInResponse> signResponse = new MutableLiveData<>();

    public MainActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        contactsRepository = CTemplarApp.getContactsRepository();
        messagesRepository = CTemplarApp.getMessagesRepository();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public void changeAction(MainActivityActions action) {
        actions.postValue(action);
    }

    public LiveData<DialogState> getDialogStatus() {
        return dialogState;
    }

    public void showProgressDialog() {
        dialogState.postValue(DialogState.SHOW_PROGRESS_DIALOG);
    }

    public void hideProgressDialog() {
        dialogState.postValue(DialogState.HIDE_PROGRESS_DIALOG);
    }

    public LiveData<ResponseStatus> getDeleteSeveralMessagesStatus() {
        return deleteSeveralMessagesStatus;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public LiveData<ResponseStatus> getToFolderStatus() {
        return toFolderStatus;
    }

    MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    MutableLiveData<ResponseBody> getUnreadFoldersBody() {
        return unreadFoldersBody;
    }

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder.postValue(currentFolder);
    }

    public LiveData<String> getCurrentFolder() {
        return currentFolder;
    }

    public LiveData<ResponseMessagesData> getMessagesResponse() {
        return messagesResponse;
    }

    public LiveData<List<Contact>> getContactsResponse() {
        return contactsResponse;
    }

    public MutableLiveData<MyselfResponse> getMyselfResponse() {
        return myselfResponse;
    }

    public void logout() {
        if (userRepository != null) {
            deleteFirebaseToken();
        }
    }

    public void exit() {
        userRepository.logout();
        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }

    public void getMessages(int limit, final int offset, final String folder) {
        if (folder == null) {
            return;
        }
        List<MessageEntity> messageEntities = messagesRepository.getLocalMessagesByFolder(folder);
        List<MessageProvider> messageProviders = MessageProvider.fromMessageEntities(messageEntities);

        if (offset == 0 && !folder.equals(MainFolderNames.STARRED)) {
            ResponseMessagesData localMessagesData = new ResponseMessagesData(messageProviders, folder, offset);
            if (!localMessagesData.messages.isEmpty()) {
                messagesResponse.postValue(localMessagesData);
            }
        }

        Observable<MessagesResponse> messagesResponseObservable
                = folder.equals(MainFolderNames.STARRED) ?
                userRepository.getStarredMessagesList(limit, offset, 1) :
                userRepository.getMessagesList(limit, offset, folder);

        messagesResponseObservable.observeOn(Schedulers.computation())
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final MessagesResponse response) {
                        List<MessagesResult> messages = response.getMessagesList();
                        List<MessageEntity> messageEntities = MessageProvider
                                .fromMessagesResultsToEntities(messages, folder);

                        List<MessageProvider> messageProviders;
                        if (offset > 0 || folder.equals(MainFolderNames.STARRED)) {
                            messageProviders = MessageProvider.fromMessageEntities(messageEntities);
                        } else {
                            messagesRepository.deleteLocalMessagesByFolderName(folder);
                            messagesRepository.addMessagesToDatabase(messageEntities);

                            List<MessageEntity> localEntities = messagesRepository.getLocalMessagesByFolder(folder);
                            messageProviders = MessageProvider.fromMessageEntities(localEntities);
                        }

                        messagesResponse.postValue(new ResponseMessagesData(messageProviders, folder, offset));
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
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

    public void getContacts(int limit, int offset) {
        List<ContactEntity> contactEntities = contactsRepository.getLocalContacts();
        List<Contact> contactList = Contact.fromEntities(contactEntities);
        contactsResponse.postValue(contactList.isEmpty() ? null : contactList);

        contactsRepository.getContactsList(limit, offset)
                .observeOn(Schedulers.computation())
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ContactsResponse response) {
                        ContactData[] contacts = response.getResults();
                        ContactData[] decryptedContacts = Contact.decryptContactData(contacts);

                        contactsRepository.saveContacts(decryptedContacts);
                        List<Contact> contactList = Contact.fromResponseResults(decryptedContacts);

                        contactsResponse.postValue(contactList);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_CONTACTS);
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

    public void getMailboxes(int limit, int offset) {
        userRepository.getMailboxesList(limit, offset)
                .subscribe(new Observer<MailboxesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MailboxesResponse mailboxesResponse) {

                        if (mailboxesResponse.getTotalCount() > 0) {
                            userRepository.saveMailboxes(mailboxesResponse.getMailboxesList());
                        }
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MAILBOXES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).code();
                            switch (code) {
                                case 401:
                                case 403:
                                    signIn();
                                    break;
                            }
                        }
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void signIn() {
        String username = userRepository.getUsername();
        String password = userRepository.getUserPassword();
        SignInRequest signInRequest = new SignInRequest(
                username,
                EncodeUtils.generateHash(username, password)
        );

        userRepository.signIn(signInRequest)
                .subscribe(new Observer<SignInResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            switch (exception.code()) {
                                case 400:
                                    logout();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SignInResponse signInResponse) {
                        userRepository.saveUserToken(signInResponse.getToken());
                    }
                });
    }

    public void deleteContact(final Contact contact) {
        contactsRepository.deleteLocalContact(contact.getId());
        contactsRepository.deleteContact(contact.getId())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

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

    public void deleteMessage(final long messageId) {
        userRepository.deleteMessage(messageId)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        messagesRepository.deleteMessagesByParentId(messageId);
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

    public void deleteSeveralMessages(String messagesId) {
        userRepository.deleteSeveralMessages(messagesId)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> response) {
                        deleteSeveralMessagesStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
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

    public void toFolder(final long messageId, final String folder) {
        userRepository.toFolder(messageId, folder)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        messagesRepository.updateMessageFolderName(messageId, folder);
                        toFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
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

    public void markMessageIsStarred(final long id, final boolean starred) {
        userRepository.markMessageIsStarred(id, starred)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> messageResponse) {
                        int resultCode = messageResponse.code();
                        if (resultCode == 204) {
                            CTemplarApp.getAppDatabase().messageDao().updateIsStarred(id, starred);
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

    public void getFolders(int limit, int offset) {
        manageFoldersRepository.getFoldersList(limit, offset)
                .subscribe(new Observer<FoldersResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FoldersResponse response) {
                        foldersResponse.postValue(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getUnreadFoldersList() {
        manageFoldersRepository.getUnreadFoldersList()
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        unreadFoldersBody.postValue(responseBody);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        Timber.e(e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getUserMyselfInfo() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyselfResponse myselfResponse) {
                        if (myselfResponse != null) {
                            MyselfResult myselfResult = myselfResponse.result[0];
                            SettingsEntity settingsEntity = myselfResult.settings;

                            String timezone = settingsEntity.getTimezone();
                            boolean isAttachmentsEncrypted = settingsEntity.isAttachmentsEncrypted();
                            boolean isContactsEncrypted = settingsEntity.isContactsEncrypted();

                            userRepository.saveTimeZone(timezone);
                            userRepository.setAttachmentsEncryptionEnabled(isAttachmentsEncrypted);
                            userRepository.setContactsEncryptionEnabled(isContactsEncrypted);
                            userRepository.setNotificationsEnabled(true);
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

    public void deleteFirebaseToken() {
        String token = userRepository.getFirebaseToken();
        userRepository.deleteFirebaseToken(token)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        exit();
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
}
