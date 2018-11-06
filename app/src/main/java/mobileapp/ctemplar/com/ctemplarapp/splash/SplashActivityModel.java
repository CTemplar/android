package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.arch.lifecycle.ViewModel;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;

public class SplashActivityModel extends ViewModel {

    UserRepository userRepository;

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public String getToken() {
        return userRepository.getUserToken();
    }
}
