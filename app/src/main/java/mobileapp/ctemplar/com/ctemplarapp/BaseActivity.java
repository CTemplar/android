package mobileapp.ctemplar.com.ctemplarapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.BaseContextWrapperDelegate;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import mobileapp.ctemplar.com.ctemplarapp.utils.LocaleUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;

public abstract class BaseActivity extends AppCompatActivity {
    @Deprecated
    protected int getLayoutId() {
        return 0;
    }

    private Unbinder mUnbinder;
    private AppCompatDelegate baseContextWrappingDelegate;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return baseContextWrappingDelegate != null ?
                baseContextWrappingDelegate :
                (baseContextWrappingDelegate = new BaseContextWrapperDelegate(super.getDelegate()));
    }

    @Override
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        Context context = super.createConfigurationContext(overrideConfiguration);
        return LocaleUtils.getContextWrapper(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        if (savedInstanceState == null || mUnbinder == null) {
            int layoutId = getLayoutId();
            if (layoutId != 0) {
                setContentView(layoutId);
                mUnbinder = ButterKnife.bind(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    protected boolean handleBackPress() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!handleBackPress()) {
            finish();
        }
    }
}
