package mobileapp.ctemplar.com.ctemplarapp.folders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.EditFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.HttpErrorResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

public class EditFolderViewModel extends ViewModel {
    private final ManageFoldersRepository manageFoldersRepository;
    private final MutableLiveData<ResponseStatus> deletingStatus = new MutableLiveData<>();
    private final MutableLiveData<FoldersResult> editResponse = new MutableLiveData<>();
    private final MutableLiveData<String> editErrorResponse = new MutableLiveData<>();

    public EditFolderViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public MutableLiveData<ResponseStatus> getDeletingStatus() {
        return deletingStatus;
    }

    public MutableLiveData<FoldersResult> getEditResponse() {
        return editResponse;
    }

    public MutableLiveData<String> getEditErrorResponse() {
        return editErrorResponse;
    }

    public void deleteFolderById(long folderId) {
        manageFoldersRepository.deleteFolder(folderId)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Response<Void> responseVoid) {
                        deletingStatus.postValue(ResponseStatus.RESPONSE_COMPLETE);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FoldersResult foldersResult) {
                        editResponse.postValue(foldersResult);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e instanceof HttpException) {
                            Response<?> errorResponse = ((HttpException) e).response();
                            if (errorResponse != null && errorResponse.errorBody() != null) {
                                try {
                                    HttpErrorResponse httpErrorResponse = GENERAL_GSON.fromJson(
                                            errorResponse.errorBody().string(),
                                            HttpErrorResponse.class
                                    );
                                    editErrorResponse.postValue(httpErrorResponse.getError().getError());
                                    return;
                                } catch (IOException | JsonSyntaxException ex) {
                                    Timber.e(ex, "Can't parse edit folder error");
                                }
                            }
                        }
                        editErrorResponse.postValue("Edit folder error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
