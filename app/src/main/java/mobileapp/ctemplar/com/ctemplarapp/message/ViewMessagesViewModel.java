package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import timber.log.Timber;

public class ViewMessagesViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<MessagesResponse> messagesResponse = new MutableLiveData<>();
    private MutableLiveData<MessagesResult> starredResponse = new MutableLiveData<>();

    public ViewMessagesViewModel() {
        userRepository = UserRepository.getInstance();
    }

    public void getChainMessages(long id) {
        userRepository.getChainMessages(id)
        .subscribe(new Observer<MessagesResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MessagesResponse messagesResponse) {
                ViewMessagesViewModel.this.messagesResponse.postValue(messagesResponse);
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

    public void markMessageIsStarred(long id, boolean starred) {
        userRepository.markMessageIsStarred(id, starred)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult messagesResult) {
                        starredResponse.postValue(messagesResult);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void markMessageAsRead(long id) {
        userRepository.markMessageAsRead(id)
                .subscribe(new Observer<MessagesResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResult messagesResult) {
                        Timber.i("Message marked as read");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public MutableLiveData<MessagesResponse> getMessagesResponse() {
        return messagesResponse;
    }

    MutableLiveData<MessagesResult> getStarredResponse() {
        return starredResponse;
    }
}
