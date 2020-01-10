package mobileapp.ctemplar.com.ctemplarapp.settings;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class ChangePasswordActivity extends BaseActivity {

    private String userCurrentPassword;
    private ChangePasswordViewModel changePasswordModel;

    @BindView(R.id.activity_change_password_current_input_layout)
    TextInputLayout editTextCurrentPasswordLayout;

    @BindView(R.id.activity_change_password_current_input)
    EditText editTextCurrentPassword;

    @BindView(R.id.activity_change_password_new_input_layout)
    TextInputLayout editTextNewPasswordLayout;

    @BindView(R.id.activity_change_password_new_input)
    EditText editTextNewPassword;

    @BindView(R.id.activity_change_password_confirm_input_layout)
    TextInputLayout editTextPasswordConfirmationLayout;

    @BindView(R.id.activity_change_password_confirm_input)
    EditText editTextPasswordConfirmation;

    @BindView(R.id.activity_change_password_reset_check)
    AppCompatCheckBox checkBoxResetData;

    @BindView(R.id.progress_bar)
    public ProgressBar progress;

    @BindView(R.id.progress_background)
    public View progressBackground;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
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

        changePasswordModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        userCurrentPassword = CTemplarApp.getInstance()
                .getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);

        changePasswordModel.getResponseStatus().observe(this, this::handleResponse);

        changePasswordModel.getActionsStatus().observe(this, this::handleMainActions);

        setListeners();
    }

    private void handleMainActions(MainActivityActions mainActivityActions) {
        if (mainActivityActions == MainActivityActions.ACTION_LOGOUT) {
            startSignInActivity();
        }
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void handleResponse(ResponseStatus responseStatus) {
        dialogState(false);
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(this, getResources().getString(R.string.toast_password_not_changed), Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(this, getResources().getString(R.string.toast_password_changed), Toast.LENGTH_SHORT).show();
            changePasswordModel.logout();
        }
    }

    @OnClick(R.id.activity_change_password_button)
    public void OnClickChange() {
        final String currentPassword = editTextCurrentPassword.getText().toString();
        final String newPassword = editTextNewPassword.getText().toString();
        final String passwordConfirmation = editTextPasswordConfirmation.getText().toString();
        final boolean resetData = checkBoxResetData.isChecked();

        if(!TextUtils.equals(userCurrentPassword, currentPassword)) {
            Toast.makeText(this, getResources().getString(R.string.error_current_password_not_match),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.equals(newPassword, passwordConfirmation)
                && EditTextUtils.isTextLength(newPassword, 8, 64)) {

            String alertMessage = getResources().getString(R.string.dialog_change_password_confirm);
            if (resetData) {
                alertMessage = getResources().getString(R.string.dialog_change_password_confirm_reset);
            }
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_change_password))
                    .setMessage(alertMessage)
                    .setPositiveButton(getResources().getString(R.string.btn_change), (dialog, which) -> {
                        changePasswordModel.changePassword(currentPassword, newPassword, resetData);
                        dialogState(true);
                    }
                    )
                    .setNeutralButton(getResources().getString(R.string.btn_cancel), null)
                    .show();
        } else {
            Toast.makeText(this, getString(R.string.error_password_small), Toast.LENGTH_SHORT).show();
        }
    }

    public void setListeners() {
        editTextCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    editTextPasswordConfirmationLayout.setError(getString(R.string.error_enter_old_password));
                } else {
                    editTextPasswordConfirmationLayout.setError(null);
                }

                String oldPassword = editTextCurrentPassword.getText().toString();
                if (TextUtils.isEmpty(oldPassword)) {
                    editTextCurrentPasswordLayout.setError(getString(R.string.error_enter_old_password));
                } else {
                    editTextCurrentPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String passwordConfirmation = editTextPasswordConfirmation.getText().toString();
                if (TextUtils.equals(s, passwordConfirmation)) {
                    editTextPasswordConfirmationLayout.setError(null);
                } else {
                    editTextPasswordConfirmationLayout.setError(getString(R.string.error_password_not_match));
                }

                String oldPassword = editTextCurrentPassword.getText().toString();
                if (TextUtils.isEmpty(oldPassword)) {
                    editTextCurrentPasswordLayout.setError(getString(R.string.error_enter_old_password));
                } else {
                    editTextCurrentPasswordLayout.setError(null);
                }
            }
        });

        editTextPasswordConfirmation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = editTextNewPassword.getText().toString();
                if (TextUtils.equals(s, password)) {
                    editTextPasswordConfirmationLayout.setError(null);
                } else {
                    editTextPasswordConfirmationLayout.setError(getString(R.string.error_password_not_match));
                }

                String oldPassword = editTextCurrentPassword.getText().toString();
                if (TextUtils.isEmpty(oldPassword)) {
                    editTextCurrentPasswordLayout.setError(getString(R.string.error_enter_old_password));
                } else {
                    editTextCurrentPasswordLayout.setError(null);
                }
            }
        });
    }

    private void dialogState(boolean state) {
        if (state) {
            progress.setVisibility(View.VISIBLE);
            progressBackground.setVisibility(View.VISIBLE);
            blockUI();
        } else {
            progress.setVisibility(View.GONE);
            progressBackground.setVisibility(View.GONE);
            unlockUI();
        }
    }

    public void blockUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void unlockUI() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
