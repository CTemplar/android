package com.ctemplar.app.fdroid.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.lifecycle.ViewModelProvider;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.login.LoginActivity;
import com.ctemplar.app.fdroid.main.MainActivity;

public class SplashActivity extends BaseActivity {
    private SplashActivityModel viewModel;
    private Handler handler = new Handler();

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(viewModel.getToken())) {
                startSignInActivity();
            } else {
                startMainActivity();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SplashActivityModel.class);

        skipDelay();
    }

    private void loadData() {
        // data will be loaded here before starting new activity
        handler.postDelayed(run, 2000);
    }

    private void skipDelay() {
        handler.removeCallbacks(run);
        handler.postDelayed(run, 300);
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Intent startIntent = getIntent();
        if (startIntent != null) {
            intent.putExtras(startIntent);
            intent.setAction(startIntent.getAction());
            intent.setData(startIntent.getData());
        }
        startActivity(intent);
    }
}
