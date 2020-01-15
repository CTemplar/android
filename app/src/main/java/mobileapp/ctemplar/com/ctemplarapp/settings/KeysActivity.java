package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import timber.log.Timber;

public class KeysActivity extends BaseActivity {

    @BindView(R.id.activity_setting_keys_address_spinner)
    Spinner mailboxSpinner;

    @BindView(R.id.activity_setting_keys_fingerprint_text_view)
    TextView fingerprintTextView;

    @BindView(R.id.activity_setting_keys_public_key_text_view)
    TextView downloadPublicKeyTextView;

    private KeysViewModel keysModel;
    private List<MailboxEntity> mailboxEntityList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_keys;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        keysModel = ViewModelProviders.of(this).get(KeysViewModel.class);
        mailboxEntityList = keysModel.getMailboxEntityList();
        String[] addresses = new String[mailboxEntityList.size()];
        for (int addressIterator = 0; addressIterator < addresses.length; addressIterator++) {
            addresses[addressIterator] = mailboxEntityList.get(addressIterator).getEmail();
        }
        SpinnerAdapter addressAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                addresses
        );
        mailboxSpinner.setAdapter(addressAdapter);

        SpannableString downloadTitleSpannable = new SpannableString(getString(R.string.download_public_key));
        downloadTitleSpannable.setSpan(new UnderlineSpan(), 0, downloadTitleSpannable.length(), 0);
        downloadPublicKeyTextView.setText(downloadTitleSpannable);
        setListeners();
    }

    private void setListeners() {
        mailboxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fingerprintTextView.setText(mailboxEntityList.get(position).getFingerprint());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        downloadPublicKeyTextView.setOnClickListener(v -> {
            int selectedItemPosition = mailboxSpinner.getSelectedItemPosition();
            MailboxEntity mailboxEntity = mailboxEntityList.get(selectedItemPosition);
            String keyName = mailboxEntity.getEmail() + "_" + mailboxEntity.getFingerprint() + ".asc";
            byte[] keyContent = mailboxEntity.getPublicKey().getBytes();

            if (!PermissionCheck.readAndWriteExternalStorage(this)) {
                return;
            }
            File externalStorageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File keyFile = new File(externalStorageFile, keyName);
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(keyFile));
                bufferedOutputStream.write(keyContent);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (IOException e) {
                Timber.e(e);
            }
            Toast.makeText(this, getString(R.string.your_key_saved), Toast.LENGTH_SHORT).show();
        });
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
