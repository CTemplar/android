package com.ctemplar.app.fdroid.settings.filters;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterOrderListRequest;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterRequest;
import com.ctemplar.app.fdroid.net.response.HttpErrorResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterOrderListResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterResult;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.UserRepository;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

public class FiltersViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final ManageFoldersRepository manageFoldersRepository;

    private final MutableLiveData<EmailFilterResponse> filtersResponse = new MutableLiveData<>();
    private final MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> addFilterResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> deleteFilterResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<ResponseStatus> editFilterResponseStatus = new MutableLiveData<>();
    private final MutableLiveData<EmailFilterOrderListResponse> filterOrderListResponse = new MutableLiveData<>();
    private final MutableLiveData<String> filterOrderListErrorResponse = new MutableLiveData<>();

    MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

    MutableLiveData<EmailFilterResponse> getFiltersResponse() {
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

    MutableLiveData<EmailFilterOrderListResponse> getEmailFilterOrderListResponse() {
        return filterOrderListResponse;
    }

    MutableLiveData<String> getFilterOrderListErrorResponse() {
        return filterOrderListErrorResponse;
    }

    public FiltersViewModel() {
        userRepository = CTemplarApp.getUserRepository();
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public void getFilters() {
        userRepository.getFilterList()
                .subscribe(new Observer<EmailFilterResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull EmailFilterResponse response) {
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

    public void addFilter(EmailFilterRequest emailFilterRequest) {
        userRepository.createFilter(emailFilterRequest)
                .subscribe(new Observer<EmailFilterResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull EmailFilterResult response) {
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

    public void editFilter(long id, EmailFilterRequest emailFilterRequest) {
        userRepository.updateFilter(id, emailFilterRequest)
                .subscribe(new Observer<EmailFilterResult>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull EmailFilterResult response) {
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

    public void deleteFilter(long id) {
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

    public void updateEmailFiltersOrder(EmailFilterOrderListRequest request) {
        manageFoldersRepository.updateEmailFiltersOrder(request)
                .subscribe(new Observer<EmailFilterOrderListResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull EmailFilterOrderListResponse response) {
                        filterOrderListResponse.postValue(response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                        if (e instanceof HttpException) {
                            Response<?> errorResponse = ((HttpException) e).response();
                            if (errorResponse != null && errorResponse.errorBody() != null) {
                                try {
                                    String errorBody = errorResponse.errorBody().string();
                                    HttpErrorResponse httpErrorResponse = GENERAL_GSON
                                            .fromJson(errorBody, HttpErrorResponse.class);
                                    filterOrderListErrorResponse.postValue(httpErrorResponse.getError().getError());
                                } catch (IOException | JsonSyntaxException ex) {
                                    Timber.e(ex, "Can't parse filters order error");
                                }
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
