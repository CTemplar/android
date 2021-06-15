package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityMailboxKeyAddBinding;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import timber.log.Timber;

public class AddMailboxKeyActivity extends AppCompatActivity {
    private ActivityMailboxKeyAddBinding binding;
    private MailboxKeyViewModel mailboxKeyViewModel;

    private Map<MailboxEntity, List<GeneralizedMailboxKey>> mailboxKeyMap;

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
        binding = ActivityMailboxKeyAddBinding.inflate(getLayoutInflater());
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

        binding.generateKeysButton.setOnClickListener(v -> onGenerateKeys());
        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.passwordLayout.getError() != null) {
                    binding.passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mailboxKeyViewModel.getAddMailboxKeyResponseStatus().observe(this,
                this::handleAddMailboxStatus);
        mailboxKeyViewModel.getAddMailboxKeyErrorResponse().observe(this,
                responseStatus -> ToastUtils.showToast(this, responseStatus));
    }

    private void handleAddMailboxStatus(ResponseStatus response) {
        setLoading(false);
        if (response == ResponseStatus.RESPONSE_COMPLETE) {
            ToastUtils.showLongToast(getApplicationContext(), getString(R.string.add_new_key_message));
        }
    }

    private void onGenerateKeys() {
        MailboxEntity mailboxEntity = getMailboxByIndex(binding.emailSpinner.getSelectedItemPosition());
        if (mailboxEntity == null) {
            Timber.e("mailboxEntity is null");
            return;
        }
        if (TextUtils.isEmpty(binding.passwordEditText.getText())) {
            binding.passwordLayout.setError(getString(R.string.error_field_cannot_be_empty));
            return;
        }
        setLoading(true);
        mailboxKeyViewModel.createMailboxKey(
                mailboxEntity.getId(),
                mailboxEntity.getEmail(),
                getSelectedKeyType(),
                EditTextUtils.getText(binding.passwordEditText)
        );
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

    private KeyType getSelectedKeyType() {
        return binding.selectKeyTypeRadioGroup.getCheckedRadioButtonId()
                == binding.eccDefaultRadioButton.getId() ? KeyType.ECC : KeyType.RSA4096;
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.generateKeysButton.setVisibility(loading ? View.GONE : View.VISIBLE);
        binding.generateKeysButton.setEnabled(!loading);
    }
}
