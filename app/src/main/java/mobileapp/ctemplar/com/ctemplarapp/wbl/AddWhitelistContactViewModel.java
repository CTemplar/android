package mobileapp.ctemplar.com.ctemplarapp.wbl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
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
