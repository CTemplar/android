package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class RadioButtonTableLayout extends TableLayout implements OnClickListener {

    private RadioButton activeRadioButton;

    public RadioButtonTableLayout(Context context) {
        super(context);
    }

    public RadioButtonTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if (activeRadioButton != null) {
            activeRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        Drawable checkDrawable = getResources().getDrawable(R.drawable.color_radio_button);
        if (R.id.folder_color_3 == rb.getId()) {
            checkDrawable.setColorFilter(getResources().getColor(R.color.colorWhiteLight), PorterDuff.Mode.SRC_IN);
        } else {
            checkDrawable.clearColorFilter();
        }
        rb.setButtonDrawable(checkDrawable);
        activeRadioButton = rb;
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow)child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow)child);
    }


    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i = 0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if (v instanceof RadioButton) {
                v.setOnClickListener(this);
            }
        }
    }

    public int getCheckedRadioButtonId() {
        if (activeRadioButton != null) {
            return activeRadioButton.getId();
        }

        return -1;
    }

    public void setActive(RadioButton activeButton) {
        activeButton.setChecked(true);
        activeRadioButton = activeButton;
    }
}