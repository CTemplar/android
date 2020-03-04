package com.ctemplar.app.fdroid.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class StepRecoveryFragment extends BaseFragment {

    private StepRegistrationViewModel viewModel;

    @BindView(R.id.fragment_step_recovery_email_input)
    TextInputEditText recoveryEmailEditText;

    @BindView(R.id.fragment_step_recovery_email_input_layout)
    TextInputLayout recoveryEmailInputLayout;

    @BindView(R.id.fragment_step_recovery_checkbox)
    AppCompatCheckBox recoveryEmailCheckBox;

    @BindView(R.id.fragment_step_recovery_check_text)
    TextView recoveryEmailCheckText;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_recovery;
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

    @OnClick({R.id.fragment_step_password_next_btn})
    public void onClickNext() {
        if (!recoveryEmailCheckBox.isChecked()) {
            if (EditTextUtils.isEmailValid(EditTextUtils.getText(recoveryEmailEditText))) {
                viewModel.setRecoveryEmail(EditTextUtils.getText(recoveryEmailEditText));
            } else {
                recoveryEmailInputLayout.setError(getString(R.string.error_invalid_email));
                return;
            }
        }

        viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
    }

    private void setListeners() {
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
        recoveryEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recoveryEmailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recoveryEmailCheckBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> showRecoveryLayout(!isChecked));
    }

    private void showRecoveryLayout(boolean state) {
        recoveryEmailInputLayout.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}
