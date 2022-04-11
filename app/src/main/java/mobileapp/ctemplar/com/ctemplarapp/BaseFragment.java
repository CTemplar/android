package mobileapp.ctemplar.com.ctemplarapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    @Deprecated
    @LayoutRes
    protected int getLayoutId() {
        return 0;
    }

    private Unbinder mUnbinder;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId == 0) {
            throw new RuntimeException("Use bindings instead of ButterKnife (in " + this.getClass().getSimpleName() + " Fragment):");
        }
        View view = inflater.inflate(layoutId, container, false);
        view.setClickable(true);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroyView();
    }

    public boolean handleBackPress() {
        return false;
    }
}
