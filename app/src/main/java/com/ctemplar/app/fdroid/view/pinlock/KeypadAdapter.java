package com.ctemplar.app.fdroid.view.pinlock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class KeypadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_NUMBER = 0;
    private static final int VIEW_TYPE_DELETE = 1;

    private OnNumClickListener onNumClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    private int[] keyValues = getAdjustKeyValues(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
    private int pinLength;

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_NUMBER) {
            View view = inflater.inflate(R.layout.item_number, parent, false);
            return new NumberViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_delete, parent, false);
            return new DeleteViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NUMBER) {
            setupNumberButtonHolder((NumberViewHolder) holder, position);
        } else if (holder.getItemViewType() == VIEW_TYPE_DELETE) {
            setupDeleteButtonHolder((DeleteViewHolder) holder);
        }
    }

    private void setupNumberButtonHolder(NumberViewHolder holder, int position) {
        if (holder != null) {
            if (position == 9) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                int keyValue = keyValues[position];
                holder.numberTextView.setText(String.valueOf(keyValue));
                holder.numberHintTextView.setText(getKeyPadWordHint(keyValue));
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setTag(keyValues[position]);
            }
        }
    }

    private void setupDeleteButtonHolder(DeleteViewHolder holder) {
        if (holder != null) {
            if (pinLength > 0) {
                holder.itemView.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_DELETE;
        }
        return VIEW_TYPE_NUMBER;
    }

    public int getPinLength() {
        return pinLength;
    }

    public void setPinLength(int pinLength) {
        this.pinLength = pinLength;
    }

    public int[] getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(int[] keyValues) {
        this.keyValues = getAdjustKeyValues(keyValues);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnNumClickListener onNumClickListener) {
        this.onNumClickListener = onNumClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    class NumberViewHolder extends ViewHolder {
        @BindView(R.id.number_text_view)
        TextView numberTextView;
        @BindView(R.id.number_hint_text_view)
        TextView numberHintTextView;

        NumberViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (onNumClickListener != null) {
                    onNumClickListener.onNumClicked(Integer.parseInt(
                            EditTextUtils.getText(numberTextView)));
                }
            });
        }
    }

    class DeleteViewHolder extends ViewHolder {
        DeleteViewHolder(final View itemView) {
            super(itemView);
            if (isRecyclable() && pinLength > 0) {
                itemView.setOnClickListener(v -> {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClicked();
                    }
                });
                itemView.setOnLongClickListener(v -> {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteLongClicked();
                    }
                    return true;
                });
            }
        }
    }

    static abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private int[] getAdjustKeyValues(int[] keyValues) {
        int[] adjustedKeyValues = new int[keyValues.length + 1];
        for (int i = 0; i < keyValues.length; i++) {
            if (i < 9) {
                adjustedKeyValues[i] = keyValues[i];
            } else {
                adjustedKeyValues[i] = -1;
                adjustedKeyValues[i + 1] = keyValues[i];
            }
        }
        return adjustedKeyValues;
    }

    private String getKeyPadWordHint(int keyValue) {
        switch (keyValue) {
            case 2:
                return "ABC";
            case 3:
                return "DEF";
            case 4:
                return "GHI";
            case 5:
                return "JKL";
            case 6:
                return "MNO";
            case 7:
                return "PRQS";
            case 8:
                return "TUV";
            case 9:
                return "WXYZ";
            case 0:
                return "+";
            default:
                return "";
        }
    }

    public interface OnNumClickListener {
        void onNumClicked(int keyValue);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked();
        void onDeleteLongClicked();
    }

    public interface KeypadListener {
        void onComplete(String pinCode);
        void onPINChanged(int pinLength, String changedPIN);
        void onEmpty();
    }
}
