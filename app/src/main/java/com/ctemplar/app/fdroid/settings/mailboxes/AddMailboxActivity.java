package com.ctemplar.app.fdroid.settings.mailboxes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.BuildConfig;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityAddMailboxBinding;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.domains.DomainsResults;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.ThemeUtils;

public class AddMailboxActivity extends BaseActivity {
    private ActivityAddMailboxBinding binding;
    private MailboxesViewModel mailboxesModel;
    private ProgressDialog progressDialog;

    private final Handler handler = new Handler();
    private final Runnable inputFinishChecker = this::checkEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityAddMailboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mailboxesModel = new ViewModelProvider(this).get(MailboxesViewModel.class);

        progressDialog = new ProgressDialog(AddMailboxActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.mailbox_alias_creation_progress));

        final List<String> domainsList = new ArrayList<>();
        domainsList.add(BuildConfig.DOMAIN);
        setDomains(domainsList);

        mailboxesModel.getDomainsResponse().observe(this, domainsResponse -> {
            if (domainsResponse != null) {
                List<DomainsResults> domainsResultList = domainsResponse.getDomainsResultsList();
                for (DomainsResults domain : domainsResultList) {
                    domainsList.add(domain.getDomain());
                }
                setDomains(domainsList);
            }
        });
        mailboxesModel.getDomains();
        binding.createAddressButton.setEnabled(false);
        mailboxesModel.getCheckUsernameResponse().observe(this, checkUsernameResponse -> {
            if (checkUsernameResponse != null) {
                boolean isExists = checkUsernameResponse.isExists();
                binding.createAddressButton.setEnabled(!isExists);
                if (isExists) {
                    binding.usernameEditText.setError(getString(R.string.mailbox_alias_exists));
                }
            }
        });
        mailboxesModel.getCheckUsernameStatus().observe(this, checkUsernameStatus -> {
            if (checkUsernameStatus == ResponseStatus.RESPONSE_ERROR_TOO_MANY_REQUESTS) {
                Toast.makeText(this, R.string.error_too_many_requests, Toast.LENGTH_LONG).show();
            } else if (checkUsernameStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(this, R.string.error_connection, Toast.LENGTH_LONG).show();
            }
        });
        mailboxesModel.createMailboxResponseStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(this, R.string.mailbox_alias_creation_success, Toast.LENGTH_LONG).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR_PAID) {
                Toast.makeText(this, R.string.mailbox_alias_creation_paid, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.mailbox_alias_creation_failed, Toast.LENGTH_LONG).show();
            }
            progressDialog.cancel();
            onBackPressed();
        });
        addListeners();
    }

    private void setDomains(List<String> domainsList) {
        SpinnerAdapter domainsAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                domainsList
        );
        binding.domainsSpinner.setAdapter(domainsAdapter);
    }

    private void addListeners() {
        binding.usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(inputFinishChecker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.createAddressButton.setEnabled(false);
                binding.usernameEditText.setError(null);
                if (s.length() > 0) {
                    handler.postDelayed(inputFinishChecker, getResources().getInteger(R.integer.typing_delay));
                }
            }
        });
        binding.createAddressButton.setOnClickListener(v -> onClickCreateMailbox());
    }

    private void checkEmailAddress() {
        String username = EditTextUtils.getText(binding.usernameEditText);
        if (username.length() < getResources().getInteger(R.integer.restriction_username_min)) {
            binding.usernameEditText.setError(getString(R.string.error_username_small));
            return;
        }
        if (username.length() > getResources().getInteger(R.integer.restriction_username_max)) {
            binding.usernameEditText.setError(getString(R.string.error_username_big));
            return;
        }
        if (!EditTextUtils.isUsernameValid(username)) {
            binding.usernameEditText.setError(getString(R.string.error_username_incorrect));
            return;
        }
        mailboxesModel.checkUsername(username);
    }

    public void onClickCreateMailbox() {
        progressDialog.show();
        String username = EditTextUtils.getText(binding.usernameEditText);
        String domain = binding.domainsSpinner.getSelectedItem().toString();
        String emailAddress = username + "@" + domain;
        mailboxesModel.createMailbox(emailAddress);
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
