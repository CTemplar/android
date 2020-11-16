package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.HtmlUtils;
import timber.log.Timber;

public class StepRecoveryFragment extends BaseFragment {
    @BindView(R.id.fragment_step_recovery_email_input)
    TextInputEditText recoveryEmailEditText;

    @BindView(R.id.fragment_step_recovery_email_input_layout)
    TextInputLayout recoveryEmailInputLayout;

    @BindView(R.id.fragment_step_recovery_checkbox)
    AppCompatCheckBox recoveryEmailCheckBox;

    @BindView(R.id.fragment_step_recovery_check_text)
    TextView recoveryEmailCheckText;

    private StepRegistrationViewModel viewModel;
    private LoginActivityViewModel loginActivityModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_recovery;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("FragmentActivity is null");
            return;
        }
        ViewModelStoreOwner viewModelStoreOwner = getParentFragment();
        if (viewModelStoreOwner == null) {
            Timber.w("getParentFragment is null");
            viewModelStoreOwner = activity;
        }

        loginActivityModel = new ViewModelProvider(activity).get(LoginActivityViewModel.class);
        viewModel = new ViewModelProvider(viewModelStoreOwner).get(StepRegistrationViewModel.class);
        viewModel.getResponseStatus().observe(getViewLifecycleOwner(), this::handleResponseStatus);
        viewModel.getResponseError().observe(getViewLifecycleOwner(), this::handleResponseError);
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

        viewModel.signUp();
        loginActivityModel.showProgressDialog();
        viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
    }

    private void setListeners() {
        final Spanned shortHint = HtmlUtils.fromHtml(getString(R.string.hint_step_email_recovery_short));
        final Spanned longHint = HtmlUtils.fromHtml(getString(R.string.hint_step_email_recovery_long));
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

    private void handleResponseStatus(ResponseStatus status) {
        loginActivityModel.hideProgressDialog();
        if (status == ResponseStatus.RESPONSE_NEXT_STEP_EMAIL) {
            loginActivityModel.changeAction(LoginActivityActions.CHANGE_ACTIVITY_MAIN);
        }
    }

    private void handleResponseError(@Nullable String error) {
        if (error != null) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }
}
