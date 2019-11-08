package mobileapp.ctemplar.com.ctemplarapp.folders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.ManageFoldersRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import retrofit2.Response;
import timber.log.Timber;

public class ManageFoldersViewModel extends ViewModel {

    private ManageFoldersRepository manageFoldersRepository;
    private UserRepository userRepository;
    private MutableLiveData<ResponseStatus> responseStatus = new MutableLiveData<>();
    private MutableLiveData<ResponseStatus> deletingStatus = new MutableLiveData<>();
    private MutableLiveData<FoldersResponse> foldersResponse = new MutableLiveData<>();
    private MutableLiveData<MyselfResponse> myselfResponse = new MutableLiveData<>();

    public MutableLiveData<FoldersResponse> getFoldersResponse() {
        return foldersResponse;
    }

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
