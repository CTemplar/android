package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.contact.Contact;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;

public class ManageFoldersViewModel extends ViewModel {

    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();

    public ManageFoldersViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void getFolders(int limit, int offset) {
        manageFoldersRepository.getFoldersList(limit, offset)
                .subscribe(new Observer<FoldersResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FoldersResponse response) {
                        foldersResponse.postValue(response);

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
