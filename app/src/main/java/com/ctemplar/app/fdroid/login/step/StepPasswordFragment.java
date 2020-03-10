package com.ctemplar.app.fdroid.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class StepPasswordFragment extends BaseFragment {

    private StepRegistrationViewModel viewModel;

    @BindInt(R.integer.restriction_password_min)
    int PASSWORD_MIN;

    @BindInt(R.integer.restriction_password_max)
    int PASSWORD_MAX;

    @BindView(R.id.fragment_step_password_choose_input)
    TextInputEditText passwordEditText;

    @BindView(R.id.fragment_step_password_choose_input_layout)
    TextInputLayout passwordInputLayout;

    @BindView(R.id.fragment_step_password_confirm_input)
    TextInputEditText passwordConfirmEditText;

    @BindView(R.id.fragment_step_password_confirm_input_layout)
    TextInputLayout passwordConfirmInputLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_password;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(StepRegistrationViewModel.class);
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

    @OnClick({R.id.fragment_step_password_next_btn})
    public void onClickNext() {
        if (passwordEditText.length() < PASSWORD_MIN) {
            passwordInputLayout.setError(getString(R.string.error_password_small));
            return;
        }
        if (passwordEditText.length() > PASSWORD_MAX) {
            passwordInputLayout.setError(getString(R.string.error_password_big));
            return;
        }
        if(!TextUtils.equals(
                EditTextUtils.getText(passwordConfirmEditText),
                EditTextUtils.getText(passwordEditText)
        )) {
            passwordConfirmInputLayout.setError(getString(R.string.error_password_not_match));
            return;
        }

        viewModel.setPassword(EditTextUtils.getText(passwordEditText));
        viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
    }

    private void setListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInputLayout.setError(null);
                passwordConfirmInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        passwordEditText.addTextChangedListener(watcher);
        passwordConfirmEditText.addTextChangedListener(watcher);
    }
}
