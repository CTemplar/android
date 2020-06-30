package mobileapp.ctemplar.com.ctemplarapp.view.pinlock;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import timber.log.Timber;

public class RecyclerSpaceDecoration extends RecyclerView.ItemDecoration {
    private final int spanCount;

    public RecyclerSpaceDecoration(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, @NotNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        RecyclerView.LayoutManager layoutManger = parent.getLayoutManager();
        if (layoutManger != null && layoutManger.getWidth() != 0) {
            int numberSize = (int) ThemeUtils.getDimension(parent.getContext(), R.dimen.number_button_size);
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
                outRect.top = space / 3;
            }
        } else {
            int keypadColumnSpace = (int) ThemeUtils.getDimension(parent.getContext(), R.dimen.keypad_column_space);
            int keypadRowSpace = (int) ThemeUtils.getDimension(parent.getContext(), R.dimen.keypad_row_space);
            outRect.left = column * keypadColumnSpace / spanCount;
            outRect.right = keypadColumnSpace - (column + 1) * keypadColumnSpace / spanCount;
            if (position >= spanCount) {
                outRect.top = keypadRowSpace;
            }
        }
    }
}
