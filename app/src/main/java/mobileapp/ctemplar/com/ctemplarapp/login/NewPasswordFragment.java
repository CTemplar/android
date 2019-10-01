package mobileapp.ctemplar.com.ctemplarapp.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;

public class NewPasswordFragment extends BaseFragment {

    private LoginActivityViewModel viewModel;

    @BindView(R.id.fragment_new_password_input)
    TextInputEditText editChoose;

    @BindView(R.id.fragment_new_password_confirm_input)
    TextInputEditText editConfirm;

    @BindView(R.id.fragment_new_password_confirm_input_layout)
    TextInputLayout editConfirmLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_password;
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

        viewModel = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);
        viewModel.getResponseStatus().observe(getActivity(), new Observer<ResponseStatus>() {

            @Override
            public void onChanged(@Nullable ResponseStatus status) {
                handleStatus(status);
            }
        });

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

    @OnClick(R.id.fragment_new_password_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fragment_new_password_next_btn)
    public void onNextClick() {

        if (editChoose.length() < 7) {
            editConfirmLayout.setError(getResources().getString(R.string.error_password_small));
            return;
        }
        if (editChoose.length() > 64) {
            editConfirmLayout.setError(getResources().getString(R.string.error_password_big));
            return;
        }
        if(!TextUtils.equals(editConfirm.getText().toString(), editChoose.getText().toString())) {
            editConfirmLayout.setError(getResources().getString(R.string.error_password_not_match));
            return;
        }

        if(!TextUtils.isEmpty(editChoose.getText().toString()) &&
                !TextUtils.isEmpty(editConfirm.getText().toString()) &&
                TextUtils.equals(editChoose.getText().toString(), editConfirm.getText().toString())) {

            viewModel.getRecoverPasswordRequest().setPassword(editChoose.getText().toString());
            viewModel.showProgressDialog();
            viewModel.resetPassword();
        }
    }

    private void handleStatus(ResponseStatus status) {
        if(status != null) {
            viewModel.hideProgressDialog();

            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_change_password), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_ERROR_CODE_NOT_MATCH:
                    Toast.makeText(getActivity(), getString(R.string.error_authentication_failed), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_NEXT_NEW_PASSWORD:
                    viewModel.changeAction(LoginActivityActions.CHANGE_ACTIVITY_MAIN);
                    break;
                case RESPONSE_NEXT:
                    break;
            }
        }
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
    }
}
