package mobileapp.ctemplar.com.ctemplarapp.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepRegistrationActions;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepRegistrationViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class ForgotUsernameFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forgot_username;
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnClick(R.id.fragment_forgot_username_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }
}