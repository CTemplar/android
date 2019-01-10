package mobileapp.ctemplar.com.ctemplarapp.repository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;

public class ManageFoldersRepository {

    RestService service;

    private static ManageFoldersRepository instance = new ManageFoldersRepository();

    public static ManageFoldersRepository getInstance() {
        return instance;
    }

    public ManageFoldersRepository() {
        service = CTemplarApp.getRestClient().getRestService();
    }

    public Observable<FoldersResponse> getFoldersList(int limit, int offset) {
        return service.getFolders(limit, offset)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
