package mobileapp.ctemplar.com.ctemplarapp.login;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

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

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginActivityModel = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    @OnClick(R.id.fragment_reset_code_btn)
    public void onClickConfirmCode() {
        if(!TextUtils.isEmpty(EditTextUtils.getText(editCode))) {
            loginActivityModel.getRecoverPasswordRequest().setCode(EditTextUtils.getText(editCode));
            loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_NEW_PASSWORD);
        }
    }

    @OnClick(R.id.fragment_reset_code_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    private void setListeners() {
        txtHint.setText(Html.fromHtml(getResources().getString(R.string.title_reset_code_hint, loginActivityModel.getRecoverPasswordRequest().getEmail())));
        txtHint.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));
        txtHint.setMovementMethod(LinkMovementMethod.getInstance());
        txtHint.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);

        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s.toString())) {
                    editCodeLayout.setError(getResources().getString(R.string.error_empty_password));
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
