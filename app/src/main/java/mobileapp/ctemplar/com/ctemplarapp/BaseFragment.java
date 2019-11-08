package mobileapp.ctemplar.com.ctemplarapp;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    @LayoutRes
    protected abstract int getLayoutId();

    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        view.setClickable(true);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    public boolean handleBackPress() {
        return false;
    }

    private <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public <T> T getListener(Class<T> clazz, Context context) {
        Fragment parentFragment = getParentFragment();
        while (parentFragment != null) {
            T listener = convertInstanceOfObject(parentFragment, clazz);
            if (listener != null) {
                return listener;
            }
            parentFragment = parentFragment.getParentFragment();
        }

        T listener = convertInstanceOfObject(context, clazz);
        if (listener != null) {
            return listener;
        }

        throw new RuntimeException(this.getClass().getSimpleName() + " attached to invalid listener (Activity/ParentFragment). Expecting " + clazz.getSimpleName());
    }

//    protected boolean isEventListener() {
//        return false;
//    }

    @Override
    public void onResume() {
        super.onResume();
//        if (isEventListener()) {
//            sEventBus.register(this);
//        }
    }

    @Override
    public void onPause() {
//        if (isEventListener()) {
//            sEventBus.unregister(this);
//        }
        super.onPause();
    }
}
