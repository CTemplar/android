package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import retrofit2.Response;

public class ManageFoldersViewModel extends ViewModel {

    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deletingStatus = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();

    public MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public MutableLiveData<ResponseStatus> getDeletingStatus() {
        return deletingStatus;
    }

    public ManageFoldersViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
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

    public void deleteFolder(FoldersResult foldersResult) {
        manageFoldersRepository.deleteFolder(foldersResult.getId())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> responseVoid) {
                        deletingStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        deletingStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
