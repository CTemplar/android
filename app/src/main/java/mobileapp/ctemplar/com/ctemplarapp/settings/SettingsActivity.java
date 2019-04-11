package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoSaveContactEnabledRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoveryEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.wbl.WhiteBlackListActivity;
import timber.log.Timber;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        private UserRepository userRepository = CTemplarApp.getUserRepository();

        private EditTextPreference preferenceRecoveryEmail;
        private Preference localStorageLimitPreference;
        private PreferenceScreen recoveryEmailHolder;
        private SwitchPreference autoSaveContactsEnabledPreference;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestMySelfData();

            addPreferencesFromResource(R.xml.settings_screen);

            recoveryEmailHolder = (PreferenceScreen) findPreference(getString(R.string.recovery_email_holder));

            Preference passwordKey = findPreference(getString(R.string.password_key));
            passwordKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent changePassword = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(changePassword);
                    return true;
                }
            });

            Preference whiteBlackList = findPreference(getString(R.string.white_black_list));
            whiteBlackList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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

            preferenceRecoveryEmail = (EditTextPreference) findPreference(getString(R.string.recovery_email));
            if (preferenceRecoveryEmail.getText() != null && !preferenceRecoveryEmail.getText().isEmpty()) {
                preferenceRecoveryEmail.setTitle(preferenceRecoveryEmail.getText());
                recoveryEmailHolder.setSummary(preferenceRecoveryEmail.getText());
            }
            preferenceRecoveryEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = (String) newValue;
                    if (EditTextUtils.isEmailValid(value)) {
                        updateRecoveryEmail(value);
                        preferenceRecoveryEmail.setTitle((String) newValue);
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_recovery_email_changed), Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_email_not_valid), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            });

            CheckBoxPreference checkBoxRecoveryEmailEnabled = (CheckBoxPreference) findPreference(getString(R.string.recovery_email_enabled));
            preferenceRecoveryEmail.setEnabled(checkBoxRecoveryEmailEnabled.isChecked());
            checkBoxRecoveryEmailEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    preferenceRecoveryEmail.setEnabled(value);
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_recovery_email_changed), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

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
                        preferenceSignature.setTitle(getResources().getString(R.string.txt_type_signature));
                    }
                    return true;
                }
            });

            CheckBoxPreference checkBoxSignatureEnabled = (CheckBoxPreference) findPreference(getString(R.string.signature_enabled));
            preferenceSignature.setEnabled(checkBoxSignatureEnabled.isChecked());
            checkBoxSignatureEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    preferenceSignature.setEnabled(value);
                    return true;
                }
            });

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
            preferenceMobileSignature.setEnabled(checkBoxMobileSignatureEnabled.isChecked());
            checkBoxMobileSignatureEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    preferenceMobileSignature.setEnabled(value);
                    return true;
                }
            });

            ListPreference listPreferenceAddresses = (ListPreference) findPreference(getString(R.string.email_addresses));

            List<MailboxEntity> mailboxEntities = CTemplarApp.getAppDatabase().mailboxDao().getAll();
            String[] emails = new String[mailboxEntities.size()];
            for (int i = 0; i < mailboxEntities.size(); i++) {
                emails[i] = mailboxEntities.get(i).email;
            }

            if (emails.length != 0) {
                listPreferenceAddresses.setEntries(emails);
                listPreferenceAddresses.setEntryValues(emails);
                int index = Arrays.asList(emails).indexOf(listPreferenceAddresses.getValue());
                if (index == -1) {
                    index = 0;
                }
                listPreferenceAddresses.setValueIndex(index);
            }

            autoSaveContactsEnabledPreference = (SwitchPreference) findPreference(getString(R.string.auto_save_contacts_enabled));
            autoSaveContactsEnabledPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (boolean) newValue;
                    updateAutoSaveEnabled(value);
                    return true;
                }
            });
            localStorageLimitPreference = findPreference(getString(R.string.local_storage_limit));
        }

        private void updateAutoSaveEnabled(boolean isEnabled) {
            long settingId = getSettingId();
            if (settingId == -1) {
                Timber.e("Setting id is not defined");
                return;
            }

            AutoSaveContactEnabledRequest request = new AutoSaveContactEnabledRequest(isEnabled);
            userRepository.updateAutoSaveEnabled(settingId, request)
                    .subscribe(new Observer<SettingsEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Timber.i("Updating auto save contact");
                        }

                        @Override
                        public void onNext(SettingsEntity settingsEntity) {
                            Timber.i("Auto save contact updated");
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

        private long getSettingId() {
            SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            return defaultPreferences.getLong(getString(R.string.setting_id), -1);
        }

        private void setSettingId(long id) {
            SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            defaultPreferences.edit().putLong(getString(R.string.setting_id), id).apply();
        }

        private void updateRecoveryEmail(String newRecoveryEmail) {
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
                                setSettingId(settings.id);
                                updateData(settings);
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

        private void updateData(SettingsEntity settingsEntity) {
            localStorageLimitPreference.setSummary(getString(R.string.storage_limit_info,
                    AppUtils.usedStorage(settingsEntity.getUsedStorage()),
                    AppUtils.usedStorage(settingsEntity.getAllocatedStorage())));

            String recoveryEmail = settingsEntity.recoveryEmail;
            if (!recoveryEmail.isEmpty()) {
                preferenceRecoveryEmail.setText(recoveryEmail);
                preferenceRecoveryEmail.setTitle(recoveryEmail);
                recoveryEmailHolder.setSummary(recoveryEmail);
            }

            autoSaveContactsEnabledPreference.setChecked(settingsEntity.saveContacts);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
