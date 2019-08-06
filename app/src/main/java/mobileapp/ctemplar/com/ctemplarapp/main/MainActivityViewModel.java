package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.contacts.Contact;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.UnreadFoldersListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.MessagesRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {

    UserRepository userRepository;
    ContactsRepository contactsRepository;
    MessagesRepository messagesRepository;
    ManageFoldersRepository manageFoldersRepository;
    MutableLiveData<MainActivityActions> actions = new SingleLiveEvent<>();
    MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    MutableLiveData<ResponseMessagesData> messagesResponse = new MutableLiveData<>();
    MutableLiveData<List<MessageProvider>> starredMessagesResponse = new MutableLiveData<>();
    MutableLiveData<List<Contact>> contactsResponse = new MutableLiveData<>();
    MutableLiveData<ResponseStatus> toFolderStatus = new MutableLiveData<>();
    MutableLiveData<ResponseStatus> deleteSeveralMessagesStatus = new MutableLiveData<>();
    MutableLiveData<String> currentFolder = new MutableLiveData<>();
    MutableLiveData<SignInResponse> signResponse = new MutableLiveData<>();
    MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    MutableLiveData<UnreadFoldersListResponse> unreadFoldersResponse = new MutableLiveData<>();

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

    public void logout() {
        if (userRepository != null) {
            userRepository.logout();
        }

        actions.postValue(MainActivityActions.ACTION_LOGOUT);
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

    public LiveData<List<MessageProvider>> getStarredMessagesResponse() {
        return starredMessagesResponse;
    }

    public LiveData<List<Contact>> getContactsResponse() {
        return contactsResponse;
    }

    private Observable<MessagesResponse> messagesObservable;
    private CompositeDisposable disposable;

    public void getMessages(int limit, int offset, final String folder) {
        List<MessageEntity> messageEntities = messagesRepository.getLocalMessagesByFolder(folder);
        List<MessageProvider> messageProviders = MessageProvider.fromMessageEntities(messageEntities);
        messagesResponse.postValue(new ResponseMessagesData(messageProviders, folder));

        Observable<MessagesResponse> messagesObservable = userRepository.getMessagesList(limit, offset, folder);
        messagesObservable.subscribe(new Observer<MessagesResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(final MessagesResponse response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MessagesResult> messages = response.getMessagesList();
                        List<MessageEntity> messageEntities = MessageProvider
                                .fromMessagesResultsToEntities(messages, folder);
//                        List<MessageProvider> messageProviders = MessageProvider
//                                .fromMessageEntities(messageEntities); TODO rewrite?

                        messagesRepository.deleteLocalMessagesByFolderName(folder);
                        messagesRepository.addMessagesToDatabase(messageEntities);

                        List<MessageEntity> localEntities = messagesRepository.getLocalMessagesByFolder(folder);
                        List<MessageProvider> messageProviders = MessageProvider.fromMessageEntities(localEntities);
                        messagesResponse.postValue(new ResponseMessagesData(messageProviders, folder));
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
                    }
                }).start();
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

    public void getStarredMessages(int limit, int offset, int starred) {
        userRepository.getStarredMessagesList(limit, offset, starred)
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResponse response) {
                        List<MessagesResult> messages = response.getMessagesList();
                        List<MessageProvider> messageProviders = MessageProvider
                                .fromMessagesResults(messages);

                        starredMessagesResponse.postValue(messageProviders);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
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

    public void getContacts(int limit, int offset) {
        List<ContactEntity> contactEntities = contactsRepository.getLocalContacts();

        List<Contact> contacts = Contact.fromEntities(contactEntities);

        contactsResponse.postValue(contacts);

        contactsRepository.getContactsList(limit, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactsResponse response) {
                        ContactData[] contacts = response.getResults();

                        contactsRepository.saveContacts(contacts);

                        List<Contact> contactsList = Contact.fromResponseResults(contacts);

                        contactsResponse.postValue(contactsList);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_CONTACTS);
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
                EncodeUtils.encodePassword(username, password)
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
                        Timber.e(e, "Delete contact");
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
                        Timber.e(e, "Delete message");
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
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        messagesRepository.updateMessageFolderName(messageId, folder);
                        toFolderStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Move message");
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
                .subscribe(new Observer<UnreadFoldersListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnreadFoldersListResponse unreadFoldersListResponse) {
                        unreadFoldersResponse.postValue(unreadFoldersListResponse);
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

    public LiveData<ResponseStatus> getToFolderStatus() {
        return toFolderStatus;
    }

    MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    MutableLiveData<UnreadFoldersListResponse> getUnreadFoldersResponse() {
        return unreadFoldersResponse;
    }
}