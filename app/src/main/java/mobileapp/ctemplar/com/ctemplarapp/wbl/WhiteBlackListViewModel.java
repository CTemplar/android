package mobileapp.ctemplar.com.ctemplarapp.wbl;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.BlackListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class WhiteBlackListViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<BlackListContact[]> blacklistResponse = new MutableLiveData<>();
    private MutableLiveData<WhiteListContact[]> whitelistResponse = new MutableLiveData<>();

    private boolean isBlackListReady = false;
    private boolean isWhiteListReady = false;


    public WhiteBlackListViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public void getBlackListWhiteListContacts() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyselfResponse myselfResponse) {
                        blacklistResponse.postValue(myselfResponse.result[0].blacklist);
                        whitelistResponse.postValue(myselfResponse.result[0].whitelist);
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

    public LiveData<BlackListContact[]> getBlacklistResponse() {
        return blacklistResponse;
    }

    public LiveData<WhiteListContact[]> getWhitelistResponse() {
        return whitelistResponse;
    }

    public void blackListIsReady() {
        isBlackListReady = true;
        if (isWhiteListReady) {
            getBlackListWhiteListContacts();
        }
    }

    public void whiteListIsReady() {
        isWhiteListReady = true;
        if (isBlackListReady) {
            getBlackListWhiteListContacts();
        }
    }

    public void deleteBlacklistContact(BlackListContact contact) {
        userRepository.deleteBlacklistContact(contact)
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

    public void deleteWhitelistContact(WhiteListContact contact) {
        userRepository.deleteWhitelistContact(contact)
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
}
