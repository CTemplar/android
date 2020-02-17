package com.ctemplar.app.fdroid.login;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.R;

public class ForgotUsernameFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forgot_username;
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