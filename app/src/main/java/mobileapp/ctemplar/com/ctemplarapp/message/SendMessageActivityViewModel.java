package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.kibotu.pgp.Pgp;

import org.spongycastle.openpgp.PGPException;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import timber.log.Timber;

public class SendMessageActivityViewModel extends ViewModel {

    UserRepository userRepository;
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    MutableLiveData<MessagesResult> messagesResult = new MutableLiveData<>();

    public SendMessageActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public void sendMessage(SendMessageRequest request) {
        String content = request.getContent();
        try {
            content = Pgp.encrypt(content);
        } catch (IOException | PGPException e) {
            Timber.e("Pgp encrypt error: %s", e.getMessage());
        }
        request.setContent(content);
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

    public LiveData<MessagesResult> getMessagesResult() {
        return messagesResult;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }
}
