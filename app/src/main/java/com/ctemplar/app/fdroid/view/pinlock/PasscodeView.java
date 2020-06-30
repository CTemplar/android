package com.ctemplar.app.fdroid.view.pinlock;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.ThemeUtils;

public class PasscodeView extends LinearLayout {
    private final int dotDiameter;
    private final int dotSpacing;

    private int previousLength;

    public PasscodeView(Context context) {
        this(context, null);
    }

    public PasscodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasscodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dotDiameter = (int) ThemeUtils.getDimension(getContext(), R.dimen.passcode_dot_diameter);
        dotSpacing = (int) ThemeUtils.getDimension(getContext(), R.dimen.passcode_dot_spacing);
        initView();
    }

    private void initView() {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);
        setLayoutTransition(new LayoutTransition());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = dotDiameter;
        requestLayout();
    }

    public void updatepasscode(int length) {
        if (length > 0) {
            if (length > previousLength) {
                View view = new View(getContext());
                fillDot(view);
                LayoutParams params = new LayoutParams(dotDiameter, dotDiameter);
                params.setMargins(dotSpacing, 0, dotSpacing, 0);
                view.setLayoutParams(params);
                addView(view, length - 1);
            } else {
                removeViewAt(length);
            }
            previousLength = length;
        } else {
            removeAllViews();
            previousLength = 0;
        }
    }

    private void fillDot(View view) {
        view.setBackgroundResource(R.drawable.filled_circle);
    }
}
