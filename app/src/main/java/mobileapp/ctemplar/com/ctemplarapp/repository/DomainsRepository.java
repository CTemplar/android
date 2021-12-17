package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.RestService;
import mobileapp.ctemplar.com.ctemplarapp.net.response.domains.CustomDomainsResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainsDTO;

@Singleton
public class DomainsRepository {
    private static DomainsRepository instance = new DomainsRepository();
    private RestService service;

    public static DomainsRepository getInstance() {
        if (instance == null) {
            instance = new DomainsRepository();
        }
        return instance;
    }

    public DomainsRepository() {
        CTemplarApp.getRestClientLiveData().observeForever(instance
                -> service = instance.getRestService());
    }

    public Single<CustomDomainsDTO> getCustomDomains() {
        return Single.create(emitter -> service.getCustomDomains()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomDomainsResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainsResponse response) {
                        emitter.onSuccess(CustomDomainsDTO.get(response));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        emitter.onError(e);
                    }
                }));
    }
}
