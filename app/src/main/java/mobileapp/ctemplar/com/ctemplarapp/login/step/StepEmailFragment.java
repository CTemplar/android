package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class StepEmailFragment extends BaseFragment {

    @BindView(R.id.fragment_step_email_input_layout)
    TextInputLayout editEmailLayout;

    @BindView(R.id.fragment_step_email_input)
    TextInputEditText editEmail;

    @BindView(R.id.fragment_step_email_next_btn)
    Button btnNext;

    @BindView(R.id.fragment_step_email_check)
    AppCompatCheckBox checkBox;

    @BindView(R.id.fragment_step_email_check_text)
    TextView txtCheckHint;

    private StepRegistrationViewModel viewModel;
    private LoginActivityViewModel loginActivityModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_email;
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
        viewModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                handleResponseStatus(responseStatus);
            }
        });

        loginActivityModel = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);

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

    @OnClick(R.id.fragment_step_email_next_btn)
    public void onClickNext() {

        handleErrorEmail(editEmail.getText().toString());

        if(EditTextUtils.isEmailValid(editEmail.getText().toString())) {
            loginActivityModel.showProgressDialog();
            viewModel.setRecoveryEmail(editEmail.getText().toString());
            viewModel.signUp();
        }
    }

    public void setListeners() {
        txtCheckHint.setText(Html.fromHtml(getResources().getString(R.string.title_step_email_check_hint)));
        txtCheckHint.setMovementMethod(LinkMovementMethod.getInstance());
        txtCheckHint.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));

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

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnNext.setEnabled(isChecked);
            }
        });
    }

    private void handleErrorEmail(String email) {
        if(!EditTextUtils.isEmailValid(email)) {
            editEmailLayout.setError(getResources().getString(R.string.error_invalid_email));
            return;
        }
    }

    public void handleResponseStatus(ResponseStatus status) {
        if(status != null) {
            loginActivityModel.hideProgressDialog();
            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_server), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_NEXT_STEP_EMAIL:
                    loginActivityModel.changeAction(LoginActivityActions.CHANGE_ACTIVITY_MAIN);
                    break;
            }
        }
    }

}