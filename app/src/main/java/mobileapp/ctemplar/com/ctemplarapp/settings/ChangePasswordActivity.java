package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;

public class ChangePasswordActivity extends BaseActivity {

    private String userCurrentPassword;
    private ChangePasswordViewModel changePasswordModel;

    @BindView(R.id.activity_change_password_current_input)
    EditText editTextCurrentPassword;
    @BindView(R.id.activity_change_password_new_input)
    EditText editTextNewPassword;
    @BindView(R.id.activity_change_password_confirm_input)
    EditText editTextPasswordConfirmation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changePasswordModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        userCurrentPassword = CTemplarApp.getInstance()
                .getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);

        changePasswordModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                handleResponse(responseStatus);
            }
        });

        changePasswordModel.getActionsStatus().observe(this, new Observer<MainActivityActions>() {
            @Override
            public void onChanged(@Nullable MainActivityActions mainActivityActions) {
                handleMainActions(mainActivityActions);
            }
        });
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

        if(!TextUtils.equals(userCurrentPassword, currentPassword)) {
            Toast.makeText(this, getResources().getString(R.string.error_current_password_not_match),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.equals(newPassword, passwordConfirmation) && !newPassword.isEmpty() && newPassword.length() > 7) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_change_password))
                    .setMessage(getResources().getString(R.string.dialog_change_password_confirm))
                    .setPositiveButton(getResources().getString(R.string.btn_change), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changePasswordModel.changePassword(currentPassword, newPassword, passwordConfirmation);
                                }
                            }
                    )
                    .setNeutralButton(getResources().getString(R.string.btn_cancel), null)
                    .show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_new_password_not_match),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
