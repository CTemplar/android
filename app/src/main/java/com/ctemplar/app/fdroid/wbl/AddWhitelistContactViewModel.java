package com.ctemplar.app.fdroid.wbl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.Myself.WhiteListContact;
import com.ctemplar.app.fdroid.repository.UserRepository;
import timber.log.Timber;

public class AddWhitelistContactViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();

    public AddWhitelistContactViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void addWhitelistContact(String name, String email) {
        userRepository.addWhitelistContact(new WhiteListContact(name, email))
                .subscribe(new Observer<WhiteListContact>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WhiteListContact whiteListContact) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
