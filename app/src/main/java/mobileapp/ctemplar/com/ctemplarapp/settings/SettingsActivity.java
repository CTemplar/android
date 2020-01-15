package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.SwitchPreference;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.appcompat.widget.Toolbar;
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
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.wbl.WhiteBlackListActivity;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity {

    public static final String USER_IS_PRIME = "user_is_prime";
    public static final String SETTING_ID = "setting_id";

    private static SettingsActivityViewModel settingsModel;
    private static UserRepository userRepository = CTemplarApp.getUserRepository();
    private static UserStore userStore = CTemplarApp.getUserStore();

    private static PreferenceScreen recoveryEmailPreferenceScreen;
    private static Preference storageLimitPreference;
    private static SharedPreferences sharedPreferences;

    private static boolean isPrimeUser;
    private static long defaultMailboxId = -1;
    private static long settingId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        settingsModel = ViewModelProviders.of(this).get(SettingsActivityViewModel.class);
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

    private static long getSettingId() {
        return sharedPreferences.getLong(SETTING_ID, -1);
    }

    private static void setSettingId(long id) {
        sharedPreferences.edit().putLong(SETTING_ID, id).apply();
    }

    public static class SettingsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            setPreferencesFromResource(R.xml.settings_activity, s);

            storageLimitPreference = findPreference(getString(R.string.local_storage_limit));
            recoveryEmailPreferenceScreen = findPreference(getString(R.string.recovery_email_holder));
            String recoveryEmail = sharedPreferences.getString(getString(R.string.recovery_email), null);
            if (recoveryEmail != null && !recoveryEmail.isEmpty()) {
                recoveryEmailPreferenceScreen.setSummary(recoveryEmail);
            }

            Preference passwordKey = findPreference(getString(R.string.password_key));
            passwordKey.setOnPreferenceClickListener(preference -> {
                Intent changePassword = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(changePassword);
                return false;
            });

            Preference keys = findPreference(getString(R.string.setting_keys));
            keys.setOnPreferenceClickListener(preference -> {
                Intent keysIntent = new Intent(getActivity(), KeysActivity.class);
                startActivity(keysIntent);
                return false;
            });

            Preference filters = findPreference(getString(R.string.filters));
            filters.setOnPreferenceClickListener(preference -> {
                Intent filtersIntent = new Intent(getActivity(), FiltersActivity.class);
                startActivity(filtersIntent);
                return false;
            });

            Preference whiteBlackList = findPreference(getString(R.string.white_black_list));
            whiteBlackList.setOnPreferenceClickListener(preference -> {
                Intent whiteBlackList1 = new Intent(getActivity(), WhiteBlackListActivity.class);
                startActivity(whiteBlackList1);
                return true;
            });

            Preference manageFolders = findPreference(getString(R.string.manage_folders));
            manageFolders.setOnPreferenceClickListener(preference -> {
                Intent manageFolders1 = new Intent(getActivity(), ManageFoldersActivity.class);
                startActivity(manageFolders1);
                return true;
            });

            Preference mailboxes = findPreference(getString(R.string.email_addresses));
            mailboxes.setOnPreferenceClickListener(preference -> {
                Intent mailboxesIntent = new Intent(getActivity(), MailboxesActivity.class);
                mailboxesIntent.putExtra(USER_IS_PRIME, isPrimeUser);
                startActivity(mailboxesIntent);
                return true;
            });
        }
    }

    public static class NotificationsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.notifications_settings, rootKey);

            SwitchPreference switchPreferenceNotificationsEnabled = findPreference(getString(R.string.push_notifications_enabled));
            switchPreferenceNotificationsEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabled = (boolean) newValue;
                userStore.setNotificationsEnabled(isEnabled);
                return true;
            });

        }
    }

    public static class RecoveryEmailFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.recovery_email_settings, rootKey);

            EditTextPreference preferenceRecoveryEmail = findPreference(getString(R.string.recovery_email));
            String recoveryEmail = sharedPreferences.getString(getString(R.string.recovery_email), null);
            if (recoveryEmail != null && !recoveryEmail.isEmpty()) {
                preferenceRecoveryEmail.setTitle(recoveryEmail);
            }

            CheckBoxPreference checkBoxRecoveryEmailEnabled = findPreference(getString(R.string.recovery_email_enabled));
            checkBoxRecoveryEmailEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                Boolean value = (Boolean) newValue;
                if (!value) {
                    preferenceRecoveryEmail.setTitle(getString(R.string.settings_type_recovery_email));
                    recoveryEmailPreferenceScreen.setSummary("");
                    settingsModel.updateRecoveryEmail(settingId, "");
                }
                Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                return true;
            });

            preferenceRecoveryEmail.setOnPreferenceChangeListener((preference, newValue) -> {
                String value = (String) newValue;
                if (EditTextUtils.isEmailValid(value) || value.isEmpty()) {
                    if (value.isEmpty()) {
                        preferenceRecoveryEmail.setTitle(getString(R.string.settings_type_recovery_email));
                        checkBoxRecoveryEmailEnabled.setChecked(false);
                    } else {
                        preferenceRecoveryEmail.setTitle((String) newValue);
                    }
                    recoveryEmailPreferenceScreen.setSummary((String) newValue);
                    settingsModel.updateRecoveryEmail(settingId, value);
                    Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_email_not_valid), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

    public static class SavingContactsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.saving_contacts_settings, rootKey);

            SwitchPreference autoSaveContactsPreference = findPreference(getString(R.string.auto_save_contacts_enabled));
            autoSaveContactsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabled = (boolean) newValue;
                settingsModel.updateAutoSaveEnabled(settingId, isEnabled);
                return true;
            });
        }
    }

    public static class EncryptionFragment extends BasePreferenceFragment {

        private SwitchPreference contactsEncryptionSwitchPreference;

        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.encryption_settings, rootKey);
            SwitchPreference subjectEncryptionSwitchPreference = findPreference(getString(R.string.subject_encryption_enabled));
            subjectEncryptionSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                boolean value = (boolean) newValue;
                settingsModel.updateSubjectEncryption(settingId, value);
                return true;
            });
            subjectEncryptionSwitchPreference.setEnabled(isPrimeUser);

            SwitchPreference attachmentsEncryptionSwitchPreference = findPreference(getString(R.string.attachments_encryption_enabled));
            attachmentsEncryptionSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                boolean value = (boolean) newValue;
                userStore.setAttachmentsEncryptionEnabled(value);
                settingsModel.updateAttachmentsEncryption(settingId, value);
                return true;
            });

            contactsEncryptionSwitchPreference = findPreference(getString(R.string.contacts_encryption_enabled));
            contactsEncryptionSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean value = (boolean) newValue;
                if (value) {
                    userStore.setContactsEncryptionEnabled(true);
                    settingsModel.updateContactsEncryption(settingId, true);
                    Toast.makeText(getActivity(), getString(R.string.toast_contacts_encrypted), Toast.LENGTH_SHORT).show();
                } else {
                    disableContactsEncryption();
                    return false;
                }
                return true;
            });
        }

        private void disableContactsEncryption() {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getString(R.string.txt_contact_decryption));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            final int[] listOffset = {0};
            settingsModel.getDecryptionStatus().observe(this, responseStatus -> {
                if (responseStatus == ResponseStatus.RESPONSE_NEXT) {
                    listOffset[0] += 20;
                    settingsModel.decryptContacts(listOffset[0]);
                } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                    userStore.setContactsEncryptionEnabled(false);
                    settingsModel.updateContactsEncryption(settingId, false);
                    contactsEncryptionSwitchPreference.setChecked(false);
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.toast_contacts_decrypted), Toast.LENGTH_SHORT).show();
                } else if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.toast_decryption_error), Toast.LENGTH_SHORT).show();
                }
            });

            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getString(R.string.settings_disable_contacts_encryption))
                    .setMessage(getActivity().getString(R.string.settings_disable_contacts_encryption_note))
                    .setPositiveButton(getActivity().getString(R.string.btn_confirm), (dialog, which) -> {
                        progressDialog.show();
                        settingsModel.decryptContacts(listOffset[0]);
                    }
                    )
                    .setNeutralButton(getActivity().getString(R.string.btn_cancel), null)
                    .show();
        }
    }

    public static class SignatureFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.signature_settings, rootKey);

            final EditTextPreference preferenceSignature = findPreference(getString(R.string.signature));
            final String signatureText = preferenceSignature.getText();
            if (signatureText != null && !signatureText.isEmpty()) {
                preferenceSignature.setTitle(signatureText);
            }

            preferenceSignature.setOnPreferenceChangeListener((preference, newValue) -> {
                String newSignatureText = newValue.toString();
                if (!newSignatureText.isEmpty()) {
                    preferenceSignature.setTitle(newSignatureText);
                    settingsModel.updateSignature(settingId, defaultMailboxId, newSignatureText);
                } else {
                    preferenceSignature.setTitle(getString(R.string.txt_type_signature));
                }
                return true;
            });

            CheckBoxPreference checkBoxSignatureEnabled = findPreference(getString(R.string.signature_enabled));
            checkBoxSignatureEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean signatureEnabled = (boolean) newValue;
                if (signatureEnabled) {
                    Toast.makeText(getActivity(), getString(R.string.txt_signature_enabled), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit()
                            .putBoolean(getString(R.string.mobile_signature_enabled), false)
                            .apply();
                }
                return true;
            });
        }
    }

    public static class MobileSignatureFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.mobile_signature_settings, rootKey);

            final EditTextPreference preferenceMobileSignature = findPreference(getString(R.string.mobile_signature));
            if (preferenceMobileSignature.getText() != null && !preferenceMobileSignature.getText().isEmpty()) {
                preferenceMobileSignature.setTitle(preferenceMobileSignature.getText());
            }

            preferenceMobileSignature.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!newValue.toString().isEmpty()) {
                    preferenceMobileSignature.setTitle((String) newValue);
                } else {
                    preferenceMobileSignature.setTitle(getResources().getString(R.string.txt_type_mobile_signature));
                }
                return true;
            });

            CheckBoxPreference checkBoxMobileSignatureEnabled = findPreference(getString(R.string.mobile_signature_enabled));
            checkBoxMobileSignatureEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean mobileSignatureEnabled = (boolean) newValue;
                if (mobileSignatureEnabled) {
                    Toast.makeText(getActivity(), getString(R.string.txt_mobile_signature_enabled), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit()
                            .putBoolean(getString(R.string.signature_enabled), false)
                            .apply();
                }
                return true;
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
                        if (myselfResponse != null && myselfResponse.result != null) {
                            MyselfResult myselfResult = myselfResponse.result[0];
                            saveData(myselfResult);
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

    private void saveData(MyselfResult myselfResult) {
        SettingsEntity settingsEntity = myselfResult.settings;
        MailboxesResult mailboxesResult = myselfResult.mailboxes[0];
        settingId = settingsEntity.getId();
        defaultMailboxId = mailboxesResult.getId();
        isPrimeUser = myselfResult.isPrime();
        setSettingId(settingId);

        String usedStorage = AppUtils.usedStorage(settingsEntity.getUsedStorage());
        String allocatedStorage = AppUtils.usedStorage(settingsEntity.getAllocatedStorage());

        String recoveryEmail = settingsEntity.getRecoveryEmail();
        boolean isNotificationsEnabled = userStore.getNotificationsEnabled();

        storageLimitPreference.setSummary(getString(
                R.string.storage_limit_info,
                usedStorage,
                allocatedStorage
        ));
        recoveryEmailPreferenceScreen.setSummary(settingsEntity.getRecoveryEmail());
        sharedPreferences.edit()
                .putString(getString(R.string.recovery_email), recoveryEmail)
                .putString(getString(R.string.signature), mailboxesResult.getSignature())
                .putBoolean(getString(R.string.recovery_email_enabled), !recoveryEmail.isEmpty())
                .putBoolean(getString(R.string.auto_save_contacts_enabled), settingsEntity.isSaveContacts())
                .putBoolean(getString(R.string.subject_encryption_enabled), settingsEntity.isSubjectEncrypted())
                .putBoolean(getString(R.string.attachments_encryption_enabled), settingsEntity.isAttachmentsEncrypted())
                .putBoolean(getString(R.string.contacts_encryption_enabled), settingsEntity.isContactsEncrypted())
                .putBoolean(getString(R.string.push_notifications_enabled), isNotificationsEnabled)
                .apply();
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
                        //setOnClickPreference(inner);
                    }
                }
            }
        }

        private void setOnClickPreference(Preference preference) {
            final String fragmentName = preference.getFragment();
            if (fragmentName != null && !fragmentName.isEmpty()) {
                preference.setOnPreferenceClickListener(preference1 -> {
                    Fragment fragment = Fragment.instantiate(getActivity(), fragmentName);
                    SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                    if (settingsActivity != null) {
                        settingsActivity.showFragment(fragment);
                    }
                    return false;
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
