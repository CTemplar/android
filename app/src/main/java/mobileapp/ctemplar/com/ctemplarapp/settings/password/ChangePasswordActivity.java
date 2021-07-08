package mobileapp.ctemplar.com.ctemplarapp.settings.password;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityChangePasswordBinding;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;

public class ChangePasswordActivity extends BaseActivity {
    private ActivityChangePasswordBinding binding;
    private ChangePasswordViewModel changePasswordModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        changePasswordModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        changePasswordModel.getResponseStatus().observe(this, this::handleResponse);
        changePasswordModel.getChangePasswordResponseError().observe(this,
                v -> ToastUtils.showToast(this, v));
        changePasswordModel.getActionsStatus().observe(this, this::handleMainActions);
        binding.changePasswordButton.setOnClickListener(v -> onChangePassword());
        setListeners();
    }

    private void handleMainActions(MainActivityActions mainActivityActions) {
        if (mainActivityActions == MainActivityActions.ACTION_LOGOUT) {
            startSignInActivity();
        }
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void handleResponse(ResponseStatus responseStatus) {
        dialogState(false);
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(this, R.string.toast_password_not_changed, Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(this, R.string.toast_password_changed, Toast.LENGTH_SHORT).show();
            changePasswordModel.logout();
        }
    }

    public void onChangePassword() {
        if (binding.newPasswordEditText.length() < getResources().getInteger(R.integer.restriction_password_min)) {
            binding.newPasswordInputLayout.setError(getString(R.string.error_password_small));
            return;
        }
        if (binding.newPasswordEditText.length() > getResources().getInteger(R.integer.restriction_password_max)) {
            binding.newPasswordInputLayout.setError(getString(R.string.error_password_big));
            return;
        }
        if (!TextUtils.equals(EditTextUtils.getText(binding.newPasswordEditText),
                EditTextUtils.getText(binding.newPasswordConfirmationEditText))
        ) {
            binding.newPasswordConfirmationInputLayout.setError(getString(R.string.error_password_not_match));
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_change_password)
                .setMessage(
                        binding.resetDataCheckBox.isChecked()
                                ? getString(R.string.dialog_change_password_confirm_reset)
                                : getString(R.string.dialog_change_password_confirm)
                )
                .setPositiveButton(R.string.btn_change, (dialog, which) -> {
                            changePasswordModel.changePassword(
                                    EditTextUtils.getText(binding.currentPasswordEditText),
                                    EditTextUtils.getText(binding.newPasswordEditText),
                                    binding.resetDataCheckBox.isChecked()
                            );
                            dialogState(true);
                        }
                )
                .setNeutralButton(R.string.btn_cancel, null)
                .show();
    }

    public void setListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.currentPasswordInputLayout.setError(null);
                binding.newPasswordInputLayout.setError(null);
                binding.newPasswordConfirmationInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.currentPasswordEditText.addTextChangedListener(textWatcher);
        binding.newPasswordEditText.addTextChangedListener(textWatcher);
        binding.newPasswordConfirmationEditText.addTextChangedListener(textWatcher);
    }

    private void dialogState(boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBackgroundView.setVisibility(View.VISIBLE);
            binding.appBarLayout.setVisibility(View.GONE);
            blockUI();
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.progressBackgroundView.setVisibility(View.GONE);
            binding.appBarLayout.setVisibility(View.VISIBLE);
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
