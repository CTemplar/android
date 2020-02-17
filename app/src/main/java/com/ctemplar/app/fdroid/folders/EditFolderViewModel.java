package com.ctemplar.app.fdroid.folders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.EditFolderRequest;
import com.ctemplar.app.fdroid.net.response.Folders.FoldersResult;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import retrofit2.Response;

public class EditFolderViewModel extends ViewModel {

    private ManageFoldersRepository manageFoldersRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deletingStatus = new MutableLiveData<>();
    private MutableLiveData<FoldersResult> editResponse = new MutableLiveData<>();

    public EditFolderViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public MutableLiveData<FoldersResult> getEditResponse() {
        return editResponse;
    }

    public MutableLiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public MutableLiveData<ResponseStatus> getDeletingStatus() {
        return deletingStatus;
    }

    public void deleteFolderById(long folderId) {
        manageFoldersRepository.deleteFolder(folderId)
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

    public void editFolder(long folderId, String folderName, String folderColor) {
        EditFolderRequest request = new EditFolderRequest(folderId, folderName, folderColor);
        manageFoldersRepository.editFolder(folderId, request)
                .subscribe(new Observer<FoldersResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FoldersResult foldersResult) {
                        editResponse.postValue(foldersResult);
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
