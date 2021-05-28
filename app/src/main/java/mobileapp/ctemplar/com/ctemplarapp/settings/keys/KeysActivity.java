package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivitySettingsKeysBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.settings.SettingsViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import timber.log.Timber;

public class KeysActivity extends AppCompatActivity {
    private static final String PUBLIC_KEY_FORMAT = "public.asc";
    private static final String PRIVATE_KEY_FORMAT = "private.asc";
    private static final String SPLITTER = "_";

    private ActivitySettingsKeysBinding binding;

    private SettingsViewModel settingsViewModel;
    private MailboxAdapter adapter = new MailboxAdapter();
    private List<MailboxEntity> mailboxEntityList;
    private boolean isPrivate;

    private final ActivityResultLauncher<String[]> downloadKeyPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    return;
                }
                downloadKey(isPrivate);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding.mailboxesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.mailboxesRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        binding.mailboxesRecyclerView.addItemDecoration(dividerItemDecoration);
        adapter.setItems(settingsViewModel.getAllMailboxes());
        setListeners();
    }

    private void setListeners() {
    }

    private void downloadKey(boolean isPrivate) {
        this.isPrivate = isPrivate;
        if (!PermissionUtils.writeExternalStorage(this)) {
            downloadKeyPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
            return;
        }
        int selectedItemPosition = 0;
        MailboxEntity mailboxEntity = mailboxEntityList.get(selectedItemPosition);

        String keyName;
        byte[] keyContent;
        if (isPrivate) {
            keyName = mailboxEntity.getEmail() + SPLITTER + mailboxEntity.getFingerprint()
                    + SPLITTER + PRIVATE_KEY_FORMAT;
            keyContent = mailboxEntity.getPrivateKey().getBytes();
        } else {
            keyName = mailboxEntity.getEmail() + SPLITTER + mailboxEntity.getFingerprint()
                    + SPLITTER + PUBLIC_KEY_FORMAT;
            keyContent = mailboxEntity.getPublicKey().getBytes();
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
