package com.ctemplar.app.fdroid.splash;

import androidx.lifecycle.ViewModel;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.repository.UserRepository;

public class SplashActivityModel extends ViewModel {
    private final UserRepository userRepository;

    public SplashActivityModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public boolean isAuthorized() {
        return userRepository.isAuthorized();
    }

    public String getUserToken() {
        return userRepository.getUserToken();
    }
}
