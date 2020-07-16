package com.ctemplar.app.fdroid.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.LoginActivityActions;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import timber.log.Timber;

public class ForgotPasswordFragment extends BaseFragment {
    @BindView(R.id.fragment_forgot_password_email_input)
    TextInputEditText editEmail;

    @BindView(R.id.fragment_forgot_password_email_input_layout)
    TextInputLayout editEmailLayout;

    @BindView(R.id.fragment_forgot_password_uername_input)
    TextInputEditText editUsername;

    @BindView(R.id.fragment_forgot_password_username_input_layout)
    TextInputLayout editUsernameLayout;

    @BindInt(R.integer.restriction_username_min)
    int USERNAME_MIN;

    @BindInt(R.integer.restriction_username_max)
    int USERNAME_MAX;

    private LoginActivityViewModel loginActivityModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("FragmentActivity is null");
            return;
        }

        loginActivityModel = new ViewModelProvider(activity).get(LoginActivityViewModel.class);
        setListeners();
    }

    @OnClick(R.id.fragment_forgot_password_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fragment_forgot_password_send_btn)
    public void onClickNext() {
        handleClickError(EditTextUtils.getText(editUsername), EditTextUtils.getText(editEmail));

        if (isValid(EditTextUtils.getText(editUsername), EditTextUtils.getText(editEmail))) {
            loginActivityModel.setRecoveryPassword(EditTextUtils.getText(editUsername), EditTextUtils.getText(editEmail));
            loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_CONFIRM_PASWORD);
        }
    }

    @OnClick(R.id.fragment_forgot_password_forgot_username)
    public void onClickForgotUsername() {
        loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_FORGOT_USERNAME);
    }

    public void setListeners() {
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editEmailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editUsernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean isValid(String username, String email) {
        return EditTextUtils.isTextLength(username, USERNAME_MIN, USERNAME_MAX) &&
                EditTextUtils.isUsernameValid(username) && EditTextUtils.isEmailValid(email);
    }

    private void handleClickError(String username, String email) {
        if (username.length() < USERNAME_MIN) {
            editUsernameLayout.setError(getString(R.string.error_username_small));
            return;
        }
        if (username.length() > USERNAME_MAX) {
            editUsernameLayout.setError(getString(R.string.error_username_big));
            return;
        }
        if (!EditTextUtils.isUsernameValid(username)) {
            editUsernameLayout.setError(getString(R.string.error_username_incorrect));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editEmailLayout.setError(getString(R.string.error_empty_email));
            return;
        }
        if (!EditTextUtils.isEmailValid(email)) {
            editEmailLayout.setError(getString(R.string.error_invalid_email));
        }
    }
}
