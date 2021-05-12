package com.ctemplar.app.fdroid.filters;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.CustomFilterRequest;
import com.ctemplar.app.fdroid.net.response.filters.FilterResult;
import com.ctemplar.app.fdroid.net.response.filters.FiltersResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import retrofit2.Response;
import timber.log.Timber;

public class FiltersViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final ManageFoldersRepository manageFoldersRepository;

    private final MutableLiveData<FiltersResponse> filtersResponse = new MutableLiveData<>();
    private final MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> addFilterResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deleteFilterResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> editFilterResponseStatus = new MutableLiveData<>();

    MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    MutableLiveData<FiltersResponse> getFiltersResponse() {
        return filtersResponse;
    }

    MutableLiveData<ResponseStatus> getAddFilterResponseStatus() {
        return addFilterResponseStatus;
    }

    MutableLiveData<ResponseStatus> getDeleteFilterResponseStatus() {
        return deleteFilterResponseStatus;
    }

    MutableLiveData<ResponseStatus> getEditFilterResponseStatus() {
        return editFilterResponseStatus;
    }

    public FiltersViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    void getFilters() {
        userRepository.getFilterList()
                .subscribe(new Observer<FiltersResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FiltersResponse response) {
                        filtersResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void addFilter(CustomFilterRequest customFilterRequest) {
        userRepository.createFilter(customFilterRequest)
                .subscribe(new Observer<FilterResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FilterResult response) {
                        addFilterResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        addFilterResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void editFilter(long id, CustomFilterRequest customFilterRequest) {
        userRepository.updateFilter(id, customFilterRequest)
                .subscribe(new Observer<FilterResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FilterResult response) {
                        editFilterResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        editFilterResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void deleteFilter(long id) {
        userRepository.deleteFilter(id)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> response) {
                        deleteFilterResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        deleteFilterResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getFolders(int limit, int offset) {
        manageFoldersRepository.getFoldersList(limit, offset)
                .subscribe(new Observer<FoldersResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FoldersResponse response) {
                        foldersResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
