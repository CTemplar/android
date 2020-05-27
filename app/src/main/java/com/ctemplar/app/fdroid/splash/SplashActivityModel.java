package com.ctemplar.app.fdroid.splash;

import androidx.lifecycle.ViewModel;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.repository.UserRepository;

public class SplashActivityModel extends ViewModel {

    private UserRepository userRepository;

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public String getToken() {
        return userRepository.getUserToken();
    }
}
