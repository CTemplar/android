package mobileapp.ctemplar.com.ctemplarapp.settings.domains;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.DomainsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainsDTO;
import timber.log.Timber;

public class DomainsViewModel extends ViewModel {
    private final DomainsRepository domainsRepository;

    private final MutableLiveData<DTOResource<CustomDomainsDTO>> customDomains = new MutableLiveData<>();
    private final MutableLiveData<DTOResource<CustomDomainDTO>> customDomain = new MutableLiveData<>();

    public DomainsViewModel() {
        domainsRepository = CTemplarApp.getDomainsRepository();
    }

    public MutableLiveData<DTOResource<CustomDomainsDTO>> getCustomDomains() {
        return customDomains;
    }

    public MutableLiveData<DTOResource<CustomDomainDTO>> getCustomDomain() {
        return customDomain;
    }

    public void customDomainsRequest() {
        domainsRepository.getCustomDomains()
                .subscribe(new SingleObserver<CustomDomainsDTO>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainsDTO dto) {
                        customDomains.postValue(DTOResource.success(dto));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        customDomains.postValue(DTOResource.error(e));
                        Timber.e(e);
                    }
                });
    }

    public void customDomainRequest(int id) {
        domainsRepository.getCustomDomain(id)
                .subscribe(new SingleObserver<CustomDomainDTO>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainDTO dto) {
                        customDomain.postValue(DTOResource.success(dto));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        customDomain.postValue(DTOResource.error(e));
                        Timber.e(e);
                    }
                });
    }

    public MutableLiveData<DTOResource<CustomDomainDTO>> createCustomDomain(String domain) {
        MutableLiveData<DTOResource<CustomDomainDTO>> result = new MutableLiveData<>();
        domainsRepository.createCustomDomain(domain)
                .subscribe(new SingleObserver<CustomDomainDTO>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainDTO domainDTO) {
                        result.postValue(DTOResource.success(domainDTO));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.postValue(DTOResource.error(e));
                        Timber.e(e);
                    }
                });
        return result;
    }

    public MutableLiveData<DTOResource<CustomDomainDTO>> updateCustomDomain(int id, boolean catchAll, String catchAllEmail) {
        MutableLiveData<DTOResource<CustomDomainDTO>> result = new MutableLiveData<>();
        domainsRepository.updateCustomDomain(id, catchAll, catchAllEmail)
                .subscribe(new SingleObserver<CustomDomainDTO>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainDTO domainDTO) {
                        result.postValue(DTOResource.success(domainDTO));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.postValue(DTOResource.error(e));
                        Timber.e(e);
                    }
                });
        return result;
    }

    public MutableLiveData<DTOResource<Boolean>> deleteCustomDomain(int id) {
        MutableLiveData<DTOResource<Boolean>> result = new MutableLiveData<>();
        domainsRepository.deleteCustomDomain(id)
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Boolean aBoolean) {
                        result.postValue(DTOResource.success(aBoolean));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.postValue(DTOResource.error(e));
                        Timber.e(e);
                    }
                });
        return result;
    }
}
