package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.iid.FirebaseInstanceId;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;

public class SplashActivity extends BaseActivity {
    private static final String ANDROID = "android";

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

        viewModel = new ViewModelProvider(this).get(SplashActivityModel.class);

        checkFirebaseToken();
        loadData();
    }

    private void checkFirebaseToken() {
        viewModel.getAddFirebaseTokenResponse().observe(this, response -> {
            if (response != null) {
                String newToken = response.getToken();
                viewModel.saveFirebaseToken(newToken);
                skipDelay();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String instanceToken = instanceIdResult.getToken();
            String storedToken = viewModel.getFirebaseToken();
            String userToken = viewModel.getToken();
            if (TextUtils.isEmpty(userToken) && TextUtils.isEmpty(storedToken) || instanceToken.equals(storedToken)) {
                skipDelay();
            } else {
                if (!TextUtils.isEmpty(storedToken)) {
                    viewModel.deleteFirebaseToken(storedToken);
                }
                viewModel.addFirebaseToken(instanceToken, ANDROID);
            }
        });
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
