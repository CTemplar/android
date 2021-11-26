package com.ctemplar.app.fdroid.message.dialog;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.FragmentMessagesMoveDialogBinding;
import com.ctemplar.app.fdroid.folders.AddFolderActivity;
import com.ctemplar.app.fdroid.folders.ManageFoldersActivity;
import com.ctemplar.app.fdroid.message.ViewMessagesViewModel;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResult;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import timber.log.Timber;

public class MoveDialogFragment extends DialogFragment {
    public static final String MESSAGE_IDS = "message_ids";

    private FragmentMessagesMoveDialogBinding binding;
    private ViewMessagesViewModel viewMessagesModel;
    private List<FoldersResult> customFoldersList;
    private OnMoveListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewMessagesModel = new ViewModelProvider(getActivity()).get(ViewMessagesViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        if (dialog == null) {
            Timber.e("dialog is null");
            return;
        }
        getCustomFolders();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessagesMoveDialogBinding.inflate(inflater, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundleArguments = getArguments();
        if (bundleArguments == null) {
            Timber.e("bundleArguments is null");
            return;
        }
        binding.closeImageView.setOnClickListener(v -> dismiss());
        binding.cancelActionButton.setOnClickListener(v -> dismiss());

        long[] messageIds = bundleArguments.getLongArray(MESSAGE_IDS);
        Long[] messageObjectIds = new Long[messageIds.length];
        for (int i = 0; i < messageIds.length; ++i) {
            messageObjectIds[i] = messageIds[i];
        }
        binding.applyActionButton.setOnClickListener(v -> {
            int checkedId = binding.foldersRadioGroup.getCheckedRadioButtonId();
            for (FoldersResult folderItem : customFoldersList) {
                if (checkedId == folderItem.getId()) {
                    String folderName = folderItem.getName();
                    viewMessagesModel.moveToFolder(messageObjectIds, folderName);
                    if (callback != null) {
                        callback.onMove(folderName);
                    }
                    ToastUtils.showToast(getActivity(), getString(R.string.toast_message_moved_to, folderName));
                    dismiss();
                }
            }
        });

        viewMessagesModel.getFoldersResponse().observe(getViewLifecycleOwner(),
                foldersResponse -> handleFoldersResponse(view, foldersResponse));
        getCustomFolders();
    }

    private void handleFoldersResponse(View view, FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            Timber.e("foldersResponse is null");
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        customFoldersList = foldersResponse.getFoldersList();
        binding.foldersRadioGroup.removeAllViewsInLayout();

        for (FoldersResult folderItem : customFoldersList) {
            View folderItemButton = inflater.inflate(R.layout.item_move_folder_radiobutton,
                    binding.foldersRadioGroup, false);
            RadioButton radioButton = folderItemButton.findViewById(R.id.radio_button);
            radioButton.setId(folderItem.getId());
            radioButton.setText(folderItem.getName());

            Resources resources = requireContext().getResources();
            Drawable folderLeftDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_manage_folders, null);
            Drawable folderRightDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.selector_check, null);
            if (folderLeftDrawable == null || folderRightDrawable == null) {
                continue;
            }
            folderLeftDrawable.mutate();
            folderRightDrawable.mutate();

            int folderColor = Color.parseColor(folderItem.getColor());
            int markColor = resources.getColor(R.color.secondaryTextColor);
            folderLeftDrawable.setColorFilter(folderColor, PorterDuff.Mode.SRC_IN);
            folderRightDrawable.setColorFilter(markColor, PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(folderRightDrawable, markColor);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(folderLeftDrawable, null,
                    folderRightDrawable, null);
            binding.foldersRadioGroup.addView(folderItemButton);
        }

        View addFolderLayout = inflater.inflate(R.layout.manage_folders_footer,
                binding.foldersRadioGroup, false);
        Button addFolderButton = addFolderLayout.findViewById(R.id.manager_folders_footer_btn);
        addFolderButton.setOnClickListener(v -> {
            Intent addFolder = new Intent(getActivity(), AddFolderActivity.class);
            startActivity(addFolder);
        });
        View manageFolderLayout = inflater.inflate(R.layout.item_manage_folders,
                binding.foldersRadioGroup, false);
        TextView manageFolderButton = manageFolderLayout.findViewById(R.id.manager_folders);
        manageFolderButton.setOnClickListener(v -> {
            Intent managerFolderIntent = new Intent(getActivity(), ManageFoldersActivity.class);
            startActivity(managerFolderIntent);
        });

        if (customFoldersList.isEmpty()) {
            binding.foldersRadioGroup.addView(addFolderLayout);
        } else {
            binding.foldersRadioGroup.addView(manageFolderLayout);
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
