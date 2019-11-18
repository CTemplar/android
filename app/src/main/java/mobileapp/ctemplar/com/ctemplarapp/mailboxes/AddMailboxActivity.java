package mobileapp.ctemplar.com.ctemplarapp.mailboxes;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Domains.DomainsResults;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class AddMailboxActivity extends BaseActivity {
    private MailboxesViewModel mailboxesModel;
    private String emailAddress;
    private ProgressDialog progressDialog;

    @BindView(R.id.activity_add_mailbox_domains)
    Spinner domainSpinner;

    @BindView(R.id.activity_add_mailbox_username)
    TextInputEditText emailEditText;

    @BindView(R.id.activity_add_mailbox_create_btn)
    Button createMailboxButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_mailbox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mailboxesModel = ViewModelProviders.of(this).get(MailboxesViewModel.class);

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
        domainsList.add("ctemplar.com");
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
        mailboxesModel.createMailboxResponseStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(getApplicationContext(), getString(R.string.mailbox_alias_creation_success), Toast.LENGTH_LONG).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR_PAID) {
                Toast.makeText(getApplicationContext(), getString(R.string.mailbox_alias_creation_paid), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.mailbox_alias_creation_failed), Toast.LENGTH_LONG).show();
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
                checkEmailAddress();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        domainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkEmailAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void checkEmailAddress() {
        String domain = domainSpinner.getSelectedItem().toString();
        String username = EditTextUtils.getText(emailEditText);
        if (username.isEmpty()) {
            return;
        }
        if (username.length() < 2) {
            emailEditText.setError(getString(R.string.error_username_small_two));
            return;
        }
        if (!EditTextUtils.isTextValid(username)) {
            emailEditText.setError(getString(R.string.error_username_incorrect));
            return;
        }
        emailAddress = username + "@" + domain;
        createMailboxButton.setEnabled(false);
        mailboxesModel.checkUsername(emailAddress);
    }

    @OnClick(R.id.activity_add_mailbox_create_btn)
    public void OnClickCreateMailbox() {
        progressDialog.show();
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
