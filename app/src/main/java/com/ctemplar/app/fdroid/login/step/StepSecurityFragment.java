package com.ctemplar.app.fdroid.login.step;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.LoginActivityActions;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.login.LoginActivityViewModel;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.CaptchaResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaVerifyResponse;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.HtmlUtils;
import timber.log.Timber;

public class StepSecurityFragment extends BaseFragment {
    @BindView(R.id.fragment_step_security_captcha_img)
    ImageView captchaImageView;

    @BindView(R.id.fragment_step_security_check_text)
    TextView txtCheckHint;

    @BindView(R.id.fragment_step_security_captcha_input)
    TextInputEditText captchaEditText;

    @BindView(R.id.fragment_step_security_captcha_input_layout)
    TextInputLayout captchaInputLayout;

    @BindView(R.id.fragment_step_security_captcha_layout)
    ConstraintLayout captchaLayout;

    @BindView(R.id.fragment_step_security_captcha_checked)
    ImageView captchaChecked;

    @BindView(R.id.fragment_step_security_check)
    AppCompatCheckBox termsCheckBox;

    @BindView(R.id.fragment_step_security_next_btn)
    Button nextButton;

    private StepRegistrationViewModel viewModel;
    private LoginActivityViewModel loginActivityModel;

    private boolean captchaState;
    private String captchaKey;
    private String captchaValue;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_security;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("FragmentActivity is null");
            return;
        }
        ViewModelStoreOwner viewModelStoreOwner = getParentFragment();
        if (viewModelStoreOwner == null) {
            Timber.w("getParentFragment is null");
            viewModelStoreOwner = activity;
        }

        loginActivityModel = new ViewModelProvider(activity).get(LoginActivityViewModel.class);
        viewModel = new ViewModelProvider(viewModelStoreOwner).get(StepRegistrationViewModel.class);
        viewModel.getResponseStatus().observe(getViewLifecycleOwner(), this::handleResponseStatus);
        viewModel.getCaptchaResponse().observe(getViewLifecycleOwner(), this::handleCaptchaResponse);
        viewModel.getCaptchaVerifyResponse().observe(getViewLifecycleOwner(), this::handleCaptchaVerifyResponse);

        refreshCaptcha();
        setListeners();
    }

    @OnClick(R.id.fragment_step_security_next_btn)
    public void onClickNext() {
        if (captchaState) {
            loginActivityModel.showProgressDialog();
            viewModel.setCaptcha(captchaKey, captchaValue);
            viewModel.signUp();
        } else {
            captchaInputLayout.setError(getResources().getString(R.string.txt_enter_valid_captcha));
        }
    }

    @OnClick(R.id.fragment_step_security_captcha_refresh_img)
    public void onClickCaptchaRefresh() {
        refreshCaptcha();
    }

    @OnClick(R.id.fragment_step_security_captcha_confirm)
    public void onClickCaptchaConfirm() {
        captchaValue = EditTextUtils.getText(captchaEditText);
        viewModel.captchaVerify(captchaKey, captchaValue);
    }

    private void refreshCaptcha() {
        viewModel.getCaptcha();
    }

    private void handleCaptchaVerifyResponse(CaptchaVerifyResponse response) {
        if (response != null) {
            captchaState = response.getStatus();
            changeCaptchaState(captchaState);
            nextButton.setEnabled(captchaState && termsCheckBox.isChecked());
            if (!captchaState) {
                captchaInputLayout.setError(getString(R.string.txt_enter_valid_captcha));
            }
        }
    }

    private void handleCaptchaResponse(CaptchaResponse response) {
        if (response != null) {
            captchaKey = response.getCaptchaKey();
            String captchaImageUrl = response.getCaptchaImageUrl();
            Picasso.get()
                    .load(captchaImageUrl)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(captchaImageView);
        }
    }

    private void changeCaptchaState(boolean state) {
        captchaChecked.setVisibility(state ? View.VISIBLE : View.GONE);
        captchaLayout.setVisibility(state ? View.GONE : View.VISIBLE);
        if (!state) {
            refreshCaptcha();
            captchaEditText.setText("");
        }
    }

    private void setListeners() {
        txtCheckHint.setText(HtmlUtils.fromHtml(getResources().getString(R.string.title_step_email_check_hint)));
        txtCheckHint.setMovementMethod(LinkMovementMethod.getInstance());
        txtCheckHint.setLinkTextColor(getResources().getColor(R.color.colorLinkBlue));

        captchaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                captchaInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> nextButton.setEnabled(isChecked && captchaState));
    }

    private void handleResponseStatus(@Nullable ResponseStatus status) {
        if (status != null) {
            loginActivityModel.hideProgressDialog();
            switch (status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_server), Toast.LENGTH_LONG).show();
                    changeCaptchaState(false);
                    break;
                case RESPONSE_NEXT_STEP_EMAIL:
                    loginActivityModel.changeAction(LoginActivityActions.CHANGE_ACTIVITY_MAIN);
                    break;
            }
        }
    }
}
