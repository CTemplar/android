package mobileapp.ctemplar.com.ctemplarapp.repository;

import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

@Singleton
public class UserRepository {
    private RestService service;
    private UserStore userStore;
    private static UserRepository instance = new UserRepository();

//    @Inject
//    public UserRepository(RestService service) {
//        this.service = service;
//    }

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

    public String getUserToken() {
        return userStore.getToken();
    }

    public void clearToken() {
        userStore.clearToken();
    }

    public void logout() {
        userStore.logout();
        CTemplarApp.getAppDatabase().mailboxDao().deleteAll();
    }

    public void saveUserToken(String token) {
        userStore.saveToken(token);
    }

    public void saveMailboxes(List<MailboxesResult> mailboxes) {
        if(mailboxes != null && mailboxes.size()>0) {
            for(int i = 0; i < mailboxes.size(); i++) {
                MailboxesResult result = mailboxes.get(i);

                MailboxEntity entity = new MailboxEntity();
                entity.setId(result.getId());
                entity.setDefault(result.isDefault()?1:0);
                entity.setDisplayName(result.getDisplayName());
                entity.setEmail(result.getEmail());
                entity.setEnabled(result.isEnabled()?1:0);
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

    public Observable<MessagesResponse> getMessage(long id) {
        return service.getMessage(id)
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

    public Observable<MessagesResult> sendMessage(SendMessageRequest request) {
        return service.sendMessage(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveUserPassword(String password) {
        userStore.savePassword(password);
    }

    public void saveUserName(String username) {
        userStore.saveUsername(username);
    }

    public String getUsername() {
        return userStore.getUsername();
    }

    public String getUserPassword() {
        return userStore.getUserPassword();
    }
}