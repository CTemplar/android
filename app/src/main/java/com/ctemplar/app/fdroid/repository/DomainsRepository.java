package com.ctemplar.app.fdroid.repository;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.RestService;
import com.ctemplar.app.fdroid.net.request.domains.CreateDomainRequest;
import com.ctemplar.app.fdroid.net.request.domains.UpdateDomainRequest;
import com.ctemplar.app.fdroid.net.response.domains.CustomDomainResponse;
import com.ctemplar.app.fdroid.net.response.domains.CustomDomainsResponse;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainDTO;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainsDTO;
import retrofit2.Response;

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

    public Single<CustomDomainDTO> verifyCustomDomain(int id) {
        return Single.create(emitter -> service.verifyCustomDomain(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomDomainResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainResponse response) {
                        emitter.onSuccess(CustomDomainDTO.get(response));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        emitter.onError(e);
                    }
                }));
    }

    public Single<CustomDomainDTO> getCustomDomain(int id) {
        return Single.create(emitter -> service.getCustomDomain(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomDomainResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainResponse response) {
                        emitter.onSuccess(CustomDomainDTO.get(response));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        emitter.onError(e);
                    }
                }));
    }

    public Single<CustomDomainDTO> createCustomDomain(String domain) {
        return Single.create(emitter -> service.createCustomDomain(new CreateDomainRequest(domain))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomDomainResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainResponse response) {
                        emitter.onSuccess(CustomDomainDTO.get(response));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        emitter.onError(e);
                    }
                }));
    }

    public Single<CustomDomainDTO> updateCustomDomain(int id, UpdateDomainRequest request) {
        return Single.create(emitter -> service.updateCustomDomain(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CustomDomainResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainResponse response) {
                        emitter.onSuccess(CustomDomainDTO.get(response));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        emitter.onError(e);
                    }
                }));
    }

    public Single<Boolean> deleteCustomDomain(int id) {
        return Single.create(emitter -> service.deleteCustomDomain(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<Void>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Response<Void> voidResponse) {
                        emitter.onSuccess(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        emitter.onError(e);
                    }
                }));
    }
}
