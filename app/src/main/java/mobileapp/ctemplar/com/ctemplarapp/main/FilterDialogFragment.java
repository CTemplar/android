package mobileapp.ctemplar.com.ctemplarapp.main;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class FilterDialogFragment extends DialogFragment {

    private OnApplyClickListener onApplyClickListener;

    interface OnApplyClickListener {
        void onApply(boolean isStarred, boolean isUnread, boolean withAttachment);
    }

    public void setOnApplyClickListener(OnApplyClickListener onApplyClickListener) {
        this.onApplyClickListener = onApplyClickListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_filter_dialog, container, false);

        final CheckBox checkBoxIsStarred = view.findViewById(R.id.fragment_messages_filter_dialog_starred);
        final CheckBox checkBoxIsUnread = view.findViewById(R.id.fragment_messages_filter_dialog_unread);
        final CheckBox checkBoxWithAttachment = view.findViewById(R.id.fragment_messages_filter_dialog_with_attachment);

        View buttonCancel = view.findViewById(R.id.fragment_messages_filter_dialog_action_cancel);
        buttonCancel.setOnClickListener(v -> dismiss());

        final ImageView closeDialog = view.findViewById(R.id.fragment_messages_filter_dialog_close);
        closeDialog.setOnClickListener(v -> dismiss());

        final TextView clearAllSelected = view.findViewById(R.id.fragment_messages_filter_dialog_clear_all);
        clearAllSelected.setOnClickListener(v -> {
            checkBoxIsStarred.setChecked(false);
            checkBoxIsUnread.setChecked(false);
            checkBoxWithAttachment.setChecked(false);
        });

        Button buttonApply = view.findViewById(R.id.fragment_messages_filter_dialog_action_apply);
        buttonApply.setOnClickListener(v -> {
            dismiss();
            if (onApplyClickListener != null) {
                boolean isStarred = checkBoxIsStarred.isChecked();
                boolean isUnread = checkBoxIsUnread.isChecked();
                boolean withAttachment = checkBoxWithAttachment.isChecked();
                onApplyClickListener.onApply(isStarred, isUnread, withAttachment);
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(this.getActivity(), R.style.DialogAnimation);
    }
}
