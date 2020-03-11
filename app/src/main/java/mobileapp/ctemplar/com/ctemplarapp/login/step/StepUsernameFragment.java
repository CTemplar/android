package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
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
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class StepUsernameFragment extends BaseFragment {

    private LoginActivityViewModel loginActivityModel;
    private StepRegistrationViewModel viewModel;

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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_username;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (handleErrorUsername(EditTextUtils.getText(usernameEditText))) {
            return;
        }
        if (handleErrorInviteCode(EditTextUtils.getText(inviteCodeEditText))) {
            return;
        }
        viewModel.checkUsername(EditTextUtils.getText(usernameEditText));
        viewModel.setInviteCode(EditTextUtils.getText(inviteCodeEditText));
        loginActivityModel.showProgressDialog();
    }

    private boolean handleErrorUsername(String username) {
        if(username.length() < USERNAME_MIN) {
            usernameInputLayout.setError(getResources().getString(R.string.error_username_small));
            return true;
        }
        if(username.length() > USERNAME_MAX) {
            usernameInputLayout.setError(getResources().getString(R.string.error_username_big));
            return true;
        }
        if(!EditTextUtils.isTextValid(username)) {
            usernameInputLayout.setError(getResources().getString(R.string.error_username_incorrect));
            return true;
        }
        return false;
    }

    private boolean handleErrorInviteCode(String inviteCode) {
        if (inviteCode.isEmpty()) {
            inviteCodeInputLayout.setError(getString(R.string.error_field_cannot_be_empty));
            return true;
        }
        return false;
    }

    public void setListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameInputLayout.setError(null);
                inviteCodeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        usernameEditText.addTextChangedListener(textWatcher);
        inviteCodeEditText.addTextChangedListener(textWatcher);

        inviteCodeHintTextView.setText(EditTextUtils.fromHtml(
                getString(R.string.title_step_invitation_code_hint))
        );
        inviteCodeHintTextView.setMovementMethod(LinkMovementMethod.getInstance());
        inviteCodeHintTextView.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));
    }

    public void handleResponseStatus(ResponseStatus status) {
        if(status != null) {
            loginActivityModel.hideProgressDialog();
            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_server), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_ERROR_USERNAME_EXISTS:
                    usernameInputLayout.setError(getString(R.string.error_username_exists));
                    break;
                case RESPONSE_NEXT_STEP_USERNAME:
                    viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
                    break;
            }
        }
    }
}
