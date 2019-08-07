package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.filters.FiltersActivity;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.mailboxes.MailboxesActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoSaveContactEnabledRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoveryEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.wbl.WhiteBlackListActivity;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity {
    public static final String USER_IS_PRIME = "user_is_prime";
    private static SharedPreferences sharedPreferences;
    private static UserRepository userRepository = CTemplarApp.getUserRepository();
    private static PreferenceScreen recoveryEmailPreferenceScreen;
    private static Preference storageLimitPreference;
    private static boolean userIsPrime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        requestMySelfData();

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new SettingsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, preferenceFragment)
                    .commit();
        }
    }

    protected void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static class SettingsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            setPreferencesFromResource(R.xml.settings_activity, s);

            storageLimitPreference = findPreference(getString(R.string.local_storage_limit));
            recoveryEmailPreferenceScreen = (PreferenceScreen) findPreference(getString(R.string.recovery_email_holder));
            String recoveryEmail = sharedPreferences.getString(getString(R.string.recovery_email), null);
            if (recoveryEmail != null && !recoveryEmail.isEmpty()) {
                recoveryEmailPreferenceScreen.setSummary(recoveryEmail);
            }

            Preference passwordKey = findPreference(getString(R.string.password_key));
            passwordKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent changePassword = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(changePassword);
                    return false;
                }
            });

            Preference filters = findPreference(getString(R.string.filters));
            filters.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent filtersIntent = new Intent(getActivity(), FiltersActivity.class);
                    startActivity(filtersIntent);
                    return false;
                }
            });

            Preference whiteBlackList = findPreference(getString(R.string.white_black_list));
            whiteBlackList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent whiteBlackList = new Intent(getActivity(), WhiteBlackListActivity.class);
                    startActivity(whiteBlackList);
                    return true;
                }
            });

            Preference manageFolders = findPreference(getString(R.string.manage_folders));
            manageFolders.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent manageFolders = new Intent(getActivity(), ManageFoldersActivity.class);
                    startActivity(manageFolders);
                    return true;
                }
            });

            final Preference mailboxes = findPreference(getString(R.string.email_addresses));
            mailboxes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent mailboxesIntent = new Intent(getActivity(), MailboxesActivity.class);
                    mailboxesIntent.putExtra(USER_IS_PRIME, userIsPrime);
                    startActivity(mailboxesIntent);
                    return true;
                }
            });
        }
    }

    public static class NotificationsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.notifications_settings, rootKey);
        }
    }

    public static class RecoveryEmailFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.recovery_email_settings, rootKey);

            CheckBoxPreference checkBoxRecoveryEmailEnabled = (CheckBoxPreference) findPreference(getString(R.string.recovery_email_enabled));
            checkBoxRecoveryEmailEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), getString(R.string.toast_recovery_email_changed), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            final EditTextPreference preferenceRecoveryEmail = (EditTextPreference) findPreference(getString(R.string.recovery_email));
            String recoveryEmail = sharedPreferences.getString(getString(R.string.recovery_email), null);
            if (recoveryEmail != null && !recoveryEmail.isEmpty()) {
                preferenceRecoveryEmail.setTitle(recoveryEmail);
            }

            preferenceRecoveryEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = (String) newValue;
                    if (EditTextUtils.isEmailValid(value)) {
                        updateRecoveryEmail(value);
                        preferenceRecoveryEmail.setTitle((String) newValue);
                        recoveryEmailPreferenceScreen.setSummary((String) newValue);
                        Toast.makeText(getActivity(), getString(R.string.toast_recovery_email_changed), Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_email_not_valid), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            });
        }
    }

    public static class SavingContactsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.saving_contacts_settings, rootKey);

            SwitchPreference autoSaveContactsPreference = (SwitchPreference) findPreference(getString(R.string.auto_save_contacts_enabled));
            autoSaveContactsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isEnabled = (boolean) newValue;
                    updateAutoSaveEnabled(isEnabled);
                    return true;
                }
            });
        }

        private void updateAutoSaveEnabled(boolean isEnabled) {
            final long settingId = getSettingId();
            if (settingId == -1) {
                Timber.e("Setting id is not defined");
                return;
            }

            userRepository.updateAutoSaveEnabled(settingId, new AutoSaveContactEnabledRequest(isEnabled))
                    .subscribe(new Observer<SettingsEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(SettingsEntity settingsEntity) {

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

    public static class SignatureFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.signature_settings, rootKey);

            final EditTextPreference preferenceSignature = (EditTextPreference) findPreference(getString(R.string.signature));
            if (preferenceSignature.getText() != null && !preferenceSignature.getText().isEmpty()) {
                preferenceSignature.setTitle(preferenceSignature.getText());
            }

            preferenceSignature.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!newValue.toString().isEmpty()) {
                        preferenceSignature.setTitle((String) newValue);
                    } else {
                        preferenceSignature.setTitle(getString(R.string.txt_type_signature));
                    }
                    return true;
                }
            });

            CheckBoxPreference checkBoxSignatureEnabled = (CheckBoxPreference) findPreference(getString(R.string.signature_enabled));
            checkBoxSignatureEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //
                    return true;
                }
            });
        }
    }

    public static class MobileSignatureFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.mobile_signature_settings, rootKey);

            final EditTextPreference preferenceMobileSignature = (EditTextPreference) findPreference(getString(R.string.mobile_signature));
            if (preferenceMobileSignature.getText() != null && !preferenceMobileSignature.getText().isEmpty()) {
                preferenceMobileSignature.setTitle(preferenceMobileSignature.getText());
            }

            preferenceMobileSignature.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!newValue.toString().isEmpty()) {
                        preferenceMobileSignature.setTitle((String) newValue);
                    } else {
                        preferenceMobileSignature.setTitle(getResources().getString(R.string.txt_type_mobile_signature));
                    }
                    return true;
                }
            });

            CheckBoxPreference checkBoxMobileSignatureEnabled = (CheckBoxPreference) findPreference(getString(R.string.mobile_signature_enabled));
            checkBoxMobileSignatureEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //
                    return true;
                }
            });
        }
    }

    private void requestMySelfData() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.i("Request myself info");
                    }

                    @Override
                    public void onNext(MyselfResponse myselfResponse) {
                        if (myselfResponse.result != null && myselfResponse.result.length > 0) {
                            SettingsEntity settings = myselfResponse.result[0].settings;
                            userIsPrime = myselfResponse.result[0].isPrime;
                            setSettingId(settings.id);
                            saveData(settings);
                        }
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

    private void saveData(SettingsEntity settingsEntity) {
        String usedStorage = AppUtils.usedStorage(settingsEntity.getUsedStorage());
        String allocatedStorage = AppUtils.usedStorage(settingsEntity.getAllocatedStorage());

        storageLimitPreference.setSummary(getString(
                R.string.storage_limit_info,
                usedStorage,
                allocatedStorage
        ));

        recoveryEmailPreferenceScreen.setSummary(settingsEntity.recoveryEmail);
        sharedPreferences.edit()
                .putString(getString(R.string.recovery_email), settingsEntity.recoveryEmail)
                .putBoolean(getString(R.string.auto_save_contacts_enabled), settingsEntity.saveContacts)
                .apply();
    }

    private static long getSettingId() {
        return sharedPreferences.getLong("setting_id", -1);
    }

    private static void setSettingId(long id) {
        sharedPreferences.edit().putLong("setting_id", id).apply();
    }

    private static void updateRecoveryEmail(String newRecoveryEmail) {
        long settingId = getSettingId();
        if (settingId == -1) {
            Timber.e("Setting id is not defined");
            return;
        }

        RecoveryEmailRequest request = new RecoveryEmailRequest(newRecoveryEmail);
        userRepository.updateRecoveryEmail(settingId, request)
                .subscribe(new Observer<SettingsEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.i("Updating recovery email");
                    }

                    @Override
                    public void onNext(SettingsEntity settingsEntity) {
                        Timber.i("Recovery email updated");
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

    public static abstract class BasePreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            int count = preferenceScreen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference preference = preferenceScreen.getPreference(i);
                setOnClickPreference(preference);
                if (preference instanceof PreferenceCategory) {
                    PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.getPreference(i);
                    for (int j = 0; j < preferenceCategory.getPreferenceCount(); j++) {
                        Preference inner = preferenceCategory.getPreference(j);
                        setOnClickPreference(inner);
                    }
                }
            }
        }

        private void setOnClickPreference(Preference preference) {
            final String fragmentName = preference.getFragment();
            if (fragmentName != null && !fragmentName.isEmpty()) {
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Fragment fragment = Fragment.instantiate(getActivity(), fragmentName);
                        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                        if (settingsActivity != null) {
                            settingsActivity.showFragment(fragment);
                        }
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
