package com.ctemplar.app.fdroid.repository;

import androidx.lifecycle.MutableLiveData;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.RestService;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterOrderListRequest;
import com.ctemplar.app.fdroid.net.request.folders.AddFolderRequest;
import com.ctemplar.app.fdroid.net.request.folders.EditFolderRequest;
import com.ctemplar.app.fdroid.net.response.PagableResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterOrderListResponse;
import com.ctemplar.app.fdroid.net.response.folders.CustomFolderResponse;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;
import com.ctemplar.app.fdroid.repository.mapper.CustomFolderMapper;
import com.ctemplar.app.fdroid.repository.mapper.PageableMapper;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ManageFoldersRepository {
    private RestService service;

    private static final ManageFoldersRepository instance = new ManageFoldersRepository();

    private final MutableLiveData<DTOResource<PageableDTO<CustomFolderDTO>>> customFoldersLiveData = new MutableLiveData<>();
    private final MutableLiveData<DTOResource<ResponseBody>> unreadFoldersLiveData = new MutableLiveData<>();

    public static ManageFoldersRepository getInstance() {
        return instance;
    }

    public ManageFoldersRepository() {
        CTemplarApp.getRestClientLiveData().observeForever(
                instance -> service = instance.getRestService());
    }

    public Observable<ResponseBody> addFolder(AddFolderRequest request) {
        return service.addFolder(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<Void>> deleteFolder(long id) {
        return service.deleteFolder(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CustomFolderResponse> editFolder(long id, EditFolderRequest request) {
        return service.editFolder(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<EmailFilterOrderListResponse> updateEmailFiltersOrder(
            EmailFilterOrderListRequest request
    ) {
        return service.updateEmailFiltersOrder(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    public MutableLiveData<DTOResource<ResponseBody>> getUnreadFoldersLiveData() {
        return unreadFoldersLiveData;
    }

    public void getUnreadFolders() {
        service.getUnreadFolders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        unreadFoldersLiveData.postValue(DTOResource.success(responseBody));
                    }

                    @Override
                    public void onError(Throwable e) {
                        unreadFoldersLiveData.postValue(DTOResource.error(e));
                    }
                });
    }
}
