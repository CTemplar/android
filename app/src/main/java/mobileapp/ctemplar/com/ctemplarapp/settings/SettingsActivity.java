package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.CheckBoxPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import org.jetbrains.annotations.NotNull;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.billing.view.SubscriptionActivity;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.ProxyController;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.SettingsResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.settings.domains.DomainsActivity;
import mobileapp.ctemplar.com.ctemplarapp.settings.filters.FiltersActivity;
import mobileapp.ctemplar.com.ctemplarapp.settings.keys.KeysActivity;
import mobileapp.ctemplar.com.ctemplarapp.settings.mailboxes.MailboxesActivity;
import mobileapp.ctemplar.com.ctemplarapp.settings.password.ChangePasswordActivity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.HtmlUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.UnitUtils;
import mobileapp.ctemplar.com.ctemplarapp.wbl.WhiteBlackListActivity;
import timber.log.Timber;

public class SettingsActivity extends BaseActivity {
    public static final String USER_IS_PRIME = "user_is_prime";
    public static final String SETTING_ID = "setting_id";

    private static SettingsViewModel settingsModel;

    private static final UserRepository userRepository = CTemplarApp.getUserRepository();
    private static final UserStore userStore = CTemplarApp.getUserStore();

    private static PreferenceScreen recoveryEmailPreferenceScreen;
    private static Preference storageLimitPreference;
    private static SharedPreferences sharedPreferences;

    private static boolean isPrimeUser;
    private static long settingId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsModel = new ViewModelProvider(this).get(SettingsViewModel.class);
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
                    .addToBackStack(null)
                    .commit();
        }
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

            if (sharedPreferences != null && recoveryEmailPreferenceScreen != null) {
                String recoveryEmail = sharedPreferences.getString(getString(R.string.recovery_email), null);
                if (EditTextUtils.isNotEmpty(recoveryEmail)) {
                    recoveryEmailPreferenceScreen.setSummary(recoveryEmail);
                }
            }
            Preference subscriptionKey = findPreference(getString(R.string.subscription_key));
            if (subscriptionKey != null) {
                subscriptionKey.setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(getActivity(), SubscriptionActivity.class));
                    return false;
                });
            }
            Preference passwordKey = findPreference(getString(R.string.password_key));
            if (passwordKey != null) {
                passwordKey.setOnPreferenceClickListener(preference -> {
                    Intent changePassword = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(changePassword);
                    return false;
                });
            }
            Preference pinLockKey = findPreference(getString(R.string.pin_lock_key));
            if (pinLockKey != null) {
                pinLockKey.setOnPreferenceClickListener(preference -> {
                    Intent pinLockSettingsIntent = new Intent(getActivity(), PINLockSettingsActivity.class);
                    startActivity(pinLockSettingsIntent);
                    return false;
                });
            }
            Preference signatureKey = findPreference(getString(R.string.signature_key));
            if (signatureKey != null) {
                signatureKey.setOnPreferenceClickListener(preference -> {
                    Intent signatureActivity = new Intent(getActivity(), SignatureActivity.class);
                    startActivity(signatureActivity);
                    return false;
                });
            }
            Preference mailboxKeys = findPreference(getString(R.string.setting_keys));
            if (mailboxKeys != null) {
                mailboxKeys.setOnPreferenceClickListener(preference -> {
                    Intent keysIntent = new Intent(getActivity(), KeysActivity.class);
                    startActivity(keysIntent);
                    return false;
                });
            }
            Preference filters = findPreference(getString(R.string.filters_key));
            if (filters != null) {
                filters.setOnPreferenceClickListener(preference -> {
                    Intent filtersIntent = new Intent(getActivity(), FiltersActivity.class);
                    startActivity(filtersIntent);
                    return false;
                });
            }
            Preference whiteBlackList = findPreference(getString(R.string.white_black_list));
            if (whiteBlackList != null) {
                whiteBlackList.setOnPreferenceClickListener(preference -> {
                    Intent whiteBlackList1 = new Intent(getActivity(), WhiteBlackListActivity.class);
                    startActivity(whiteBlackList1);
                    return true;
                });
            }
            Preference manageFolders = findPreference(getString(R.string.manage_folders));
            if (manageFolders != null) {
                manageFolders.setOnPreferenceClickListener(preference -> {
                    Intent manageFolders1 = new Intent(getActivity(), ManageFoldersActivity.class);
                    startActivity(manageFolders1);
                    return true;
                });
            }
            Preference domains = findPreference(getString(R.string.domains_key));
            if (domains != null) {
                domains.setOnPreferenceClickListener(preference -> {
                    Intent domainsIntent = new Intent(getActivity(), DomainsActivity.class);
                    startActivity(domainsIntent);
                    return true;
                });
            }
            Preference mailboxes = findPreference(getString(R.string.email_addresses));
            if (mailboxes != null) {
                mailboxes.setOnPreferenceClickListener(preference -> {
                    Intent mailboxesIntent = new Intent(getActivity(), MailboxesActivity.class);
                    mailboxesIntent.putExtra(USER_IS_PRIME, isPrimeUser);
                    startActivity(mailboxesIntent);
                    return true;
                });
            }
            Preference helpdeskPreference = findPreference(getString(R.string.helpdesk_key));
            if (helpdeskPreference != null) {
                helpdeskPreference.setOnPreferenceClickListener(preference -> {
                    Intent helpdeskIntent = new Intent(getActivity(), HelpdeskActivity.class);
                    startActivity(helpdeskIntent);
                    return true;
                });
            }
        }
    }

    public static class NotificationsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.notifications_settings, rootKey);

            SwitchPreference switchPreferenceNotificationsEnabled = findPreference(getString(R.string.push_notifications_enabled));
            if (switchPreferenceNotificationsEnabled != null) {
                switchPreferenceNotificationsEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                    userStore.setNotificationsEnabled((boolean) newValue);
                    return true;
                });
                boolean isNotificationsEnabled = userStore.isNotificationsEnabled();
                switchPreferenceNotificationsEnabled.setChecked(isNotificationsEnabled);
            }

            EditTextPreference preferenceNotificationEmail = findPreference(getString(R.string.notification_email_key));
            if (preferenceNotificationEmail != null) {
                preferenceNotificationEmail.setOnPreferenceChangeListener((preference, newValue) -> {
                    String value = (String) newValue;
                    if (EditTextUtils.isEmailListValid(value) || TextUtils.isEmpty(value)) {
                        if (TextUtils.isEmpty(value)) {
                            preferenceNotificationEmail.setTitle(R.string.settings_type_notification_email);
                        } else {
                            preferenceNotificationEmail.setTitle(value);
                        }
                        settingsModel.updateNotificationEmail(settingId, value);
                        Toast.makeText(getActivity(), R.string.toast_saved, Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_email_not_valid, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                if (EditTextUtils.isNotEmpty(preferenceNotificationEmail.getText())) {
                    preferenceNotificationEmail.setTitle(preferenceNotificationEmail.getText());
                }
            }
        }
    }

    public static class AutoSaveFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.automation_settings, rootKey);

            SwitchPreference autoSaveContactsPreference = findPreference(getString(R.string.auto_save_contacts_enabled));
            if (autoSaveContactsPreference != null) {
                autoSaveContactsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    settingsModel.updateAutoSaveContactsEnabled(settingId, (boolean) newValue);
                    return true;
                });
            }

            SwitchPreference autoSaveDraftsPreference = findPreference(getString(R.string.auto_save_drafts_enabled));
            if (autoSaveDraftsPreference != null) {
                autoSaveDraftsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    userStore.setDraftsAutoSaveEnabled((boolean) newValue);
                    return true;
                });
                boolean isDraftsAutoSaveEnabled = userStore.isDraftsAutoSaveEnabled();
                autoSaveDraftsPreference.setChecked(isDraftsAutoSaveEnabled);
            }

            SwitchPreference autoReadEmailPreference = findPreference(getString(R.string.key_auto_read_email_enabled));
            if (autoReadEmailPreference != null) {
                autoReadEmailPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    settingsModel.updateAutoReadEmail(settingId, (boolean) newValue);
                    return true;
                });
            }
        }
    }

    public static class DarkModeFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.dark_mode_settings, rootKey);

            ListPreference darkModeListPreference = findPreference(getString(R.string.dark_mode_key));
            if (darkModeListPreference != null) {
                darkModeListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (!(newValue instanceof String)) {
                        return false;
                    }
                    String newValueKey = (String) newValue;
                    settingsModel.updateDarkMode(settingId, ThemeUtils.isModeNight(newValueKey));
                    userStore.setDarkModeKey(newValueKey);
                    return true;
                });
                darkModeListPreference.setValue(userStore.getDarkModeKey());
            }
        }
    }

    public static class LanguagesFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.language_settings, rootKey);

            ListPreference languageListPreference = findPreference(getString(R.string.language_key));
            if (languageListPreference != null) {
                languageListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (!(newValue instanceof String)) {
                        return false;
                    }
                    userStore.setLanguageKey((String) newValue);
                    Toast.makeText(getActivity(), getString(R.string.please_restart_app_to_apply_changes), Toast.LENGTH_SHORT).show();
                    return true;
                });
                languageListPreference.setValue(userStore.getLanguageKey());
            }
        }
    }

    public static class ReportBugsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.report_bugs_settings, rootKey);

            SwitchPreference reportBugsEnabledPreference = findPreference(getString(R.string.report_bugs_enabled));
            if (reportBugsEnabledPreference != null) {
                reportBugsEnabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    userStore.setReportBugsEnabled((boolean) newValue);
                    settingsModel.updateReportBugs(settingId, (boolean) newValue);
                    Toast.makeText(getActivity(), getString(R.string.please_restart_app_to_apply_changes), Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
        }
    }

    public static class ExternalResourcesRestrictionsFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.external_restrictions_settings, rootKey);

            SwitchPreference blockExternalImagesPreference = findPreference(getString(R.string.block_external_images_key));
            SwitchPreference warnExternalLinkPreference = findPreference(getString(R.string.warn_external_link_key));

            if (blockExternalImagesPreference != null) {
                blockExternalImagesPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    userStore.setBlockExternalImagesEnabled((boolean) newValue);
                    settingsModel.updateDisableLoadingImages(settingId, (boolean) newValue);
                    ToastUtils.showToast(getActivity(), R.string.toast_saved);
                    return true;
                });
            }
            if (warnExternalLinkPreference != null) {
                warnExternalLinkPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    userStore.setWarnExternalLinkEnabled((boolean) newValue);
                    settingsModel.updateWarnExternalLink(settingId, (boolean) newValue);
                    ToastUtils.showToast(getActivity(), R.string.toast_saved);
                    return true;
                });
            }
        }
    }

    public static class RecoveryEmailFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.recovery_email_settings, rootKey);

            EditTextPreference preferenceRecoveryEmail = findPreference(getString(R.string.recovery_email));
            CheckBoxPreference checkBoxRecoveryEmailEnabled = findPreference(getString(R.string.recovery_email_enabled));
            String recoveryEmail = sharedPreferences.getString(getString(R.string.recovery_email), null);

            if (preferenceRecoveryEmail == null || checkBoxRecoveryEmailEnabled == null) {
                return;
            }

            if (EditTextUtils.isNotEmpty(recoveryEmail)) {
                preferenceRecoveryEmail.setTitle(recoveryEmail);
            }
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
                if (EditTextUtils.isEmailValid(value) || TextUtils.isEmpty(value)) {
                    if (TextUtils.isEmpty(value)) {
                        preferenceRecoveryEmail.setTitle(getString(R.string.settings_type_recovery_email));
                        checkBoxRecoveryEmailEnabled.setChecked(false);
                    } else {
                        preferenceRecoveryEmail.setTitle(value);
                    }
                    recoveryEmailPreferenceScreen.setSummary(value);
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

    public static class EncryptionFragment extends BasePreferenceFragment {
        private SwitchPreference contactsEncryptionSwitchPreference;

        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.encryption_settings, rootKey);

            contactsEncryptionSwitchPreference = findPreference(getString(R.string.contacts_encryption_enabled));
            if (contactsEncryptionSwitchPreference != null) {
                contactsEncryptionSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean value = (boolean) newValue;
                    if (value) {
                        userStore.setContactsEncryptionEnabled(true);
                        settingsModel.updateContactsEncryption(settingId, true);
                        Toast.makeText(getActivity(), getString(R.string.toast_contacts_encrypted), Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        disableContactsEncryption();
                        return false;
                    }
                });
            }

            SwitchPreference keepDecryptedSubjectsSwitchPreference = findPreference(getString(R.string.keep_decrypted_subjects_enabled));
            if (keepDecryptedSubjectsSwitchPreference != null) {
                keepDecryptedSubjectsSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean value = (boolean) newValue;
                    userStore.setKeepDecryptedSubjectsEnabled(value);
                    if (!value) {
                        settingsModel.clearAllDecryptedSubjects();
                    }
                    Toast.makeText(getActivity(), R.string.toast_saved, Toast.LENGTH_SHORT).show();
                    return true;
                });
                boolean keepDecryptedSubjects = userStore.isKeepDecryptedSubjectsEnabled();
                keepDecryptedSubjectsSwitchPreference.setChecked(keepDecryptedSubjects);
            }
        }

        private void disableContactsEncryption() {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
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

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getString(R.string.settings_disable_contacts_encryption))
                    .setMessage(getActivity().getString(R.string.settings_disable_contacts_encryption_note))
                    .setPositiveButton(getActivity().getString(R.string.title_confirm), (dialog, which) -> {
                                progressDialog.show();
                                settingsModel.decryptContacts(listOffset[0]);
                            }
                    )
                    .setNeutralButton(getActivity().getString(R.string.btn_cancel), null)
                    .show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(true);
        }
    }

    public static class ProxyFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.proxy_settings, rootKey);

            FragmentActivity activity = getActivity();
            ProxyController proxyController = CTemplarApp.getProxyController();

            SwitchPreference useTorSwitchPreference = findPreference(getString(R.string.use_tor_key));
            SwitchPreference useCustomProxySwitchPreference = findPreference(getString(R.string.use_custom_proxy_key));
            DropDownPreference proxyTypeDropDownPreference = findPreference(getString(R.string.proxy_type_key));
            EditTextPreference proxyHostEditTextPreference = findPreference(getString(R.string.proxy_host_key));
            EditTextPreference proxyPortEditTextPreference = findPreference(getString(R.string.proxy_port_key));

            if (useTorSwitchPreference != null && activity != null) {
                useTorSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((boolean) newValue) {
                        proxyController.enableTorProxy();
                        ToastUtils.showToast(getActivity(), R.string.proxy_enabled);
                        if (OrbotHelper.isOrbotInstalled(activity)) {
                            OrbotHelper.get(activity).init();
                        } else {
                            OrbotHelper.get(activity).installOrbot(activity);
                        }
                        if (useCustomProxySwitchPreference != null) {
                            useCustomProxySwitchPreference.setChecked(false);
                        }
                    } else {
                        proxyController.disableTorProxy();
                    }
                    return true;
                });
                useTorSwitchPreference.setChecked(userStore.isProxyTorEnabled());
            }

            if (useCustomProxySwitchPreference != null) {
                useCustomProxySwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((boolean) newValue) {
                        if (ProxyController.isProxyProvided(userStore)) {
                            proxyController.enableCustomProxy();
                            ToastUtils.showToast(getActivity(), R.string.proxy_enabled);
                        } else {
                            ToastUtils.showToast(getActivity(), R.string.fill_proxy_fields);
                        }
                        if (useTorSwitchPreference != null) {
                            useTorSwitchPreference.setChecked(false);
                        }
                    } else {
                        proxyController.disableCustomProxy();
                    }
                    return true;
                });
                useCustomProxySwitchPreference.setChecked(userStore.isProxyCustomEnabled());
            }

            if (proxyTypeDropDownPreference != null) {
                proxyTypeDropDownPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    int value;
                    try {
                        value = Integer.parseInt((String) newValue);
                    } catch (NumberFormatException e) {
                        Timber.e(e);
                        return false;
                    }
                    proxyController.setCustomProxyTypeIndex(value);
                    if (ProxyController.isProxyProvided(userStore)) {
                        proxyController.enableCustomProxy();
                        ToastUtils.showToast(getActivity(), R.string.proxy_enabled);
                    } else {
                        ToastUtils.showToast(getActivity(), R.string.fill_proxy_fields);
                    }
                    return true;
                });
                proxyTypeDropDownPreference.setValueIndex(userStore.getProxyTypeIndex());
            }

            if (proxyHostEditTextPreference != null) {
                proxyHostEditTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String value = (String) newValue;
                    if (EditTextUtils.isIPAddress(value)) {
                        proxyController.setCustomProxyIP(value);
                        if (ProxyController.isProxyProvided(userStore)) {
                            proxyController.enableCustomProxy();
                            ToastUtils.showToast(getActivity(), R.string.proxy_enabled);
                        } else {
                            ToastUtils.showToast(getActivity(), R.string.fill_proxy_fields);
                        }
                    } else {
                        ToastUtils.showToast(getActivity(), R.string.enter_correct_ip);
                        return false;
                    }
                    return true;
                });
                proxyHostEditTextPreference.setText(userStore.getProxyIP());
            }

            if (proxyPortEditTextPreference != null) {
                proxyPortEditTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    int value;
                    try {
                        value = Integer.parseInt((String) newValue);
                    } catch (NumberFormatException e) {
                        Timber.e(e);
                        return false;
                    }
                    if (EditTextUtils.isPort(value)) {
                        proxyController.setCustomProxyPort(value);
                        if (ProxyController.isProxyProvided(userStore)) {
                            proxyController.enableCustomProxy();
                            ToastUtils.showToast(getActivity(), R.string.proxy_enabled);
                        } else {
                            ToastUtils.showToast(getActivity(), R.string.fill_proxy_fields);
                        }
                    } else {
                        ToastUtils.showToast(getActivity(), R.string.enter_correct_port);
                        return false;
                    }
                    return true;
                });
                proxyPortEditTextPreference.setText(String.valueOf(userStore.getProxyPort()));
            }
        }
    }

    public static class PhishingProtectionFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.phishing_protection_settings, rootKey);

            EditTextPreference phishingProtectionEditText = findPreference(getString(R.string.anti_phishing_key));
            CheckBoxPreference phishingProtectionCheckBox = findPreference(getString(R.string.anti_phishing_enabled));
            Preference descriptionPreference = findPreference(getString(R.string.anti_phishing_description_key));
            String phishingProtection = sharedPreferences.getString(getString(R.string.anti_phishing_key), null);

            if (phishingProtectionEditText == null || phishingProtectionCheckBox == null
                    || descriptionPreference == null) {
                return;
            }
            if (EditTextUtils.isNotEmpty(phishingProtection)) {
                phishingProtectionEditText.setTitle(phishingProtection);
            }
            phishingProtectionCheckBox.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean value = (boolean) newValue;
                if (value) {
                    String randomPass = EncodeUtils.randomPass(6);
                    phishingProtectionEditText.setTitle(randomPass);
                    phishingProtectionEditText.setText(randomPass);
                    settingsModel.updateAntiPhishingPhrase(settingId, true, randomPass);
                } else {
                    phishingProtectionEditText.setTitle(getString(R.string.settings_type_anti_phishing_phrase));
                    phishingProtectionEditText.setText("");
                    settingsModel.updateAntiPhishingPhrase(settingId, false, "");
                }
                Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                return true;
            });

            phishingProtectionEditText.setOnPreferenceChangeListener((preference, newValue) -> {
                String value = (String) newValue;
                if (TextUtils.isEmpty(value)) {
                    phishingProtectionEditText.setTitle(getString(R.string.settings_type_anti_phishing_phrase));
                    phishingProtectionCheckBox.setChecked(false);
                } else {
                    phishingProtectionEditText.setTitle(value);
                }
                settingsModel.updateAntiPhishingPhrase(settingId, EditTextUtils.isNotEmpty(value),
                        value);
                Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                return true;
            });

            descriptionPreference.setTitle(HtmlUtils.fromHtml(
                    getString(R.string.settings_phishing_protection_description))
            );
            descriptionPreference.setOnPreferenceClickListener(preference -> {
                Intent webMailIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(BuildConfig.ORIGIN));
                startActivity(webMailIntent);
                return true;
            });
        }
    }

    private void requestMySelfData() {
        userRepository.getMyselfInfo()
                .subscribe(new Observer<MyselfResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Timber.i("Request myself info");
                    }

                    @Override
                    public void onNext(@NotNull MyselfResponse myselfResponse) {
                        if (myselfResponse.getResult() != null) {
                            MyselfResult myselfResult = myselfResponse.getResult()[0];
                            saveData(myselfResult);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void saveData(MyselfResult myselfResult) {
        SettingsResponse settingsResponse = myselfResult.getSettings();
        settingId = settingsResponse.getId();
        isPrimeUser = myselfResult.isPrime();
        setSettingId(settingId);

        String usedStorage = UnitUtils.memoryDisplay(settingsResponse.getUsedStorage());
        String allocatedStorage = UnitUtils.memoryDisplay(settingsResponse.getAllocatedStorage());
        String recoveryEmail = settingsResponse.getRecoveryEmail();

        if (storageLimitPreference != null) {
            storageLimitPreference.setSummary(getString(
                    R.string.storage_limit_info,
                    usedStorage,
                    allocatedStorage
            ));
        }
        if (recoveryEmailPreferenceScreen != null) {
            recoveryEmailPreferenceScreen.setSummary(settingsResponse.getRecoveryEmail());
        }

        sharedPreferences.edit()
                .putString(getString(R.string.recovery_email), recoveryEmail)
                .putBoolean(getString(R.string.anti_phishing_enabled), settingsResponse.isAntiPhishingEnabled())
                .putString(getString(R.string.anti_phishing_key), settingsResponse.getAntiPhishingPhrase())
                .putString(getString(R.string.notification_email_key), settingsResponse.getNotificationEmail())
                .putBoolean(getString(R.string.recovery_email_enabled), EditTextUtils.isNotEmpty(recoveryEmail))
                .putBoolean(getString(R.string.auto_save_contacts_enabled), settingsResponse.isSaveContacts())
                .putBoolean(getString(R.string.key_auto_read_email_enabled), settingsResponse.isAutoRead())
                .putBoolean(getString(R.string.contacts_encryption_enabled), settingsResponse.isContactsEncrypted())
                .putBoolean(getString(R.string.block_external_images_key), settingsResponse.isDisableLoadingImages())
                .putBoolean(getString(R.string.warn_external_link_key), settingsResponse.isWarnExternalLink())
                .putBoolean(getString(R.string.report_bugs_enabled), settingsResponse.isEnableReportBugs())
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
//                setOnClickPreference(preference);
                if (preference instanceof PreferenceCategory) {
                    PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.getPreference(i);
                    for (int j = 0; j < preferenceCategory.getPreferenceCount(); j++) {
                        Preference inner = preferenceCategory.getPreference(j);
                        //setOnClickPreference(inner);
                    }
                }
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

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
