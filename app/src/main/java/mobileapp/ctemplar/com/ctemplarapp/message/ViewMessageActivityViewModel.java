package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;

public class ViewMessageActivityViewModel extends ViewModel {
    UserRepository userRepository;
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    MutableLiveData<MessagesResult> messageResponse = new MutableLiveData<>();

    public ViewMessageActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public void getMessage(long id) {
        userRepository.getMessage(id)
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResponse messagesResponse) {
                        List<MessagesResult> messagesList = messagesResponse.getMessagesList();
                        if (messagesList != null && messagesList.size() > 0) {
                            messageResponse.postValue(messagesList.get(0));
                            responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                        } else {
                            responseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                        }
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

    MutableLiveData<MessagesResult> getMessageResponse() {
        return messageResponse;
    }

}
