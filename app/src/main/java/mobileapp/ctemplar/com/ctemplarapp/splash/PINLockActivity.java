package mobileapp.ctemplar.com.ctemplarapp.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.view.pinlock.KeypadAdapter;
import mobileapp.ctemplar.com.ctemplarapp.view.pinlock.KeypadView;
import mobileapp.ctemplar.com.ctemplarapp.view.pinlock.PasscodeView;
import timber.log.Timber;

public class PINLockActivity extends BaseActivity {
    private static final String ALLOW_BACK_KEY = "allow_back";

    @BindView(R.id.activity_pin_lock_pass_code_view)
    PasscodeView passcodeView;

    @BindView(R.id.activity_pin_lock_key_pad_view)
    KeypadView keypadView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pin_lock;
    }

    private UserStore userStore;
    private boolean canBackPress = false;

    private KeypadAdapter.KeypadListener mKeypadListener = new KeypadAdapter.KeypadListener() {
        @Override
        public void onComplete(String pinCode) {
            if (userStore.checkPINLock(pinCode)) {
                unlock();
            } else {
                notifyWrongPIN();
                keypadView.resetKeypadView();
            }
        }
        @Override
        public void onPINChanged(int pinLength, String pinCode) {
            //
        }
        @Override
        public void onEmpty() {
            //
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        if (intent != null) {
            canBackPress = intent.getBooleanExtra(ALLOW_BACK_KEY, false);
        }
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            if (canBackPress) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        userStore = CTemplarApp.getUserStore();
        keypadView.attachPasscodeView(passcodeView);
        keypadView.setKeypadListener(mKeypadListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (canBackPress) {
            super.onBackPressed();
        }
    }

    public static void requestUnlockRequest(Activity activity, int requestCode) {
        Intent pinIntent = new Intent(activity, PINLockActivity.class);
        pinIntent.putExtra(ALLOW_BACK_KEY, true);
        activity.startActivityForResult(pinIntent, requestCode);
    }

    private void unlock() {
        Timber.d("unlock");
        CTemplarApp.getInstance().onUnlocked();
        setResult(RESULT_OK);
        finish();
    }

    private void notifyWrongPIN() {
        Timber.d("notifyWrongPin");
        Toast.makeText(this, getString(R.string.invalid_pin), Toast.LENGTH_SHORT).show();
        DateUtils.vibrate(getBaseContext(), 500);
    }
}
