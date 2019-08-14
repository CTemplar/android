package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CustomFilterRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FilterResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FiltersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import retrofit2.Response;
import timber.log.Timber;

public class FiltersViewModel extends ViewModel {
    private UserRepository userRepository;
    private ManageFoldersRepository manageFoldersRepository;

    private MutableLiveData<FiltersResponse> filtersResponse = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> addFilterResponseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deleteFilterResponseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> editFilterResponseStatus = new MutableLiveData<>();

    public MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    public MutableLiveData<FiltersResponse> getFiltersResponse() {
        return filtersResponse;
    }

    public MutableLiveData<ResponseStatus> getAddFilterResponseStatus() {
        return addFilterResponseStatus;
    }

    public MutableLiveData<ResponseStatus> getDeleteFilterResponseStatus() {
        return deleteFilterResponseStatus;
    }

    public MutableLiveData<ResponseStatus> getEditFilterResponseStatus() {
        return editFilterResponseStatus;
    }

    public FiltersViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public void getFilters() {
        userRepository.getFilterList()
                .subscribe(new Observer<FiltersResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FiltersResponse response) {
                        filtersResponse.postValue(response);
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

    public void addFilter(CustomFilterRequest customFilterRequest) {
        userRepository.createFilter(customFilterRequest)
                .subscribe(new Observer<FilterResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FilterResult response) {
                        addFilterResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        addFilterResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void editFilter(long id, CustomFilterRequest customFilterRequest) {
        userRepository.updateFilter(id, customFilterRequest)
                .subscribe(new Observer<FilterResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FilterResult response) {
                        editFilterResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        editFilterResponseStatus.postValue(ResponseStatus.RESPONSE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteFilter(long id) {
        userRepository.deleteFilter(id)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> response) {
                        deleteFilterResponseStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(Throwable e) {
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
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FoldersResponse response) {
                        foldersResponse.postValue(response);
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