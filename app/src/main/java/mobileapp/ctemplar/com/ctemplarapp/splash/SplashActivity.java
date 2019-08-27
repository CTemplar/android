package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;

public class SplashActivity extends BaseActivity {

    private SplashActivityModel viewModel;
    private Handler handler = new Handler();

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if(TextUtils.isEmpty(viewModel.getToken())) {
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

        viewModel = ViewModelProviders.of(this).get(SplashActivityModel.class);

        if (!TextUtils.isEmpty(viewModel.getToken())) {
            viewModel.getRefreshTokenResponse()
                    .observe(this, new Observer<String>() {
                        @Override
                        public void onChanged(@Nullable String s) {
                            if (checkFirebaseToken()) {
                                handler.removeCallbacks(run);
                                run.run();
                            }
                        }
                    });
            viewModel.refreshToken();
        }
        loadData();
    }

    private boolean checkFirebaseToken() {
        viewModel.getAddFirebaseTokenResponse().observe(this, new Observer<AddFirebaseTokenResponse>() {
            @Override
            public void onChanged(@Nullable AddFirebaseTokenResponse response) {
                if (response != null) {
                    String newToken = response.getToken();
                    viewModel.saveFirebaseToken(newToken);
                    handler.removeCallbacks(run);
                    run.run();
                }
            }
        });

        final boolean[] skipUpdate = {true};
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                String storeToken = viewModel.getFirebaseToken();
                if (!token.equals(storeToken)) {
                    if (!storeToken.isEmpty()) {
                        viewModel.deleteFirebaseToken(storeToken);
                    }
                    viewModel.addFirebaseToken(token, "android");
                    skipUpdate[0] = false;
                }
            }
        });
        return skipUpdate[0];
    }

    private void loadData() {
        // data will be loaded here before starting new activity
        handler.postDelayed(run, 2000);
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
