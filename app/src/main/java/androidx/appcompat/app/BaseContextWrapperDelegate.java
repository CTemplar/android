package androidx.appcompat.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.ctemplar.app.fdroid.utils.LocaleUtils;

public class BaseContextWrapperDelegate extends AppCompatDelegate {
    private final AppCompatDelegate superDelegate;

    public BaseContextWrapperDelegate(AppCompatDelegate superDelegate) {
        this.superDelegate = superDelegate;
    }

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        return superDelegate.getSupportActionBar();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        superDelegate.setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return superDelegate.getMenuInflater();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        superDelegate.onCreate(savedInstanceState);
        removeActivityDelegate(superDelegate);
        addActiveDelegate(this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        superDelegate.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        superDelegate.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        superDelegate.onStart();
    }

    @Override
    public void onStop() {
        superDelegate.onStop();
    }

    @Override
    public void onPostResume() {
        superDelegate.onPostResume();
    }

    @Override
    public void setTheme(int themeResId) {
        superDelegate.setTheme(themeResId);
    }

    @Nullable
    @Override
    public <T extends View> T findViewById(int id) {
        return superDelegate.findViewById(id);
    }

    @Override
    public void setContentView(View v) {
        superDelegate.setContentView(v);
    }

    @Override
    public void setContentView(int resId) {
        superDelegate.setContentView(resId);
    }

    @Override
    public void setContentView(View v, ViewGroup.LayoutParams lp) {
        superDelegate.setContentView(v, lp);
    }

    @Override
    public void addContentView(View v, ViewGroup.LayoutParams lp) {
        superDelegate.addContentView(v, lp);
    }

    @NonNull
    @Override
    public Context attachBaseContext2(@NonNull Context context) {
        return wrap(superDelegate.attachBaseContext2(super.attachBaseContext2(context)));
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        superDelegate.setTitle(title);
    }

    @Override
    public void invalidateOptionsMenu() {
        superDelegate.invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        superDelegate.onDestroy();
        removeActivityDelegate(this);
    }

    @Nullable
    @Override
    public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return superDelegate.getDrawerToggleDelegate();
    }

    @Override
    public boolean requestWindowFeature(int featureId) {
        return superDelegate.requestWindowFeature(featureId);
    }

    @Override
    public boolean hasWindowFeature(int featureId) {
        return superDelegate.hasWindowFeature(featureId);
    }

    @Nullable
    @Override
    public ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {
        return superDelegate.startSupportActionMode(callback);
    }

    @Override
    public void installViewFactory() {
        superDelegate.installViewFactory();
    }

    @Override
    public View createView(@Nullable View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return superDelegate.createView(parent, name, context, attrs);
    }

    @Override
    public void setHandleNativeActionModesEnabled(boolean enabled) {
        superDelegate.setHandleNativeActionModesEnabled(enabled);
    }

    @Override
    public boolean isHandleNativeActionModesEnabled() {
        return superDelegate.isHandleNativeActionModesEnabled();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        superDelegate.onSaveInstanceState(outState);
    }

    @Override
    public boolean applyDayNight() {
        return superDelegate.applyDayNight();
    }

    @RequiresApi(17)
    @Override
    public void setLocalNightMode(int mode) {
        superDelegate.setLocalNightMode(mode);
    }

    @Override
    public int getLocalNightMode() {
        return superDelegate.getLocalNightMode();
    }

    private Context wrap(Context context) {
        return LocaleUtils.getContextWrapper(context);
    }
}
