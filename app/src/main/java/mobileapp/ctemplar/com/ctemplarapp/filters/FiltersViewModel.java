package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FiltersResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import timber.log.Timber;

public class FiltersViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<FiltersResponse> filtersResponse = new MutableLiveData<>();

    public MutableLiveData<FiltersResponse> getFiltersResponse() {
        return filtersResponse;
    }

    public FiltersViewModel() {
        userRepository = CTemplarApp.getUserRepository();
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
}
