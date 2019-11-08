package mobileapp.ctemplar.com.ctemplarapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.FrameLayout;

import butterknife.BindView;

public abstract class BaseFragmentActivity extends BaseActivity {

    @NonNull
    protected abstract BaseFragment getStartFragment();

    @BindView(R.id.content_frame)
    FrameLayout mContentFrame;

    // @State
    boolean mHasAddedStartFragment;

    protected int getLayoutId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!mHasAddedStartFragment) {
            mHasAddedStartFragment = true;
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(mContentFrame.getId(), getStartFragment());
            ft.commit();
        }
    }

    protected BaseFragment getCurrentFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(mContentFrame.getId());
    }

    protected void resetFragment() {
        FragmentManager manager = getSupportFragmentManager();

        //noinspection StatementWithEmptyBody
        while (manager.popBackStackImmediate()) {
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(mContentFrame.getId(), getStartFragment());
        ft.commit();
    }

    @Override
    protected boolean handleBackPress() {
        FragmentManager manager = getSupportFragmentManager();
        if (getCurrentFragment().handleBackPress()) {
            return true;
        }
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
            return true;
        }
        return false;
    }

    protected void pushFragment(BaseFragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right);
        ft.replace(mContentFrame.getId(), fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
