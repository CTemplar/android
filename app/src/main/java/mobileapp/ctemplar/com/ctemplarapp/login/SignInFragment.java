package mobileapp.ctemplar.com.ctemplarapp.login;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class SignInFragment extends BaseFragment {

    @BindInt(R.integer.restriction_username_min)
    int USERNAME_MIN;

    @BindInt(R.integer.restriction_username_max)
    int USERNAME_MAX;

    @BindView(R.id.fragment_sign_in_username_input)
    TextInputEditText editTextUsername;

    @BindView(R.id.fragment_sign_in_username_input_layout)
    TextInputLayout editTextUsernameLayout;

    @BindView(R.id.fragment_sign_in_password_input)
    TextInputEditText editTextPassword;

    @BindView(R.id.fragment_sign_in_password_input_layout)
    TextInputLayout editTextPasswordLayout;

    @BindView(R.id.fragment_sign_in_otp_code_input)
    TextInputEditText editTextOtpCode;

    @BindView(R.id.fragment_sign_in_otp_code_input_layout)
    TextInputLayout editTextOtpLayout;

    @BindView(R.id.fragment_sign_in_otp_code_title)
    TextView textViewOtpTitle;

    private LoginActivityViewModel loginActivityModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_in;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivityModel = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);
        loginActivityModel.getResponseStatus().observe(getActivity(), this::handleStatus);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnClick(R.id.fragment_sign_in_send)
    public void onClickLogin() {
        String username = "";
        String password = "";
        String otp = null;

        if (editTextUsername.getText() != null && editTextPassword.getText() != null) {
            username = EditTextUtils.getText(editTextUsername).trim().toLowerCase()
                    .replaceAll("@.+", "");
            password = EditTextUtils.getText(editTextPassword).trim();
        }
        if (editTextOtpCode.getText() != null && !editTextOtpCode.getText().toString().isEmpty()) {
            otp = EditTextUtils.getText(editTextOtpCode).trim();
        }
        if(isValid(username, password)) {
            loginActivityModel.showProgressDialog();
            loginActivityModel.signIn(username, password, otp);
        }
    }

    @OnClick(R.id.fragment_sign_in_forgot_txt)
    public void onClickForgotPassword() {
        loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_FORGOT_PASSWORD);
    }

    @OnClick(R.id.fragment_sign_in_create_account_txt)
    public void onClickCreateAccount() {
        loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_CREATE_ACCOUNT);
    }

    private void setListeners() {
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextUsernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleStatus(ResponseStatus status) {
        if(status != null) {
            loginActivityModel.hideProgressDialog();

            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_sign_in), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_ERROR_AUTH_FAILED:
                    Toast.makeText(getActivity(), getString(R.string.error_authentication_failed), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_WAIT_OTP:
                    show2FA();
                    break;
                case RESPONSE_NEXT:
                    sendFirebaseToken();
                    break;
            }
        }
    }

    private void show2FA() {
        editTextUsernameLayout.setVisibility(View.GONE);
        editTextPasswordLayout.setVisibility(View.GONE);
        textViewOtpTitle.setVisibility(View.VISIBLE);
        editTextOtpLayout.setVisibility(View.VISIBLE);
        editTextOtpLayout.requestFocus();
    }

    private void sendFirebaseToken() {
        loginActivityModel.getAddFirebaseTokenStatus().observe(this, responseStatus -> {
            loginActivityModel.clearDB();
            loginActivityModel.changeAction(LoginActivityActions.CHANGE_ACTIVITY_MAIN);
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), instanceIdResult -> {
            String token = instanceIdResult.getToken();
            loginActivityModel.addFirebaseToken(token, getString(R.string.platform));
        });
    }

    private boolean isValid(String email, String password) {
        if(TextUtils.isEmpty(email)) {
            editTextUsernameLayout.setError(getResources().getString(R.string.error_empty_email));
            return false;
        }

        if(TextUtils.isEmpty(password)) {
            editTextPasswordLayout.setError(getResources().getString(R.string.error_empty_password));
            return false;
        }

        if(email.length() < USERNAME_MIN) {
            editTextUsernameLayout.setError(getResources().getString(R.string.error_username_small));
            return false;
        }

        if(email.length() > USERNAME_MAX) {
            editTextUsernameLayout.setError(getResources().getString(R.string.error_username_big));
            return false;
        }

        if(!EditTextUtils.isTextValid(email)) {
            editTextUsernameLayout.setError(getResources().getString(R.string.error_username_incorrect));
            return false;
        }

        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && EditTextUtils.isTextValid(email);
    }
}
