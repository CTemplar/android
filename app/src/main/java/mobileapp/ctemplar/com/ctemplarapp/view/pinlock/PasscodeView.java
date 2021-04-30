package mobileapp.ctemplar.com.ctemplarapp.view.pinlock;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.DisplayUtils;

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
        dotDiameter = (int) DisplayUtils.getDimension(getContext(), R.dimen.passcode_dot_diameter);
        dotSpacing = (int) DisplayUtils.getDimension(getContext(), R.dimen.passcode_dot_spacing);
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

    public void updatePasscode(int length) {
        if (length > 0) {
            if (length > previousLength) {
                View view = new View(getContext());
                fillDot(view);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotDiameter, dotDiameter);
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
