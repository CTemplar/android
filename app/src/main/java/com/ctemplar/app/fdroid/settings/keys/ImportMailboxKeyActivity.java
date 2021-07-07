package com.ctemplar.app.fdroid.settings.keys;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Map;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityMailboxKeyImportBinding;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.repository.entity.GeneralizedMailboxKey;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.enums.KeyType;
import com.ctemplar.app.fdroid.security.PGPManager;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.FileUtils;
import com.ctemplar.app.fdroid.utils.PermissionUtils;
import com.ctemplar.app.fdroid.utils.ThemeUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import timber.log.Timber;

public class ImportMailboxKeyActivity extends AppCompatActivity {
    private ActivityMailboxKeyImportBinding binding;
    private MailboxKeyViewModel mailboxKeyViewModel;

    private Map<MailboxEntity, List<GeneralizedMailboxKey>> mailboxKeyMap;
    private String privateKey;

    private final ActivityResultLauncher<String[]> selectKeyPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    return;
                }
                onClickSelectKey();
            });

    private final ActivityResultLauncher<String> selectKeyResultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                privateKey = null;
                if (uri == null) {
                    ToastUtils.showToast(this, getString(R.string.toast_attachment_unable_read_path));
                    Timber.e("onActivityResult keyUri is null");
                } else {
                    onSelectPrivateKey(uri);
                }
                updatePrivateKeyLayout(uri);
            });

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityMailboxKeyImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mailboxKeyViewModel = new ViewModelProvider(this).get(MailboxKeyViewModel.class);

        mailboxKeyMap = mailboxKeyViewModel.getMailboxKeyMap();
        if (mailboxKeyMap == null || mailboxKeyMap.isEmpty()) {
            onBackPressed();
            return;
        }
        String[] addresses = new String[mailboxKeyMap.size()];
        int iterator = 0;
        for (MailboxEntity mailbox : mailboxKeyMap.keySet()) {
            addresses[iterator++] = mailbox.getEmail();
        }
        SpinnerAdapter addressAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                addresses
        );
        binding.emailSpinner.setAdapter(addressAdapter);
        binding.emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                privateKey = null;
                updatePrivateKeyLayout(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.selectedKeyPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.selectedKeyPasswordLayout.getError() != null) {
                    binding.selectedKeyPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.accountPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.accountPasswordLayout.getError() != null) {
                    binding.accountPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.addKeyTextView.setOnClickListener(v -> onClickSelectKey());
        binding.importKeyButton.setOnClickListener(v -> onImportKey());
        binding.privateKeyNameTextView.setVisibility(View.GONE);
        binding.passwordLayout.setVisibility(View.GONE);

        mailboxKeyViewModel.getImportMailboxKeyResponseStatus().observe(this,
                this::handleImportMailboxStatus);
        mailboxKeyViewModel.getImportMailboxKeyErrorResponse().observe(this,
                responseStatus -> ToastUtils.showToast(this, responseStatus));
    }

    private void handleImportMailboxStatus(ResponseStatus response) {
        setLoading(false);
        if (response == ResponseStatus.RESPONSE_COMPLETE) {
            ToastUtils.showLongToast(getApplicationContext(), getString(R.string.import_new_key_message));
            setResult(RESULT_OK);
            onBackPressed();
        } else {
            ToastUtils.showLongToast(getApplicationContext(), getString(R.string.operation_failed));
        }
    }

    private void onClickSelectKey() {
        if (!PermissionUtils.readExternalStorage(this)) {
            selectKeyPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
            return;
        }
        selectKeyResultLauncher.launch("*/*");
    }

    private void onSelectPrivateKey(Uri privateKeyUri) {
        String privateKeyPath = FileUtils.getPath(this, privateKeyUri);
        if (privateKeyPath == null) {
            Timber.e("privateKeyPath is null");
            return;
        }
        byte[] privateKeyBytes = FileUtils.readBytesFromFile(privateKeyPath);
        if (privateKeyBytes == null) {
            Timber.e("privateKeyBytes is null");
            ToastUtils.showToast(this, getString(R.string.toast_attachment_unable_read_file));
            return;
        }
        String privateKey = new String(privateKeyBytes);
        KeyType keyType = PGPManager.getKeyType(privateKey);
        if (keyType == null) {
            ToastUtils.showToast(this, getString(R.string.selected_key_is_not_valid));
            return;
        }
        String keyFingerprint = PGPManager.getKeyFingerprint(privateKey);
        if (keyFingerprint == null || isKeyAlreadyInActiveUse(keyFingerprint)) {
            ToastUtils.showToast(this, getString(R.string.selected_key_already_in_active_use));
            return;
        }
        this.privateKey = privateKey;
    }

    private void onImportKey() {
        MailboxEntity mailboxEntity = getMailboxByIndex(binding.emailSpinner.getSelectedItemPosition());
        if (mailboxEntity == null) {
            Timber.e("mailboxEntity is null");
            return;
        }
        if (TextUtils.isEmpty(binding.selectedKeyPasswordEditText.getText())) {
            binding.selectedKeyPasswordLayout.setError(getString(R.string.error_field_cannot_be_empty));
            return;
        }
        if (TextUtils.isEmpty(binding.accountPasswordEditText.getText())) {
            binding.accountPasswordLayout.setError(getString(R.string.error_field_cannot_be_empty));
            return;
        }
        setLoading(true);
        mailboxKeyViewModel.importMailboxKey(
                mailboxEntity.getId(),
                privateKey,
                PGPManager.getKeyType(privateKey),
                EditTextUtils.getText(binding.selectedKeyPasswordEditText),
                EditTextUtils.getText(binding.accountPasswordEditText)
        );
    }

    @Nullable
    private MailboxEntity getMailboxByIndex(int index) {
        int counter = 0;
        for (MailboxEntity mailboxEntity : mailboxKeyMap.keySet()) {
            if (counter++ == index) {
                return mailboxEntity;
            }
        }
        return null;
    }

    private boolean isKeyAlreadyInActiveUse(@NonNull String fingerprint) {
        int selectedPosition = binding.emailSpinner.getSelectedItemPosition();
        MailboxEntity selectedMailbox = getMailboxByIndex(selectedPosition);
        List<GeneralizedMailboxKey> selectedKeys = mailboxKeyMap.get(selectedMailbox);
        if (selectedKeys == null) {
            return false;
        }
        for (GeneralizedMailboxKey selectedKey : selectedKeys) {
            if (fingerprint.equals(selectedKey.getFingerprint())) {
                return true;
            }
        }
        return false;
    }

    private void updatePrivateKeyLayout(Uri privateKeyUri) {
        if (TextUtils.isEmpty(privateKey)) {
            binding.privateKeyNameTextView.setVisibility(View.GONE);
            binding.passwordLayout.setVisibility(View.GONE);
            binding.addKeyTextView.setText(R.string.add_key);
        } else {
            binding.privateKeyNameTextView.setVisibility(View.VISIBLE);
            binding.passwordLayout.setVisibility(View.VISIBLE);
            binding.addKeyTextView.setText(R.string.change_key);
            binding.privateKeyNameTextView.setText(FileUtils.getFileName(this, privateKeyUri));
        }
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.importKeyButton.setVisibility(loading ? View.GONE : View.VISIBLE);
        binding.importKeyButton.setEnabled(!loading);
    }
}
