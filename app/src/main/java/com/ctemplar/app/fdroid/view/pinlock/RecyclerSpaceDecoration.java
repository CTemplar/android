package com.ctemplar.app.fdroid.view.pinlock;

import android.content.Context;
import android.graphics.Rect;
import android.view.Surface;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.DisplayUtils;

public class RecyclerSpaceDecoration extends RecyclerView.ItemDecoration {
    private final int displayRotation;
    private final int spanCount;

    public RecyclerSpaceDecoration(Context context, int spanCount) {
        this.displayRotation = DisplayUtils.getRotation(context);
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, @NotNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        RecyclerView.LayoutManager layoutManger = parent.getLayoutManager();
        if (layoutManger != null && layoutManger.getWidth() != 0) {
            int numberSize = (int) DisplayUtils.getDimension(parent.getContext(), R.dimen.number_button_size);
            int layoutWidth = layoutManger.getWidth();
            int buttonSpace = numberSize * spanCount;
            int space = (layoutWidth - buttonSpace) / spanCount;
            if (column == 0) {
                outRect.left = space;
                outRect.right = space / 2;
            } else if (column == 1) {
                outRect.left = space / 2;
                outRect.right = space / 2;
            }
            if (position >= spanCount) {
                if (displayRotation == Surface.ROTATION_90 || displayRotation == Surface.ROTATION_270) {
                    outRect.top = 0;
                } else {
                    outRect.top = space / 3;
                }
            }
        } else {
            int keypadColumnSpace = (int) DisplayUtils.getDimension(parent.getContext(), R.dimen.keypad_column_space);
            int keypadRowSpace = (int) DisplayUtils.getDimension(parent.getContext(), R.dimen.keypad_row_space);
            outRect.left = column * keypadColumnSpace / spanCount;
            outRect.right = keypadColumnSpace - (column + 1) * keypadColumnSpace / spanCount;
            if (position >= spanCount) {
                outRect.top = keypadRowSpace;
            }
        }
    }
}
