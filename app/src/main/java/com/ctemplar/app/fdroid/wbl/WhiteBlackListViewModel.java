package com.ctemplar.app.fdroid.wbl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.response.myself.BlackListContact;
import com.ctemplar.app.fdroid.net.response.myself.WhiteListContact;
import com.ctemplar.app.fdroid.net.response.whiteBlackList.BlackListResponse;
import com.ctemplar.app.fdroid.net.response.whiteBlackList.WhiteListResponse;
import com.ctemplar.app.fdroid.repository.UserRepository;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class WhiteBlackListViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<BlackListContact[]> blacklistResponse = new MutableLiveData<>();
    private MutableLiveData<WhiteListContact[]> whitelistResponse = new MutableLiveData<>();

    private boolean isBlackListReady = false;
    private boolean isWhiteListReady = false;

    public WhiteBlackListViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public void getWhiteListContacts() {
        userRepository.getWhiteListContacts()
                .subscribe(new Observer<WhiteListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WhiteListResponse whiteListResponse) {
                        whitelistResponse.postValue(whiteListResponse.getResults());
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

    public void getBlackListContacts() {
        userRepository.getBlackListContacts()
                .subscribe(new Observer<BlackListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BlackListResponse blackListResponse) {
                        blacklistResponse.postValue(blackListResponse.getResults());
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

    public LiveData<BlackListContact[]> getBlacklistResponse() {
        return blacklistResponse;
    }

    public LiveData<WhiteListContact[]> getWhitelistResponse() {
        return whitelistResponse;
    }

    public void blackListIsReady() {
        isBlackListReady = true;
        if (isWhiteListReady) {
            getWhiteListContacts();
        }
    }

    public void whiteListIsReady() {
        isWhiteListReady = true;
        if (isBlackListReady) {
            getBlackListContacts();
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
