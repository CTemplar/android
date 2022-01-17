package mobileapp.ctemplar.com.ctemplarapp.repository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.AddFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.EditFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterOrderListRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.EmailFilterOrderListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ManageFoldersRepository {
    private RestService service;

    private static final ManageFoldersRepository instance = new ManageFoldersRepository();

    public static ManageFoldersRepository getInstance() {
        return instance;
    }

    public ManageFoldersRepository() {
        CTemplarApp.getRestClientLiveData().observeForever(instance -> service = instance.getRestService());
    }

    public Observable<FoldersResponse> getFoldersList(int limit, int offset) {
        return service.getFolders(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> getUnreadFoldersList() {
        return service.getUnreadFolders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    public Observable<FoldersResult> editFolder(long id, EditFolderRequest request) {
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
}
