package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivitySettingsKeysBinding;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import timber.log.Timber;

public class KeysActivity extends AppCompatActivity {
    private static final String PUBLIC_KEY_FORMAT = "public.asc";
    private static final String PRIVATE_KEY_FORMAT = "private.asc";
    private static final String SPLITTER = "_";

    private ActivitySettingsKeysBinding binding;
    private MailboxViewModel mailboxViewModel;

    private Map<MailboxEntity, List<GeneralizedMailboxKey>> mailboxKeyMap;
    private GeneralizedMailboxKey keyToDownload;
    private boolean isPrivateKeyToDownload;
    private MakeAsPrimaryKeyDialog markAsPrimaryDialog;
    private DeleteKeyDialog removeKeyDialog;
    private UserRepository userRepository;

    private final ActivityResultLauncher<String[]> downloadKeyPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    return;
                }
                downloadKey(keyToDownload, isPrivateKeyToDownload);
            });

    private final MailboxKeysAdapter adapter = new MailboxKeysAdapter(new MailboxKeysAdapter.ClickCallback() {
        @Override
        public void onSetAsPrimaryClick(GeneralizedMailboxKey key) {
            openSetKeyAsPrimaryDialog(key);
        }

        @Override
        public void onDownloadKeyClick(GeneralizedMailboxKey key) {
            openDownloadKeyDialog(key);
        }

        @Override
        public void onRemoveKeyClick(GeneralizedMailboxKey key) {
            openRemoveKeyDialog(key);
        }
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
        binding = ActivitySettingsKeysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding.keysRecyclerView.setAdapter(adapter);
        binding.emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMailbox(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.addNewKeyButton.setOnClickListener(v -> startActivity(
                new Intent(KeysActivity.this, AddMailboxKeyActivity.class)));
        binding.importKeyButton.setOnClickListener(v -> startActivity(
                new Intent(KeysActivity.this, ImportMailboxKeyActivity.class)));


        userRepository = CTemplarApp.getUserRepository();
        mailboxViewModel = new ViewModelProvider(this).get(MailboxViewModel.class);
        mailboxViewModel.getMailboxPrimaryResponseStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                int currentPosition = binding.emailSpinner.getSelectedItemPosition();
                onMailboxKeysUpdated(mailboxViewModel.getMailboxKeyMap());
                if (markAsPrimaryDialog != null) {
                    markAsPrimaryDialog.dismiss();
                    markAsPrimaryDialog = null;
                }
                binding.emailSpinner.setSelection(currentPosition);
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                ToastUtils.showLongToast(getApplicationContext(), getString(R.string.operation_failed));
                if (markAsPrimaryDialog != null) {
                    markAsPrimaryDialog.setLoading(false);
                }
            }
        });
        mailboxViewModel.deleteMailboxKeyResponseStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                int currentPosition = binding.emailSpinner.getSelectedItemPosition();
                onMailboxKeysUpdated(mailboxViewModel.getMailboxKeyMap());
                if (removeKeyDialog != null) {
                    removeKeyDialog.dismiss();
                    removeKeyDialog = null;
                }
                binding.emailSpinner.setSelection(currentPosition);
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                ToastUtils.showLongToast(getApplicationContext(), getString(R.string.operation_failed));
                if (removeKeyDialog != null) {
                    removeKeyDialog.setLoading(false);
                }
            }
        });
        onMailboxKeysUpdated(mailboxViewModel.getMailboxKeyMap());
        selectMailbox(0);
    }

    private void onMailboxKeysUpdated(Map<MailboxEntity, List<GeneralizedMailboxKey>> mailboxKeyMap) {
        if (mailboxKeyMap == null || mailboxKeyMap.isEmpty()) {
            onBackPressed();
            return;
        }
        this.mailboxKeyMap = mailboxKeyMap;
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
    }

    private MailboxEntity getMailboxByIndex(int index) {
        int counter = 0;
        for (MailboxEntity mailboxEntity : mailboxKeyMap.keySet()) {
            if (counter++ == index) {
                return mailboxEntity;
            }
        }
        return null;
    }

    private void selectMailbox(int index) {
        selectMailbox(getMailboxByIndex(index));
    }

    private void selectMailbox(MailboxEntity mailbox) {
        if (mailbox == null) {
            Timber.e("Mailbox is null");
            return;
        }
        List<GeneralizedMailboxKey> keys = mailboxKeyMap.get(mailbox);
        if (keys == null) {
            return;
        }
        adapter.setItems(keys);
    }

    private MailboxEntity getKeyMailbox(GeneralizedMailboxKey key) {
        for (Map.Entry<MailboxEntity, List<GeneralizedMailboxKey>> mailboxEntityListEntry : mailboxKeyMap.entrySet()) {
            if (mailboxEntityListEntry.getValue().contains(key)) {
                return mailboxEntityListEntry.getKey();
            }
        }
        return null;
    }

    private void openSetKeyAsPrimaryDialog(GeneralizedMailboxKey key) {
        MakeAsPrimaryKeyDialog dialog = new MakeAsPrimaryKeyDialog();
        dialog.setOnApplyClickListener(() -> {
            dialog.setLoading(true);
            MailboxEntity mailbox = getKeyMailbox(key);
            dialog.setCancelable(false);
            if (mailbox == null) {
                Timber.wtf("openSetKeyAsPrimaryDialog: Mailbox is null");
                dialog.dismiss();
                return;
            }
            mailboxViewModel.updateMailboxPrimaryKey(mailbox.getId(), key.getId());
        });
        dialog.show(getSupportFragmentManager(), null);
        this.markAsPrimaryDialog = dialog;
    }

    private void openRemoveKeyDialog(GeneralizedMailboxKey key) {
        DeleteKeyDialog dialog = new DeleteKeyDialog();
        dialog.setOnApplyClickListener(() -> {
            dialog.setLoading(true);
            dialog.setCancelable(false);
            String username = userRepository.getUsername();
            String password = dialog.getPassword();
            mailboxViewModel.deleteMailboxKey(key.getId(), EncodeUtils.generateHash(username, password));
        });
        dialog.show(getSupportFragmentManager(), null);
        this.removeKeyDialog = dialog;
    }

    private void openDownloadKeyDialog(GeneralizedMailboxKey key) {
        DownloadKeyDialog dialog = new DownloadKeyDialog();
        dialog.setOnApplyClickListener(new DownloadKeyDialog.OnApplyClickListener() {
            @Override
            public void onDownloadPublicKeyClick() {
                downloadKey(key, false);
            }

            @Override
            public void onDownloadPrivateKeyClick() {
                downloadKey(key, true);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void downloadKey(GeneralizedMailboxKey key, boolean isPrivate) {
        if (!PermissionUtils.writeExternalStorage(this)) {
            keyToDownload = key;
            isPrivateKeyToDownload = isPrivate;
            downloadKeyPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
            return;
        }
        MailboxEntity mailboxEntity = getKeyMailbox(key);
        if (mailboxEntity == null) {
            ToastUtils.showLongToast(this, "Cannot download key");
            return;
        }
        String keyName;
        byte[] keyContent;
        if (isPrivate) {
            keyName = mailboxEntity.getEmail() + SPLITTER + key.getFingerprint()
                    + SPLITTER + PRIVATE_KEY_FORMAT;
            keyContent = key.getPrivateKey().getBytes();
        } else {
            keyName = mailboxEntity.getEmail() + SPLITTER + key.getFingerprint()
                    + SPLITTER + PUBLIC_KEY_FORMAT;
            keyContent = key.getPublicKey().getBytes();
        }

        File externalStorageFile = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File keyFile = new File(externalStorageFile, keyName);
        try {
            BufferedOutputStream bufferedOutputStream =
                    new BufferedOutputStream(new FileOutputStream(keyFile));
            bufferedOutputStream.write(keyContent);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return;
        }

        String savedToast = isPrivate ? getString(R.string.your_private_key_saved)
                : getString(R.string.your_public_key_saved);
        Toast.makeText(this, savedToast, Toast.LENGTH_SHORT).show();
    }
}
