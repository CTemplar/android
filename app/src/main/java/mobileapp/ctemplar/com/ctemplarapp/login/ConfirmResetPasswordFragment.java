package mobileapp.ctemplar.com.ctemplarapp.login;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.LoginActivityActions;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;

public class ConfirmResetPasswordFragment extends BaseFragment {

    @BindView(R.id.fragment_confirm_reset_password_hint)
    TextView txtHint;

    private LoginActivityViewModel loginActivityModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_confirm_reset_password;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginActivityModel = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);
        loginActivityModel.getResponseStatus().observe(this, this::handleResponseStatus);
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

    @OnClick(R.id.fragment_confirm_reset_password_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fragment_confirm_reset_password_btn)
    public void onClickConfirm() {
        if(loginActivityModel.getRecoverPasswordRequest() != null) {
            // loginActivityModel.changeAction(LoginActivityActions.SHOW_PROGRESS_DIALOG);
            loginActivityModel.showProgressDialog();
            loginActivityModel.recoverPassword();
        }
    }

    private void setListeners() {
        txtHint.setText(Html.fromHtml(getResources().getString(R.string.title_confirm_reset_password_hint)));
        txtHint.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));
        txtHint.setMovementMethod(LinkMovementMethod.getInstance());
        txtHint.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
    }

    public void handleResponseStatus(ResponseStatus status) {
        if(status != null) {
            // loginActivityModel.changeAction(LoginActivityActions.HIDE_PROGRESS_DIALOG);
            loginActivityModel.hideProgressDialog();

            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_reset_password), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_ERROR_RECOVER_PASS_FAILED:
                    Toast.makeText(getActivity(), getString(R.string.error_recover_password), Toast.LENGTH_LONG).show();
                    break;
                case RESPONSE_NEXT_RECOVER_PASSWORD:
                    loginActivityModel.changeAction(LoginActivityActions.CHANGE_FRAGMENT_RESET_CODE);
                    break;
            }
        }
    }
}
