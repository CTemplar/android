package com.ctemplar.app.fdroid.login;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.DialogState;
import com.ctemplar.app.fdroid.LoginActivityActions;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityLoginBinding;
import com.ctemplar.app.fdroid.main.MainActivity;
import timber.log.Timber;

public class LoginActivity extends BaseActivity {
    private LoginActivityViewModel loginViewModel;

    private ActivityLoginBinding binding;

    protected BaseFragment getStartFragment() {
        return new SignInFragment();
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(binding.contentFrame.getId(), getStartFragment());
            ft.commit();
        }
        loginViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);
        loginViewModel.getActionStatus().observe(this, this::handleActions);
        loginViewModel.getDialogState().observe(this, this::handleDialogState);
    }

    public void blockUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void unlockUI() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }

    private void handleDialogState(DialogState state) {
        if (state != null) {
            switch (state) {
                case SHOW_PROGRESS_DIALOG:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.progressBackground.setVisibility(View.VISIBLE);
                    blockUI();
                    break;
                case HIDE_PROGRESS_DIALOG:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.progressBackground.setVisibility(View.GONE);
                    unlockUI();
                    break;
            }
        }
    }

    private void handleActions(LoginActivityActions action) {
        if (action != null) {
            switch (action) {
                case CHANGE_FRAGMENT_SIGN_IN:
                    break;
                case CHANGE_FRAGMENT_FORGOT_USERNAME:
                    pushFragment(new ForgotUsernameFragment());
                    break;
                case CHANGE_FRAGMENT_FORGOT_PASSWORD:
                    pushFragment(new ForgotPasswordFragment());
                    break;
                case CHANGE_FRAGMENT_CONFIRM_PASWORD:
                    pushFragment(new ConfirmResetPasswordFragment());
                    break;
                case CHANGE_FRAGMENT_RESET_CODE:
                    pushFragment(new ResetCodeFragment());
                    break;
                case CHANGE_FRAGMENT_NEW_PASSWORD:
                    pushFragment(new NewPasswordFragment());
                    break;
                case CHANGE_FRAGMENT_CREATE_ACCOUNT:
                    pushFragment(new SignUpFragment());
                    break;
                case CHANGE_ACTIVITY_MAIN:
                    goToMainActivity();
                    break;

            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        Timber.d("onSaveInstanceState");
        if (isPortrait2Landscape()) {
            removeFragments();
        }
        super.onSaveInstanceState(outState);
    }

    private void removeFragments() {
        Timber.d("removeFragments");
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentById(binding.contentFrame.getId());
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }
        supportFragmentManager
                .beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();
    }

    private boolean isPortrait2Landscape() {
        return isDevicePortrait() && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private boolean isDevicePortrait() {
        return (findViewById(binding.contentFrame.getId()) != null);
    }

    protected BaseFragment getCurrentFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(binding.contentFrame.getId());
    }

    protected void resetFragment() {
        FragmentManager manager = getSupportFragmentManager();

        //noinspection StatementWithEmptyBody
        while (manager.popBackStackImmediate()) {
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(binding.contentFrame.getId(), getStartFragment());
        ft.commit();
    }


    @Override
    protected boolean handleBackPress() {
        FragmentManager manager = getSupportFragmentManager();
        if (getCurrentFragment().handleBackPress()) {
            return true;
        }
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
            return true;
        }
        return false;
    }

    protected void pushFragment(BaseFragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(
                R.anim.fragment_enter_from_right,
                R.anim.fragment_exit_to_left,
                R.anim.fragment_enter_from_left,
                R.anim.fragment_exit_to_right
        );
        ft.replace(binding.contentFrame.getId(), fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
