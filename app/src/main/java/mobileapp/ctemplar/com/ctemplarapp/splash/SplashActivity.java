package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {
    private SplashActivityModel viewModel;
    private final Handler handler = new Handler();

    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            if (viewModel.isAuthorized()) {
                startMainActivity();
            } else {
                startSignInActivity();
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

        viewModel.getAddFirebaseTokenResponse().observe(this, response -> skipDelay());
        updateFirebaseToken();

        // data will be loaded here before starting new activity
        handler.postDelayed(run, 2000);
    }

    private void updateFirebaseToken() {
        if (!viewModel.isAuthorized()) {
            Timber.w("updateFirebaseToken skip because user is not authorized");
            skipDelay();
            return;
        }

        String storedToken = viewModel.getFirebaseToken();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (token.equals(storedToken)) {
                skipDelay();
                return;
            }
            if (EditTextUtils.isNotEmpty(storedToken)) {
                viewModel.deleteFirebaseToken(storedToken);
            }
            viewModel.addFirebaseToken(token, MainActivityViewModel.ANDROID);
        });
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
