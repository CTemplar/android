package com.ctemplar.app.fdroid.login.step;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class StepPasswordFragment extends BaseFragment {

    private StepRegistrationViewModel viewModel;

    @BindView(R.id.fragment_step_password_choose_input)
    TextInputEditText editChoose;

    @BindView(R.id.fragment_step_password_confirm_input)
    TextInputEditText editConfirm;

    @BindView(R.id.fragment_step_password_confirm_input_layout)
    TextInputLayout editConfirmLayout;

    @BindView(R.id.fragment_step_password_recovery_input_layout)
    TextInputLayout editRecoveryEmailLayout;

    @BindView(R.id.fragment_step_password_recovery_input)
    TextInputEditText editRecoveryEmail;

    @BindView(R.id.fragment_step_password_recovery_check)
    AppCompatCheckBox recoveryEmailCheckBox;

    @BindView(R.id.fragment_step_password_recovery_check_text)
    TextView recoveryEmailCheckText;

    @BindView(R.id.fragment_step_password_recovery_layout)
    ConstraintLayout recoveryLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_password;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
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
        if (editChoose.length() < 7) {
            editConfirmLayout.setError(getString(R.string.error_password_small));
            return;
        }
        if (editChoose.length() > 64) {
            editConfirmLayout.setError(getString(R.string.error_password_big));
            return;
        }
        if(!TextUtils.equals(EditTextUtils.getText(editConfirm), EditTextUtils.getText(editChoose))) {
            editConfirmLayout.setError(getString(R.string.error_password_not_match));
            return;
        }
        if (!recoveryEmailCheckBox.isChecked()) {
            if (EditTextUtils.isEmailValid(EditTextUtils.getText(editRecoveryEmail))) {
                viewModel.setRecoveryEmail(EditTextUtils.getText(editRecoveryEmail));
            } else {
                editRecoveryEmailLayout.setError(getString(R.string.error_invalid_email));
                return;
            }
        }

        viewModel.setPassword(EditTextUtils.getText(editChoose));
        viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
    }

    private void setListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editConfirmLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editConfirm.addTextChangedListener(watcher);
        editChoose.addTextChangedListener(watcher);

        final Spanned shortHint = EditTextUtils.fromHtml(getString(R.string.hint_step_email_recovery_short));
        final Spanned longHint = EditTextUtils.fromHtml(getString(R.string.hint_step_email_recovery_long));
        recoveryEmailCheckText.setText(shortHint, TextView.BufferType.SPANNABLE);
        recoveryEmailCheckText.setOnClickListener(v -> {
            recoveryEmailCheckText.setText(
                    v.isSelected() ? shortHint : longHint,
                    TextView.BufferType.SPANNABLE
            );
            recoveryEmailCheckText.setSelected(!v.isSelected());
        });
        recoveryEmailCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> showRecoveryLayout(!isChecked));
        editRecoveryEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editRecoveryEmailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showRecoveryLayout(boolean state) {
        if (state) {
            recoveryLayout.setVisibility(View.VISIBLE);
        } else {
            recoveryLayout.setVisibility(View.GONE);
        }
    }
}
