package mobileapp.ctemplar.com.ctemplarapp.login.step;

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

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;

public class StepPasswordFragment extends BaseFragment {

    private StepRegistrationViewModel viewModel;

    @BindView(R.id.fragment_step_password_choose_input)
    TextInputEditText editChoose;

    @BindView(R.id.fragment_step_password_confirm_input)
    TextInputEditText editConfirm;

    @BindView(R.id.fragment_step_password_confirm_input_layout)
    TextInputLayout editConfirmLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_password;
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

    @OnClick({R.id.fragment_step_password_next_btn})
    public void onClickNext() {

        // check for matching
        if(!TextUtils.equals(editConfirm.getText().toString(), editChoose.getText().toString())) {
            editConfirmLayout.setError(getResources().getString(R.string.error_password_not_match));
            return;
        }

        // validate passwords
        if(!TextUtils.isEmpty(editChoose.getText().toString()) &&
                !TextUtils.isEmpty(editConfirm.getText().toString()) &&
                TextUtils.equals(editChoose.getText().toString(), editConfirm.getText().toString())) {

            viewModel.setPassword(editChoose.getText().toString());
            viewModel.changeAction(StepRegistrationActions.ACTION_NEXT);
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