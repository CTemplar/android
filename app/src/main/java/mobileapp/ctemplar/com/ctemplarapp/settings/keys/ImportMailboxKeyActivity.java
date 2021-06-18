package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityMailboxKeyImportBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.addKeyTextView.setOnClickListener(v -> onClickSelectKey());
        binding.privateKeyNameTextView.setVisibility(View.GONE);
        binding.passwordLayout.setVisibility(View.GONE);
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
        KeyType privateKeyType = PGPManager.getKeyType(privateKey);
        if (privateKeyType == null) {
            ToastUtils.showToast(this, getString(R.string.selected_key_is_not_valid));
            return;
        }
        this.privateKey = privateKey;
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
}
