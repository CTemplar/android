package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.view.pinlock.PasscodeView;
import mobileapp.ctemplar.com.ctemplarapp.view.pinlock.KeypadView;
import mobileapp.ctemplar.com.ctemplarapp.view.pinlock.KeypadAdapter;
import timber.log.Timber;

public class PINLockActivity extends BaseActivity {

    @BindView(R.id.activity_settings_pin_lock_pass_code_view)
    PasscodeView passcodeView;

    @BindView(R.id.activity_settings_pin_lock_key_pad_view)
    KeypadView keypadView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_pin_lock;
    }

    private KeypadAdapter.KeypadListener mKeypadListener = new KeypadAdapter.KeypadListener() {
        @Override
        public void onComplete(String pinCode) {
            Timber.d("PIN complete: " + pinCode);
        }

        @Override
        public void onPINChanged(int pinLength, String pinCode) {
            Timber.d("PIN Length" + pinLength + " " + "pinChanged: " + pinCode);
        }

        @Override
        public void onEmpty() {
            Timber.d("PIN Empty");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        keypadView.attachpasscodeView(passcodeView);
        keypadView.setKeypadListener(mKeypadListener);
    }
}
