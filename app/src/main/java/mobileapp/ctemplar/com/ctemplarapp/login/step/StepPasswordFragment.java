package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

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

    @BindView(R.id.fragment_step_password_hint)
    TextView passwordHint;

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
        passwordHint.setText(EditTextUtils.fromHtml(getString(R.string.title_further_question_hint)));
        passwordHint.setMovementMethod(LinkMovementMethod.getInstance());
        passwordHint.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
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
