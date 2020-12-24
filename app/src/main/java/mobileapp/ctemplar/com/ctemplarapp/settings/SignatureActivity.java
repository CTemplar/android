package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.UpgradeToPrimeFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.HtmlUtils;

public class SignatureActivity extends BaseActivity {
    @BindView(R.id.activity_settings_signature_switch)
    SwitchCompat signatureSwitch;

    @BindView(R.id.activity_settings_signature_email_text_view)
    MaterialTextView mailboxSpinnerHint;

    @BindView(R.id.activity_settings_signature_address_spinner)
    AppCompatSpinner mailboxSpinner;

    @BindView(R.id.activity_settings_signature_display_name_text_view)
    MaterialTextView displayNameHint;

    @BindView(R.id.activity_settings_signature_display_name_input_layout)
    TextInputLayout displayNameInputLayout;

    @BindView(R.id.activity_settings_signature_display_name_input)
    TextInputEditText displayNameEditText;

    @BindView(R.id.activity_settings_signature_layout)
    LinearLayoutCompat signatureLayout;

    @BindView(R.id.activity_settings_text_formatting_bold_text_view)
    MaterialTextView formatBoldTextView;

    @BindView(R.id.activity_settings_text_formatting_italic_text_view)
    MaterialTextView formatItalicTextView;

    @BindView(R.id.activity_settings_text_formatting_underline_text_view)
    MaterialTextView formatUnderlineTextView;

    @BindView(R.id.activity_settings_text_formatting_monospace_text_view)
    MaterialTextView formatMonospaceTextView;

    @BindView(R.id.activity_settings_text_formatting_normal_text_view)
    MaterialTextView formatNormalTextView;

    @BindView(R.id.activity_settings_signature_edit_text)
    TextInputEditText signatureEditText;

    @BindView(R.id.activity_settings_signature_save_button)
    AppCompatButton saveButton;

    @BindView(R.id.activity_settings_signature_progress_bar)
    ContentLoadingProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_signature;
    }

    private SettingsViewModel settingsViewModel;
    private List<MailboxEntity> mailboxEntityList;
    private boolean isUserPrime;

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
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        settingsViewModel.getMyselfInfo();

        mailboxEntityList = settingsViewModel.getAllMailboxes();
        String[] addresses = new String[mailboxEntityList.size()];
        for (int addressIterator = 0; addressIterator < addresses.length; addressIterator++) {
            addresses[addressIterator] = mailboxEntityList.get(addressIterator).getEmail();
        }
        SpinnerAdapter addressAdapter = new ArrayAdapter<>(this,
                R.layout.item_domain_spinner, addresses);
        mailboxSpinner.setAdapter(addressAdapter);

        settingsViewModel.getMySelfResponse().observe(this, this::handleMySelfResponse);
        settingsViewModel.getUpdateSignatureStatus().observe(this, this::handleUpdateSignatureStatus);

        setListeners();
    }

    private void handleUpdateSignatureStatus(ResponseStatus responseStatus) {
        progressBar.hide();
        if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(this, R.string.error_connection, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_LONG).show();
        }
    }

    private void handleMySelfResponse(MyselfResponse myselfResponse) {
        progressBar.hide();
        MyselfResult myselfResult = myselfResponse.getResult()[0];
        isUserPrime = myselfResult.isPrime();
        if (isUserPrime) {
            signatureSwitch.setEnabled(true);
            signatureEditText.setFocusableInTouchMode(true);
        } else {
            signatureEditText.setOnClickListener(v -> upgradeToPrimeDialog());
        }
    }

    private void setListeners() {
        boolean isSignatureEnabled = settingsViewModel.isSignatureEnabled();
        showSignatureLayout(isSignatureEnabled);

        signatureSwitch.setChecked(isSignatureEnabled);
        signatureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsViewModel.setSignatureEnabled(isChecked);
            showSignatureLayout(isChecked);
            if (isChecked) {
                Toast.makeText(this, R.string.txt_signature_enabled, Toast.LENGTH_LONG).show();
            } else {
                signatureEditText.setText("");
                saveSignature();
            }
        });

        mailboxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MailboxEntity mailboxEntity = mailboxEntityList.get(position);
                if (mailboxEntity != null) {
                    String displayName = mailboxEntity.getDisplayName();
                    String signatureText = mailboxEntity.getSignature();
                    displayNameEditText.setText(displayName);
                    signatureEditText.setText(HtmlUtils.fromHtml(signatureText));
                    if (displayName != null) {
                        displayNameEditText.setSelection(displayName.length());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signatureEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        formatBoldTextView.setOnClickListener(v -> signatureTypeface(new StyleSpan(Typeface.BOLD)));
        formatItalicTextView.setOnClickListener(v -> signatureTypeface(new StyleSpan(Typeface.ITALIC)));
        formatUnderlineTextView.setOnClickListener(v -> signatureTypeface(new UnderlineSpan()));
        formatMonospaceTextView.setOnClickListener(v -> signatureTypeface(new TypefaceSpan("monospace")));
        formatNormalTextView.setOnClickListener(v -> signatureClearSpans());

        saveButton.setOnClickListener(v -> saveSignature());

        SpannableString underlineFormatting = new SpannableString(getString(R.string.txt_panel_underline));
        underlineFormatting.setSpan(new UnderlineSpan(), 0, 1, 0);
        SpannableString monospaceFormatting = new SpannableString(getString(R.string.txt_panel_monospace));
        monospaceFormatting.setSpan(new TypefaceSpan("monospace"), 0, 1, 0);
        formatUnderlineTextView.setText(underlineFormatting);
        formatMonospaceTextView.setText(monospaceFormatting);
    }

    private void saveSignature() {
        MailboxEntity selectedMailbox = mailboxEntityList.get(mailboxSpinner.getSelectedItemPosition());
        String displayName = EditTextUtils.getText(displayNameEditText);
        String signature = isUserPrime ? HtmlUtils.toHtml(signatureEditText.getText())
                : selectedMailbox.getSignature();
        settingsViewModel.updateSignature(selectedMailbox.getId(), displayName, signature);
        progressBar.show();
    }

    private void signatureTypeface(Object styleSpan) {
        int startIndex = signatureEditText.getSelectionStart();
        int endIndex = signatureEditText.getSelectionEnd();
        SpannableString signatureSpannable = new SpannableString(signatureEditText.getText());
        signatureSpannable.setSpan(styleSpan, startIndex, endIndex, 0);
        signatureEditText.setText(signatureSpannable);
        signatureEditText.setSelection(endIndex);
    }

    private void signatureClearSpans() {
        int startIndex = signatureEditText.getSelectionStart();
        int endIndex = signatureEditText.getSelectionEnd();
        String signatureText = EditTextUtils.getText(signatureEditText);
        char[] substring = signatureText.substring(startIndex, endIndex).toCharArray();
        if (substring.length > 0) {
            signatureEditText.setText(substring, startIndex, substring.length);
            signatureEditText.setSelection(endIndex);
        }
    }

    private void showSignatureLayout(boolean state) {
        signatureLayout.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void upgradeToPrimeDialog() {
        UpgradeToPrimeFragment upgradeToPrimeFragment = new UpgradeToPrimeFragment();
        upgradeToPrimeFragment.show(getSupportFragmentManager(), "UpgradeToPrimeFragment");
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
