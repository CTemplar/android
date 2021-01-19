package com.ctemplar.app.fdroid.mailboxes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.BuildConfig;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.domains.DomainsResults;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class AddMailboxActivity extends BaseActivity {
    private MailboxesViewModel mailboxesModel;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler();
    private Runnable inputFinishChecker = this::checkEmailAddress;

    @BindView(R.id.activity_add_mailbox_domains)
    Spinner domainSpinner;

    @BindView(R.id.activity_add_mailbox_username)
    TextInputEditText emailEditText;

    @BindView(R.id.activity_add_mailbox_create_btn)
    Button createMailboxButton;

    @BindInt(R.integer.restriction_username_min)
    int USERNAME_MIN;

    @BindInt(R.integer.restriction_username_max)
    int USERNAME_MAX;

    @BindInt(R.integer.typing_delay)
    int TYPING_DELAY;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_mailbox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mailboxesModel = new ViewModelProvider(this).get(MailboxesViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(AddMailboxActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.mailbox_alias_creation_progress));

        final List<String> domainsList = new ArrayList<>();
        domainsList.add(BuildConfig.DOMAIN);
        setDomains(domainsList);

        mailboxesModel.getDomains();
        mailboxesModel.getDomainsResponse().observe(this, domainsResponse -> {
            if (domainsResponse != null) {
                List<DomainsResults> domainsResultList = domainsResponse.getDomainsResultsList();
                for (DomainsResults domain : domainsResultList) {
                    domainsList.add(domain.getDomain());
                }
                setDomains(domainsList);
            }
        });
        createMailboxButton.setEnabled(false);
        mailboxesModel.getCheckUsernameResponse().observe(this, checkUsernameResponse -> {
            if (checkUsernameResponse != null) {
                boolean isExists = checkUsernameResponse.isExists();
                createMailboxButton.setEnabled(!isExists);
                if (isExists) {
                    emailEditText.setError(getString(R.string.mailbox_alias_exists));
                }
            }
        });
        mailboxesModel.getCheckUsernameStatus().observe(this, checkUsernameStatus -> {
            if (checkUsernameStatus == ResponseStatus.RESPONSE_ERROR_TOO_MANY_REQUESTS) {
                Toast.makeText(this, getString(R.string.error_too_many_requests), Toast.LENGTH_LONG).show();
            } else if (checkUsernameStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
            }
        });
        mailboxesModel.createMailboxResponseStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(this, getString(R.string.mailbox_alias_creation_success), Toast.LENGTH_LONG).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR_PAID) {
                Toast.makeText(this, getString(R.string.mailbox_alias_creation_paid), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.mailbox_alias_creation_failed), Toast.LENGTH_LONG).show();
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
        domainSpinner.setAdapter(domainsAdapter);
    }

    private void addListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(inputFinishChecker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                createMailboxButton.setEnabled(false);
                emailEditText.setError(null);
                if (s.length() > 0) {
                    handler.postDelayed(inputFinishChecker, TYPING_DELAY);
                }
            }
        });
    }

    private void checkEmailAddress() {
        String username = EditTextUtils.getText(emailEditText);
        if (username.length() < USERNAME_MIN) {
            emailEditText.setError(getString(R.string.error_username_small));
            return;
        }
        if (username.length() > USERNAME_MAX) {
            emailEditText.setError(getString(R.string.error_username_big));
            return;
        }
        if (!EditTextUtils.isUsernameValid(username)) {
            emailEditText.setError(getString(R.string.error_username_incorrect));
            return;
        }
        mailboxesModel.checkUsername(username);
    }

    @OnClick(R.id.activity_add_mailbox_create_btn)
    public void OnClickCreateMailbox() {
        progressDialog.show();
        String username = EditTextUtils.getText(emailEditText);
        String domain = domainSpinner.getSelectedItem().toString();
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
