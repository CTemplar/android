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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentMessagesSearchDialogBinding;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.SearchMessagesDTO;
import mobileapp.ctemplar.com.ctemplarapp.settings.filters.FiltersViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class SearchDialogFragment extends DialogFragment {
    private FragmentMessagesSearchDialogBinding binding;
    private FiltersViewModel filtersModel;

    private SearchClickListener searchClickListener;

    private SearchMessagesDTO searchMessages;

    private Calendar startDateCalendar;
    private Calendar endDateCalendar;

    private String[] sizeConditionEntries;
    private String[] sizeConditionValues;
    private String[] sizeMeasureEntries;
    private String[] sizeMeasureValues;

    interface SearchClickListener {
        void onSearch(SearchMessagesDTO searchMessages);
    }

    public void setSearchClickListener(SearchClickListener searchClickListener) {
        this.searchClickListener = searchClickListener;
    }

    public void setSearchText(String searchText) {
        binding.keywordEditText.setText(searchText);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        filtersModel = new ViewModelProvider(this).get(FiltersViewModel.class);
        searchMessages = new SearchMessagesDTO();
    }

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
        binding.closeImageView.setOnClickListener(v -> dismiss());
        binding.clearAllTextView.setOnClickListener(v -> clearAll());
        binding.clearFilterButton.setOnClickListener(v -> clearFilter());
        binding.searchButton.setOnClickListener(v -> search());

        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        binding.startDateLayout.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), 0,
                    (view1, year, month, dayOfMonth) -> {
                        startDateCalendar.set(year, month, dayOfMonth);
                        setStartDateView(startDateCalendar);
                    }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH),
                    startDateCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        binding.endDateLayout.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), 0,
                    (view1, year, month, dayOfMonth) -> {
                        endDateCalendar.set(year, month, dayOfMonth);
                        setEndDateView(endDateCalendar);
                    }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH),
                    endDateCalendar.get(Calendar.DAY_OF_MONTH));
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
        filtersModel.getFoldersResponse().observe(this, this::handleCustomFolders);
        getCustomFolders();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    private void clearAll() {
        binding.keywordEditText.setText("");
        binding.fromEditText.setText("");
        binding.toEditText.setText("");
        binding.sizeEditText.setText("");
        binding.sameExactlyCheckbox.setChecked(false);
        binding.hasAttachmentCheckbox.setChecked(false);
        binding.sizeConditionSpinner.setSelection(0);
        binding.sizeMeasureSpinner.setSelection(0);
        binding.folderSpinner.setSelection(0);
    }

    private void clearFilter() {
        if (searchClickListener == null) {
            Timber.e("searchClickListener == null");
            return;
        }
        clearAll();
        searchClickListener.onSearch(null);
        dismiss();
    }

    private void search() {
        if (searchClickListener == null) {
            Timber.e("searchClickListener == null");
            return;
        }
        String fromEmail = EditTextUtils.getText(binding.fromEditText).trim();
        String toEmail = EditTextUtils.getText(binding.toEditText).trim();
        if (EditTextUtils.isEmailListValid(fromEmail) || TextUtils.isEmpty(fromEmail)) {
            binding.fromInputLayout.setError(null);
        } else {
            binding.fromInputLayout.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        if (EditTextUtils.isEmailListValid(toEmail) || TextUtils.isEmpty(toEmail)) {
            binding.toInputLayout.setError(null);
        } else {
            binding.toInputLayout.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        searchMessages.setQuery(EditTextUtils.getText(binding.keywordEditText).trim());
        searchMessages.setExact(binding.sameExactlyCheckbox.isChecked());
        searchMessages.setFolder(binding.folderSpinner.getSelectedItem().toString());
        searchMessages.setHaveAttachment(binding.hasAttachmentCheckbox.isChecked());
        if (EditTextUtils.isNotEmpty(fromEmail)) {
            List<String> fromEmailList = new ArrayList<>(EditTextUtils.getListFromString(fromEmail));
            searchMessages.setReceiver(EditTextUtils.getStringFromList(fromEmailList));
        }
        if (EditTextUtils.isNotEmpty(toEmail)) {
            List<String> toEmailList = new ArrayList<>(EditTextUtils.getListFromString(toEmail));
            searchMessages.setSender(EditTextUtils.getStringFromList(toEmailList));
        }
        if (EditTextUtils.isNotEmpty(EditTextUtils.getText(binding.startDateTextView))) {
            String startDateString = DateUtils.getFilterDate(startDateCalendar.getTimeInMillis());
            searchMessages.setStartDate(startDateString);
        }
        if (EditTextUtils.isNotEmpty(EditTextUtils.getText(binding.endDateTextView))) {
            String endDateString = DateUtils.getFilterDate(endDateCalendar.getTimeInMillis());
            searchMessages.setEndDate(endDateString);
        }
        if (EditTextUtils.isNotEmpty(EditTextUtils.getText(binding.sizeEditText))) {
            int sizeValue = 0;
            try {
                sizeValue = Integer.parseInt(EditTextUtils.getText(binding.sizeEditText));
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
            int sizeMeasurePosition = binding.sizeMeasureSpinner.getSelectedItemPosition();
            int sizeMeasureMultiplier = 1;
            try {
                sizeMeasureMultiplier = Integer.parseInt(sizeMeasureValues[sizeMeasurePosition]);
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
            int sizeFormattedValue = sizeValue * sizeMeasureMultiplier;
            searchMessages.setSize(sizeFormattedValue);
        }
        int sizeConditionPosition = binding.sizeConditionSpinner.getSelectedItemPosition();
        searchMessages.setSizeOperator(sizeConditionValues[sizeConditionPosition]);
        searchClickListener.onSearch(searchMessages);
        dismiss();
    }

    private void setStartDateView(Calendar calendar) {
        binding.startDateTextView.setText(DateUtils.getFilterDate(calendar.getTimeInMillis()));
    }

    private void setEndDateView(Calendar calendar) {
        binding.endDateTextView.setText(DateUtils.getFilterDate(calendar.getTimeInMillis()));
    }

    private void handleCustomFolders(FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            Timber.d("foldersResponse is null");
            return;
        }
        List<FoldersResult> customFolderList = foldersResponse.getFoldersList();
        List<String> folderList = new ArrayList<>(Arrays.asList(ALL_MAILS, ARCHIVE, DRAFT, INBOX,
                OUTBOX, SENT, SPAM, STARRED, TRASH, UNREAD));
        List<Integer> folderColorList = new ArrayList<>(Collections.nCopies(folderList.size(), -1));
        for (FoldersResult customFolder : customFolderList) {
            folderList.add(customFolder.getName());
            try {
                folderColorList.add(Color.parseColor(customFolder.getColor()));
            } catch (IllegalArgumentException e) {
                Timber.e(e);
            }
        }
        ArrayAdapter<String> foldersAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.item_folder_spinner,
                folderList
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                return getTextViewDrawableColor(view, folderColorList.get(position));
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                return getTextViewDrawableColor(view, folderColorList.get(position));
            }
        };
        binding.folderSpinner.setAdapter(foldersAdapter);
//        binding.folderSpinner.setSelection(folderList.indexOf(searchMessages.getFolder()));
    }

    private View getTextViewDrawableColor(View view, int color) {
        if (view instanceof TextView && color != -1) {
            TextView textView = ((TextView) view);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                TextViewCompat.setCompoundDrawableTintList(textView, ColorStateList.valueOf(color));
            } else {
                for (Drawable drawable : textView.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.setTint(color);
                    }
                }
            }
        }
        return view;
    }

    private void getCustomFolders() {
        filtersModel.getFolders(200, 0);
    }
}
