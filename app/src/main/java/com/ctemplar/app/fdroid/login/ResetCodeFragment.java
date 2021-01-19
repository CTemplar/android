package com.ctemplar.app.fdroid.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.LoginActivityActions;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.HtmlUtils;
import timber.log.Timber;

public class ResetCodeFragment extends BaseFragment {
    @BindView(R.id.fragment_reset_code_hint)
    TextView txtHint;

    @BindView(R.id.fragment_reset_code_input_layout)
    TextInputLayout editCodeLayout;

    @BindView(R.id.fragment_reset_code_input)
    TextInputEditText editCode;

    @BindView(R.id.fragment_reset_code_btn)
    Button btnCodeNext;

    private LoginActivityViewModel loginActivityModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_code;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("FragmentActivity is null");
            return;
        }

        loginActivityModel = new ViewModelProvider(activity).get(LoginActivityViewModel.class);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
    }

    @OnClick(R.id.fragment_reset_code_btn)
    public void onClickConfirmCode() {
        if(!TextUtils.isEmpty(EditTextUtils.getText(editCode))) {
            loginActivityModel.getRecoverPasswordRequest().setCode(EditTextUtils.getText(editCode));
            loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_NEW_PASSWORD);
        }
    }

    @OnClick(R.id.fragment_reset_code_back)
    public void onClickBack() {
        loginActivityModel.resetResponseStatus();
        getActivity().onBackPressed();
    }

    private void setListeners() {
        txtHint.setText(HtmlUtils.fromHtml(getString(R.string.title_reset_code_hint,
                loginActivityModel.getRecoverPasswordRequest().getRecoveryEmail())));
        txtHint.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));
        txtHint.setMovementMethod(LinkMovementMethod.getInstance());
        txtHint.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);

        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(TextUtils.isEmpty(charSequence)) {
                    editCodeLayout.setError(getString(R.string.error_empty_password));
                    btnCodeNext.setEnabled(false);
                } else {
                    editCodeLayout.setError(null);
                    btnCodeNext.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
