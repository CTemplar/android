package mobileapp.ctemplar.com.ctemplarapp.folders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import okhttp3.ResponseBody;

public class AddFolderViewModel extends ViewModel {

    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();

    public AddFolderViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public void addFolder(String folderName, String folderColor) {
        AddFolderRequest addFolderRequest = new AddFolderRequest(folderName, folderColor);
        manageFoldersRepository.addFolder(addFolderRequest)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        responseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
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
