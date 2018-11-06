package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;

public class SplashActivity extends BaseActivity {

    SplashActivityModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(SplashActivityModel.class);

        loadData();
    }

    private void loadData() {
        // data will be loaded here before starting new activity

        Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(viewModel.getToken())) {
                    startSignInActivity();
                } else {
                    startMainActivity();
                }
            }
        };
        handler.postDelayed(run, 3000);
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
