package mobileapp.ctemplar.com.ctemplarapp.settings.domains;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.DomainsRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainsDTO;
import timber.log.Timber;

public class DomainsViewModel extends ViewModel {
    private final DomainsRepository domainsRepository;

    private final MutableLiveData<CustomDomainsDTO> customDomains = new MutableLiveData<>();

    public DomainsViewModel() {
        domainsRepository = CTemplarApp.getDomainsRepository();
    }

    public MutableLiveData<CustomDomainsDTO> getCustomDomains() {
        return customDomains;
    }

    public void customDomainsRequest() {
        domainsRepository.getCustomDomains()
                .subscribe(new SingleObserver<CustomDomainsDTO>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomDomainsDTO dto) {
                        customDomains.postValue(dto);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}
