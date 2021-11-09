package mobileapp.ctemplar.com.ctemplarapp.main;

import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ALL_MAILS;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.STARRED;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.UNREAD;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentMessagesSearchDialogBinding;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.settings.filters.EditFilterActivity;
import mobileapp.ctemplar.com.ctemplarapp.settings.filters.FiltersViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import timber.log.Timber;

public class SearchDialogFragment extends DialogFragment {
    private FragmentMessagesSearchDialogBinding binding;
    private FiltersViewModel filtersModel;

    private OnApplyClickListener onApplyClickListener;

    private String searchFolder;

    private String[] sizeConditionEntries;
    private String[] sizeConditionValues;
    private String[] sizeMeasureEntries;
    private String[] sizeMeasureValues;

    interface OnApplyClickListener {
        void onApply(boolean isStarred, boolean isUnread, boolean withAttachment);
    }

    public void setOnApplyClickListener(OnApplyClickListener onApplyClickListener) {
        this.onApplyClickListener = onApplyClickListener;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Dialog dialog = getDialog();
//        if (dialog == null) {
//            Timber.e("dialog == null");
//            return;
//        }
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        filtersModel = new ViewModelProvider(this).get(FiltersViewModel.class);
    }

    //    @Override
//    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentInboxBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        sizeConditionEntries = getResources().getStringArray(R.array.size_condition_entries);
        sizeConditionValues = getResources().getStringArray(R.array.size_condition_values);
        sizeMeasureEntries = getResources().getStringArray(R.array.size_measure_entries);
        sizeMeasureValues = getResources().getStringArray(R.array.size_measure_values);
        binding = FragmentMessagesSearchDialogBinding.inflate(inflater, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filtersModel.getFoldersResponse().observe(this, this::handleCustomFolders);
        getCustomFolders();
        //        final CheckBox checkBoxIsStarred = view.findViewById(R.id.fragment_messages_filter_dialog_starred);
//        final CheckBox checkBoxIsUnread = view.findViewById(R.id.fragment_messages_filter_dialog_unread);
//        final CheckBox checkBoxWithAttachment = view.findViewById(R.id.fragment_messages_filter_dialog_with_attachment);
//
//        View buttonCancel = view.findViewById(R.id.fragment_messages_filter_dialog_action_cancel);
//        buttonCancel.setOnClickListener(v -> dismiss());
//
//        final ImageView closeDialog = view.findViewById(R.id.fragment_messages_filter_dialog_close);
//        closeDialog.setOnClickListener(v -> dismiss());

        binding.closeImageView.setOnClickListener(v -> dismiss());
//
//        final TextView clearAllSelected = view.findViewById(R.id.fragment_messages_filter_dialog_clear_all);
//        clearAllSelected.setOnClickListener(v -> {
//            if (onApplyClickListener != null) {
//                checkBoxIsStarred.setChecked(false);
//                checkBoxIsUnread.setChecked(false);
//                checkBoxWithAttachment.setChecked(false);
//                onApplyClickListener.onApply(false, false, false);
//                dismiss();
//            }
//        });
//
//        Button buttonApply = view.findViewById(R.id.fragment_messages_filter_dialog_action_apply);
//        buttonApply.setOnClickListener(v -> {
//            if (onApplyClickListener != null) {
//                boolean isStarred = checkBoxIsStarred.isChecked();
//                boolean isUnread = checkBoxIsUnread.isChecked();
//                boolean withAttachment = checkBoxWithAttachment.isChecked();
//                onApplyClickListener.onApply(isStarred, isUnread, withAttachment);
//                dismiss();
//            }
//        });
        binding.startDateLayout.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), 0,
                    (view1, year, month, dayOfMonth) -> {
                        binding.startDateTextView.setText(year + "-" + month + "-" + dayOfMonth);
                    }, 0, 0, 0);
            datePickerDialog.show();
        });
        binding.endDateLayout.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), 0,
                    (view1, year, month, dayOfMonth) -> {
                        binding.endDateTextView.setText(year + "-" + month + "-" + dayOfMonth);
                    }, 0, 0, 0);
            datePickerDialog.show();
        });
        ArrayAdapter<String> sizeConditionAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_domain_spinner,
                sizeConditionEntries
        );
        binding.sizeConditionSpinner.setAdapter(sizeConditionAdapter);
        ArrayAdapter<String> sizeMeasureAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_domain_spinner,
                sizeMeasureEntries
        );
        binding.sizeMeasureSpinner.setAdapter(sizeMeasureAdapter);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    private void handleCustomFolders(FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            Timber.d("foldersResponse is null");
            return;
        }
        List<FoldersResult> customFolderList = foldersResponse.getFoldersList();
        List<String> folderList = new ArrayList<>(Arrays.asList(ALL_MAILS, ARCHIVE, DRAFT, INBOX,
                OUTBOX, SENT, SPAM, STARRED, TRASH, UNREAD));
        for (FoldersResult customFolder : customFolderList) {
            folderList.add(customFolder.getName());
        }
        ArrayAdapter<String> foldersAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_folder_spinner,
                folderList
        );
        binding.folderSpinner.setAdapter(foldersAdapter);
//        binding.folderSpinner.setSelection(folderList.indexOf(filterFolder));
    }

    private void getCustomFolders() {
        filtersModel.getFolders(200, 0);
    }
}
