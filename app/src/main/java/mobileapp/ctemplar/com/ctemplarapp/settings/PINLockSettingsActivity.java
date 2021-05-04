package mobileapp.ctemplar.com.ctemplarapp.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.AutoLockTime;
import mobileapp.ctemplar.com.ctemplarapp.splash.PINLockActivity;

public class PINLockSettingsActivity extends BaseActivity {
    private static final int SET_PIN_LOCK_REQUEST_CODE = 1;
    private static final int CHANGE_PIN_LOCK_REQUEST_CODE = 2;
    private static final int UNLOCK_REQUEST_CODE = 3;

    @BindView(R.id.activity_settings_pin_lock_enable_switch)
    SwitchCompat pinLockSwitch;

    @BindView(R.id.activity_settings_pin_lock_adjust_layout)
    LinearLayout pinAdjustingLayout;

    @BindView(R.id.activity_settings_pin_lock_change_pin_button)
    AppCompatButton changePINButton;

    @BindView(R.id.activity_settings_pin_lock_timer_spinner)
    Spinner autoLockSpinner;

    private UserStore userStore;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_pin_lock;
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

        userStore = CTemplarApp.getUserStore();

        changePINButton.setOnClickListener(v -> startChangePINLockActivity());
        boolean pinEnabled = userStore.isPINLockEnabled();
        pinLockSwitch.setChecked(pinEnabled);
        showPINAdjusting(pinEnabled);
        pinLockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showPINAdjusting(isChecked);
            if (isChecked) {
                startSetPINLockActivity();
            } else {
                userStore.disablePINLock();
            }
        });
        if (pinEnabled) {
            PINLockActivity.requestUnlockRequest(this, UNLOCK_REQUEST_CODE);
        }
        setListeners();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SET_PIN_LOCK_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                pinLockSwitch.setChecked(false);
            }
            return;
        } else if (requestCode == CHANGE_PIN_LOCK_REQUEST_CODE) {
            return;
        } else if (requestCode == UNLOCK_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                onBackPressed();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPINAdjusting(boolean state) {
        pinAdjustingLayout.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void startSetPINLockActivity() {
        Intent pinLockIntent = new Intent(getBaseContext(), PINLockChangeActivity.class);
        startActivityForResult(pinLockIntent, SET_PIN_LOCK_REQUEST_CODE);
    }

    private void startChangePINLockActivity() {
        Intent pinLockIntent = new Intent(getBaseContext(), PINLockChangeActivity.class);
        startActivityForResult(pinLockIntent, CHANGE_PIN_LOCK_REQUEST_CODE);
    }

    private void setListeners() {
        String[] autoLockTimeText = getResources().getStringArray(R.array.auto_lock_time);
        int[] autoLockTime = AutoLockTime.timeValues();
        SpinnerAdapter addressAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                autoLockTimeText
        );

        autoLockSpinner.setAdapter(addressAdapter);
        autoLockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userStore.setAutoLockTime(autoLockTime[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int selectedId = AutoLockTime.getId(userStore.getAutoLockTime());
        autoLockSpinner.setSelection(selectedId);
    }
}
