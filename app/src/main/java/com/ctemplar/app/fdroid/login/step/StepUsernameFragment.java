package com.ctemplar.app.fdroid.login.step;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ctemplar.app.fdroid.login.LoginActivityViewModel;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class StepUsernameFragment extends BaseFragment {
    private LoginActivityViewModel loginActivityModel;
    private StepRegistrationViewModel viewModel;

    private Handler handler = new Handler();
    private Runnable usernameAvailabilityCheck = this::usernameAvailabilityCheck;

    @BindView(R.id.fragment_step_username_available_layout)
    LinearLayout usernameAvailableLayout;

    @BindView(R.id.fragment_step_username_not_available_layout)
    LinearLayout usernameNotAvailableLayout;

    @BindView(R.id.fragment_step_username_checking_layout)
    LinearLayout usernameCheckingLayout;

    @BindView(R.id.fragment_step_username_input)
    TextInputEditText usernameEditText;

    @BindView(R.id.fragment_step_username_input_layout)
    TextInputLayout usernameInputLayout;

    @BindView(R.id.fragment_step_username_invite_code_input)
    TextInputEditText inviteCodeEditText;

    @BindView(R.id.fragment_step_username_invite_code_input_layout)
    TextInputLayout inviteCodeInputLayout;

    @BindView(R.id.fragment_step_username_invite_code_hint)
    TextView inviteCodeHintTextView;

    @BindView(R.id.fragment_step_username_next_btn)
    Button nextButton;

    @BindInt(R.integer.restriction_username_min)
    int USERNAME_MIN;

    @BindInt(R.integer.restriction_username_max)
    int USERNAME_MAX;

    @BindInt(R.integer.typing_delay)
    int TYPING_DELAY;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_username;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginActivityModel = new ViewModelProvider(getActivity()).get(LoginActivityViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(StepRegistrationViewModel.class);
        viewModel.getResponseStatus().observe(getViewLifecycleOwner(), this::handleResponseStatus);
        setListeners();
    }

    @OnClick(R.id.fragment_step_username_next_btn)
    public void onClickNext() {
        String username = EditTextUtils.getText(usernameEditText);
        if (handleErrorUsername(username)) {
            return;
        }
        String inviteCode = EditTextUtils.getText(inviteCodeEditText);
        if (handleErrorInviteCode(inviteCode)) {
            return;
        }
        viewModel.setInviteCode(inviteCode);
        if (TextUtils.equals(viewModel.getUsername(), username)) {
            viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
        } else {
            viewModel.checkUsername(EditTextUtils.getText(usernameEditText), true);
            displayUsernameChecking();
            loginActivityModel.showProgressDialog();
        }
    }

    private boolean handleErrorUsername(String username) {
        if (username.length() < USERNAME_MIN) {
            usernameInputLayout.setError(getString(R.string.error_username_small));
            return true;
        }
        if (username.length() > USERNAME_MAX) {
            usernameInputLayout.setError(getString(R.string.error_username_big));
            return true;
        }
        if (!EditTextUtils.isUsernameValid(username)) {
            usernameInputLayout.setError(getString(R.string.error_username_incorrect));
            return true;
        }
        return false;
    }

    private boolean handleErrorInviteCode(String inviteCode) {
        if (TextUtils.isEmpty(inviteCode)) {
            inviteCodeInputLayout.setError(getString(R.string.error_field_cannot_be_empty));
            return true;
        }
        return false;
    }

    private void usernameAvailabilityCheck() {
        String username = EditTextUtils.getText(usernameEditText);
        if (EditTextUtils.isUsernameValid(username)) {
            viewModel.checkUsername(username);
            displayUsernameChecking();
        } else {
            hideUsernameAvailability();
            usernameInputLayout.setError(getString(R.string.error_username_incorrect));
        }
    }

    public void setListeners() {
        hideUsernameAvailability();
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameInputLayout.setError(null);
                handler.removeCallbacks(usernameAvailabilityCheck);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EditTextUtils.isTextLength(s, USERNAME_MIN, USERNAME_MAX)) {
                    handler.postDelayed(usernameAvailabilityCheck, TYPING_DELAY);
                }
            }
        });
        inviteCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inviteCodeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inviteCodeHintTextView.setText(EditTextUtils.fromHtml(
                getString(R.string.title_step_invitation_code_hint)));
        inviteCodeHintTextView.setMovementMethod(LinkMovementMethod.getInstance());
        inviteCodeHintTextView.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));
    }

    public void handleResponseStatus(ResponseStatus status) {
        if (status != null) {
            loginActivityModel.hideProgressDialog();
            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_server), Toast.LENGTH_LONG).show();
                    hideUsernameAvailability();
                    break;
                case RESPONSE_ERROR_TOO_MANY_REQUESTS:
                    Toast.makeText(getActivity(), getString(R.string.error_too_many_requests), Toast.LENGTH_LONG).show();
                    hideUsernameAvailability();
                    break;
                case RESPONSE_ERROR_USERNAME_EXISTS:
                    usernameIsNotAvailable();
                    break;
                case RESPONSE_COMPLETE:
                    usernameIsAvailable();
                    break;
                case RESPONSE_NEXT_STEP_USERNAME:
                    usernameIsAvailable();
                    viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
                    break;
            }
        }
    }

    private void usernameIsAvailable() {
        usernameAvailableLayout.setVisibility(View.VISIBLE);
        usernameNotAvailableLayout.setVisibility(View.GONE);
        usernameCheckingLayout.setVisibility(View.GONE);
    }

    private void usernameIsNotAvailable() {
        usernameAvailableLayout.setVisibility(View.GONE);
        usernameNotAvailableLayout.setVisibility(View.VISIBLE);
        usernameCheckingLayout.setVisibility(View.GONE);
    }

    private void displayUsernameChecking() {
        usernameAvailableLayout.setVisibility(View.GONE);
        usernameNotAvailableLayout.setVisibility(View.GONE);
        usernameCheckingLayout.setVisibility(View.VISIBLE);
    }

    private void hideUsernameAvailability() {
        usernameAvailableLayout.setVisibility(View.INVISIBLE);
        usernameNotAvailableLayout.setVisibility(View.GONE);
        usernameCheckingLayout.setVisibility(View.GONE);
    }
}
