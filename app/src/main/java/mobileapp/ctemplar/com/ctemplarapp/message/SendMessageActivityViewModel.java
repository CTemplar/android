package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.ContactsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;

public class SendMessageActivityViewModel extends ViewModel {

    UserRepository userRepository;
    ContactsRepository contactsRepository;
    MailboxEntity currentMailbox;
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();
    MutableLiveData<ContactsResponse> contactsResponse = new MutableLiveData<>();
    MutableLiveData<KeyResponse> keyResponse = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        contactsRepository = CTemplarApp.getContactsRepository();
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
    }

    public LiveData<ContactsResponse> getContactsResponse() {
        return contactsResponse;
    }

    public void sendMessage(SendMessageRequest request, ArrayList<String> receiverPublicKeys) {
        String content = request.getContent();

        receiverPublicKeys.add(currentMailbox.getPublicKey());
        String[] publicKeys = receiverPublicKeys.toArray(new String[0]);

        PGPManager pgpManager = new PGPManager();
        String encryptedContent = pgpManager.encryptMessage(content, publicKeys);

        request.setContent(encryptedContent);
        userRepository.sendMessage(request)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult result) {
                        messagesResult.postValue(result);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_MESSAGES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getEmailPublicKeys(PublicKeysRequest request) {
        userRepository.getEmailPublicKeys(request)
                .subscribe(new Observer<KeyResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KeyResponse keyResponse) {
                        SendMessageActivityViewModel.this.keyResponse.postValue(keyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getContacts(int limit, int offset) {
        contactsRepository.getContactsList(limit, offset)
                .subscribe(new Observer<ContactsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactsResponse response) {
                        contactsResponse.postValue(response);
                        responseStatus.postValue(ResponseStatus.RESPONSE_NEXT_CONTACTS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<MessagesResult> getMessagesResult() {
        return messagesResult;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public List<MailboxEntity> getMailboxes() {
        return CTemplarApp.getAppDatabase().mailboxDao().getAll();
    }

    public LiveData<KeyResponse> getKeyResponse() {
        return keyResponse;
    }
}
