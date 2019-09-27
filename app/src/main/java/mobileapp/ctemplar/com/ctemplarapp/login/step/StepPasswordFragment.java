package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(StepRegistrationViewModel.class);
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
        if (editChoose.length() < 8) {
            editConfirmLayout.setError(getString(R.string.error_password_small));
            return;
        }
        if (editChoose.length() > 64) {
            editConfirmLayout.setError(getString(R.string.error_password_big));
            return;
        }
        if(!TextUtils.equals(editConfirm.getText().toString(), editChoose.getText().toString())) {
            editConfirmLayout.setError(getString(R.string.error_password_not_match));
            return;
        }
        if (!recoveryEmailCheckBox.isChecked()) {
            if (EditTextUtils.isEmailValid(editRecoveryEmail.getText().toString())) {
                viewModel.setRecoveryEmail(editRecoveryEmail.getText().toString());
            } else {
                editRecoveryEmailLayout.setError(getString(R.string.error_invalid_email));
                return;
            }
        }

        viewModel.setPassword(editChoose.getText().toString());
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

        final Spanned shortHint = Html.fromHtml(getString(R.string.hint_step_email_recovery_short));
        final Spanned longHint = Html.fromHtml(getString(R.string.hint_step_email_recovery_long));
        recoveryEmailCheckText.setText(shortHint, TextView.BufferType.SPANNABLE);
        recoveryEmailCheckText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryEmailCheckText.setText(
                        v.isSelected() ? shortHint : longHint,
                        TextView.BufferType.SPANNABLE
                );
                recoveryEmailCheckText.setSelected(!v.isSelected());
            }
        });
        recoveryEmailCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showRecoveryLayout(!isChecked);
            }
        });
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
