package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

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
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);

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
//                    Intent whiteBlackList = new Intent(getActivity(), WhiteBlackList.class);
//                    startActivity(whiteBlackList);
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

            final EditTextPreference preferenceRecoveryEmail = (EditTextPreference) findPreference(getString(R.string.recovery_email));
            if (preferenceRecoveryEmail.getText() != null && !preferenceRecoveryEmail.getText().isEmpty()) {
                preferenceRecoveryEmail.setTitle(preferenceRecoveryEmail.getText());
            }
            preferenceRecoveryEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = (String) newValue;
                    if (EditTextUtils.isEmailValid(value)) {
                        preferenceRecoveryEmail.setTitle((String) newValue);
                        return true;
                    } else {
                        Toast.makeText(getActivity(), "Email is not valid", Toast.LENGTH_SHORT).show();
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
                        preferenceSignature.setTitle("Type your signature");
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
                        preferenceMobileSignature.setTitle("Type your mobile signature");
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
