package com.ctemplar.app.fdroid.login.step;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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

    private StepRegistrationViewModel viewModel;
    private LoginActivityViewModel loginActivityModel;

    @BindView(R.id.fragment_step_username_input)
    TextInputEditText editUsername;

    @BindView(R.id.fragment_step_username_input_layout)
    TextInputLayout editUsernameLayout;

    @BindView(R.id.fragment_step_username_next_btn)
    Button btnNext;

    @BindInt(R.integer.restriction_username_min)
    int USERNAME_MIN;

    @BindInt(R.integer.restriction_username_max)
    int USERNAME_MAX;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_username;
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

        loginActivityModel = new ViewModelProvider(getActivity()).get(LoginActivityViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(StepRegistrationViewModel.class);
        viewModel.getResponseStatus().observe(getViewLifecycleOwner(), this::handleResponseStatus);

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

    @OnClick(R.id.fragment_step_username_next_btn)
    public void onClickNext() {

        handleErrorUsername(EditTextUtils.getText(editUsername));

        if(isValid(EditTextUtils.getText(editUsername))) {
            // viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
            viewModel.checkUsername(EditTextUtils.getText(editUsername));
            loginActivityModel.showProgressDialog();
        }
    }

    public void setListeners() {
        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editUsernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleErrorUsername(String username) {
        if(username.length() < USERNAME_MIN) {
            editUsernameLayout.setError(getResources().getString(R.string.error_username_small));
            return;
        }

        if(username.length() > USERNAME_MAX) {
            editUsernameLayout.setError(getResources().getString(R.string.error_username_big));
            return;
        }

        if(!EditTextUtils.isTextValid(username)) {
            editUsernameLayout.setError(getResources().getString(R.string.error_username_incorrect));
        }
    }

    public void handleResponseStatus(ResponseStatus status) {
        if(status != null) {
            loginActivityModel.hideProgressDialog();
            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_server), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_ERROR_USERNAME_EXISTS:
                    editUsernameLayout.setError(getString(R.string.error_username_exists));
                    break;
                case RESPONSE_NEXT_STEP_USERNAME:
                    viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
                    break;
            }
        }
    }

    public boolean isValid(String username) {
        return username.length() >= USERNAME_MIN && username.length() <= USERNAME_MAX && EditTextUtils.isTextValid(username);
    }
}