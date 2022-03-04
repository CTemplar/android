package com.ctemplar.app.fdroid.repository;

import androidx.lifecycle.MutableLiveData;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.RestService;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterOrderListRequest;
import com.ctemplar.app.fdroid.net.request.folders.FolderRequest;
import com.ctemplar.app.fdroid.net.response.PagableResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterOrderListResponse;
import com.ctemplar.app.fdroid.net.response.folders.CustomFolderResponse;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;
import com.ctemplar.app.fdroid.repository.mapper.CustomFolderMapper;
import com.ctemplar.app.fdroid.repository.mapper.PageableMapper;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ManageFoldersRepository {
    private RestService service;

    private static final ManageFoldersRepository instance = new ManageFoldersRepository();

    private final MutableLiveData<DTOResource<PageableDTO<CustomFolderDTO>>> customFoldersLiveData = new MutableLiveData<>();
    private final MutableLiveData<DTOResource<Map<String, Integer>>> unreadFoldersLiveData = new MutableLiveData<>();

    public static ManageFoldersRepository getInstance() {
        return instance;
    }

    public ManageFoldersRepository() {
        CTemplarApp.getRestClientLiveData().observeForever(
                instance -> service = instance.getRestService());
    }

    public Observable<EmailFilterOrderListResponse> updateEmailFiltersOrder(
            EmailFilterOrderListRequest request
    ) {
        return service.updateEmailFiltersOrder(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public MutableLiveData<DTOResource<CustomFolderDTO>> addFolder(String name, String color) {
        MutableLiveData<DTOResource<CustomFolderDTO>> liveData = new MutableLiveData<>();
        service.addFolder(new FolderRequest(name, color))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomFolderResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(CustomFolderResponse response) {
                        liveData.postValue(DTOResource.success(CustomFolderMapper.map(response)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(DTOResource.error(e));
                    }
                });
        return liveData;
    }

    public MutableLiveData<DTOResource<Response<Void>>> deleteFolder(long id) {
        MutableLiveData<DTOResource<Response<Void>>> liveData = new MutableLiveData<>();
        service.deleteFolder(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response<Void> response) {
                        liveData.postValue(DTOResource.success(response));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(DTOResource.error(e));
                    }
                });
        return liveData;
    }

    public MutableLiveData<DTOResource<CustomFolderDTO>> editFolder(long id, String name, String color) {
        MutableLiveData<DTOResource<CustomFolderDTO>> liveData = new MutableLiveData<>();
        service.editFolder(id, new FolderRequest(name, color))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomFolderResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(CustomFolderResponse response) {
                        liveData.postValue(DTOResource.success(CustomFolderMapper.map(response)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(DTOResource.error(e));
                    }
                });
        return liveData;
    }

    public MutableLiveData<DTOResource<PageableDTO<CustomFolderDTO>>> getCustomFoldersLiveData() {
        return customFoldersLiveData;
    }

    public void getCustomFolders(int limit, int offset) {
        service.getCustomFolders(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<PagableResponse<CustomFolderResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(PagableResponse<CustomFolderResponse> response) {
                        customFoldersLiveData.postValue(DTOResource.success(
                                PageableMapper.map(CustomFolderMapper.class, response)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        customFoldersLiveData.postValue(DTOResource.error(e));
                    }
                });
    }

    public MutableLiveData<DTOResource<Map<String, Integer>>> getUnreadFoldersLiveData() {
        return unreadFoldersLiveData;
    }

    public void getUnreadFolders() {
        service.getUnreadFolders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Map<String, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Map<String, Integer> unreadFolders) {
                        unreadFoldersLiveData.postValue(DTOResource.success(unreadFolders));
                    }

                    @Override
                    public void onError(Throwable e) {
                        unreadFoldersLiveData.postValue(DTOResource.error(e));
                    }
                });
    }
}
