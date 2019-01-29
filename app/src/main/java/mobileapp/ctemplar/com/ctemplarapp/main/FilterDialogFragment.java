package mobileapp.ctemplar.com.ctemplarapp.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final ImageView closeDialog = view.findViewById(R.id.fragment_messages_filter_dialog_close);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final TextView clearAllSelected = view.findViewById(R.id.fragment_messages_filter_dialog_clear_all);
        clearAllSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxIsStarred.setChecked(false);
                checkBoxIsUnread.setChecked(false);
                checkBoxWithAttachment.setChecked(false);
            }
        });

        Button buttonApply = view.findViewById(R.id.fragment_messages_filter_dialog_action_apply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onApplyClickListener != null) {
                    boolean isStarred = checkBoxIsStarred.isChecked();
                    boolean isUnread = checkBoxIsUnread.isChecked();
                    boolean withAttachment = checkBoxWithAttachment.isChecked();
                    onApplyClickListener.onApply(isStarred, isUnread, withAttachment);
                }
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(this.getActivity(), R.style.DialogAnimation);
    }

    interface OnApplyClickListener {
        void onApply(boolean isStarred, boolean isUnread, boolean withAttachment);
    }

}
