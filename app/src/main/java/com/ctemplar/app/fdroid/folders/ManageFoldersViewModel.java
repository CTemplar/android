package com.ctemplar.app.fdroid.folders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.myself.MyselfResponse;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;
import timber.log.Timber;

public class ManageFoldersViewModel extends ViewModel {
    private final ManageFoldersRepository manageFoldersRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deletingStatus = new MutableLiveData<>();
    private final MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();

    public LiveData<ResponseStatus> getResponseStatus() {
        return responseStatus;
    }

    public MutableLiveData<ResponseStatus> getDeletingStatus() {
        return deletingStatus;
    }

    public LiveData<MyselfResponse> getMySelfResponse() {
        return myselfResponse;
    }

    public ManageFoldersViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
        userRepository = CTemplarApp.getUserRepository();
    }

    public MutableLiveData<DTOResource<PageableDTO<CustomFolderDTO>>> getCustomFoldersLiveData() {
        return manageFoldersRepository.getCustomFoldersLiveData();
    }

    public void getCustomFolders(int limit, int offset) {
        manageFoldersRepository.getCustomFolders(limit, offset);
    }

    public void deleteFolder(int folderId) {
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

    public void getMyselfData() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyselfResponse response) {
                        myselfResponse.postValue(response);
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
}
