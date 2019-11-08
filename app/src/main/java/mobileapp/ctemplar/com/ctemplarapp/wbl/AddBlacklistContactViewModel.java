package mobileapp.ctemplar.com.ctemplarapp.wbl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.BlackListContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import timber.log.Timber;

public class AddBlacklistContactViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();

    public AddBlacklistContactViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void addBlacklistContact(String name, String email) {
        userRepository.addBlacklistContact(new BlackListContact(name, email))
        .subscribe(new Observer<BlackListContact>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BlackListContact blackListContact) {
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
