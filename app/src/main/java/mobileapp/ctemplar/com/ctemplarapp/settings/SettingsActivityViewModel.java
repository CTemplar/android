package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.arch.lifecycle.ViewModel;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;

public class SettingsActivityViewModel extends ViewModel {
    private UserRepository userRepository;

    public SettingsActivityViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    void changeRecoveryEmail(String recoveryEmail) {

    }
}
