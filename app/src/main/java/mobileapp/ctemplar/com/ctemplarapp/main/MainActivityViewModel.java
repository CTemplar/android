package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;

public class MainActivityViewModel extends ViewModel {
    UserRepository userRepository;
    MutableLiveData<MainActivityActions> actions = new SingleLiveEvent<>();
    MutableLiveData<DialogState> dialogState = new SingleLiveEvent<>();
    MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    MutableLiveData<MessagesResponse> messagesResponse = new MutableLiveData<>();
    MutableLiveData<String> currentFolder = new MutableLiveData<String>();

    public MainActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public LiveData<MainActivityActions> getActionsStatus() {
        return actions;
    }

    public void changeAction(MainActivityActions action) {
        actions.postValue(action);
    }

    public LiveData<DialogState> getDialogStatus() {
        return dialogState;
    }

    public void showProgressDialog() {
        dialogState.postValue(DialogState.SHOW_PROGRESS_DIALOG);
    }

    public void hideProgressDialog() {
        dialogState.postValue(DialogState.HIDE_PROGRESS_DIALOG);
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void logout() {
        if(userRepository != null) {
            userRepository.logout();
        }

        actions.postValue(MainActivityActions.ACTION_LOGOUT);
    }

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder.postValue(currentFolder);
    }

    public LiveData<String> getCurrentFolder() {
        return currentFolder;
    }

    public LiveData<MessagesResponse> getMessagesResponse() {
        return messagesResponse;
    }

    public void getMessages(int limit, int offset, String folder) {
        userRepository.getMessagesList(limit, offset, folder)
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResponse response) {
                        messagesResponse.postValue(response);
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
}
