package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class StepInvitationFragment extends BaseFragment {

    private StepRegistrationViewModel viewModel;

    @BindView(R.id.fragment_step_invitation_code_input)
    TextInputEditText inviteCodeEditText;

    @BindView(R.id.fragment_step_invitation_code_input_layout)
    TextInputLayout inviteCodeInputLayout;

    @BindView(R.id.fragment_step_invitation_code_hint)
    TextView inviteCodeHintTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_invitation;
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
        if (EditTextUtils.getText(inviteCodeEditText).isEmpty()) {
            inviteCodeInputLayout.setError(getString(R.string.error_field_cannot_be_empty));
            return;
        }
        viewModel.setInviteCode(EditTextUtils.getText(inviteCodeEditText));
        viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
    }

    private void setListeners() {
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
        inviteCodeHintTextView.setText(
                EditTextUtils.fromHtml(getString(R.string.title_step_invitation_code_hint))
        );
        inviteCodeHintTextView.setMovementMethod(LinkMovementMethod.getInstance());
        inviteCodeHintTextView.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));
    }
}
