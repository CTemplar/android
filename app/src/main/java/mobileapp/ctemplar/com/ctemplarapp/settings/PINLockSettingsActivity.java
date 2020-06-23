package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.splash.PINLockActivity;

public class PINLockSettingsActivity extends BaseActivity {

    @BindView(R.id.activity_settings_pin_lock_enable_switch)
    SwitchCompat pinLockSwitch;

    @BindView(R.id.activity_settings_pin_lock_change_pin_button)
    AppCompatButton changePINButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_pin_lock;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        showChangePIN(false);
        changePINButton.setOnClickListener(v -> startPINLockActivity());
        pinLockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showChangePIN(isChecked);
            if (isChecked) {
                startPINLockActivity();
            }
        });
    }

    private void showChangePIN(boolean state) {
        changePINButton.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void startPINLockActivity() {
        Intent pinLockIntent = new Intent(getBaseContext(), PINLockActivity.class);
        startActivity(pinLockIntent);
    }
}
