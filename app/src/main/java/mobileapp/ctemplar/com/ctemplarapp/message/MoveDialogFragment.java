package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.folders.AddFolderActivity;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;

public class MoveDialogFragment extends DialogFragment {

    private ViewMessagesViewModel viewMessagesModel;
    private List<FoldersResult> customFoldersList;
    private OnMoveListener callback;

    @Override
    public void onResume() {
        super.onResume();
        getCustomFolders();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messages_move_dialog, container, false);

        Bundle bundleArguments = this.getArguments();
        if (bundleArguments == null) {
            return view;
        }
        final long parentMessageId = bundleArguments.getLong(ViewMessagesActivity.PARENT_ID, -1);

        viewMessagesModel = new ViewModelProvider(getActivity()).get(ViewMessagesViewModel.class);
        getCustomFolders();

        viewMessagesModel.getFoldersResponse().observe(getViewLifecycleOwner(), foldersResponse -> {
            if (foldersResponse != null) {
                handleFoldersResponse(view, foldersResponse);
            }
        });

        ImageView closeDialog = view.findViewById(R.id.fragment_messages_move_dialog_close);
        closeDialog.setOnClickListener(v -> dismiss());

        Button buttonCancel = view.findViewById(R.id.fragment_messages_move_dialog_action_cancel);
        buttonCancel.setOnClickListener(v -> dismiss());

        Button buttonApply = view.findViewById(R.id.fragment_messages_move_dialog_action_apply);
        buttonApply.setOnClickListener(v -> {
            RadioGroup foldersRadioGroup = view.findViewById(R.id.fragment_messages_move_dialog_group);
            int checkedId = foldersRadioGroup.getCheckedRadioButtonId();
            for (FoldersResult folderItem : customFoldersList) {
                if (checkedId == folderItem.getId()) {
                    String folderName = folderItem.getName();
                    viewMessagesModel.moveToFolder(parentMessageId, folderName);
                    if (callback != null) {
                        callback.onMove(folderName);
                    }
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_message_moved_to, folderName),
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        return view;
    }

    private void handleFoldersResponse(View view, FoldersResponse foldersResponse) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout foldersListLayout = view.findViewById(R.id.fragment_messages_move_dialog_group);
        customFoldersList = foldersResponse.getFoldersList();

        foldersListLayout.removeAllViewsInLayout();

        for (FoldersResult folderItem : customFoldersList) {
            View folderItemButton = inflater.inflate(R.layout.item_move_folder_radiobutton, foldersListLayout, false);
            RadioButton radioButton = folderItemButton.findViewById(R.id.radio_button);
            radioButton.setId(folderItem.getId());
            radioButton.setText(folderItem.getName());

            Resources resources = requireContext().getResources();
            Drawable folderLeftDrawable = resources.getDrawable(R.drawable.ic_manage_folders);
            Drawable folderRightDrawable = resources.getDrawable(R.drawable.selector_check);
            folderLeftDrawable.mutate();
            folderRightDrawable.mutate();

            int folderColor = Color.parseColor(folderItem.getColor());
            int markColor = resources.getColor(R.color.secondaryTextColor);
            folderLeftDrawable.setColorFilter(folderColor, PorterDuff.Mode.SRC_IN);
            folderRightDrawable.setColorFilter(markColor, PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(folderRightDrawable, markColor);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(folderLeftDrawable, null, folderRightDrawable, null);
            foldersListLayout.addView(folderItemButton);
        }

        View addFolderLayout = inflater.inflate(R.layout.manage_folders_footer, foldersListLayout, false);
        Button addFolderButton = addFolderLayout.findViewById(R.id.manager_folders_footer_btn);
        addFolderButton.setOnClickListener(v -> {
            Intent addFolder = new Intent(getActivity(), AddFolderActivity.class);
            startActivity(addFolder);
        });
        View manageFolderLayout = inflater.inflate(R.layout.item_manage_folders, foldersListLayout, false);
        TextView manageFolderButton = manageFolderLayout.findViewById(R.id.manager_folders);
        manageFolderButton.setOnClickListener(v -> {
            Intent managerFolderIntent = new Intent(getActivity(), ManageFoldersActivity.class);
            startActivity(managerFolderIntent);
        });

        if (customFoldersList.isEmpty()) {
            foldersListLayout.addView(addFolderLayout);
        } else {
            foldersListLayout.addView(manageFolderLayout);
        }
    }


    private void getCustomFolders() {
        viewMessagesModel.getFolders(200, 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    public void setOnMoveCallback(OnMoveListener onMoveListener) {
        this.callback = onMoveListener;
    }

    public interface OnMoveListener {
        void onMove(String folderName);
    }
}
