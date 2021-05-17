package com.ctemplar.app.fdroid.filters;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterConditionRequest;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterRequest;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResult;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import timber.log.Timber;

public class AddFilterActivity extends BaseActivity {
    @BindView(R.id.activity_add_filter_name_edit_text)
    TextInputEditText filterNameEditText;

    @BindView(R.id.activity_add_parameter_spinner)
    Spinner parameterSpinner;

    @BindView(R.id.activity_add_condition_spinner)
    Spinner conditionSpinner;

    @BindView(R.id.activity_add_filter_text_edit_text)
    TextInputEditText filterTextEditText;

    @BindView(R.id.activity_add_filter_move_to_check_box)
    CheckBox moveToCheckBox;

    @BindView(R.id.activity_add_filter_as_read_check_box)
    CheckBox markAsReadCheckBox;

    @BindView(R.id.activity_add_filter_as_starred_check_box)
    CheckBox markAsStarredCheckBox;

    @BindView(R.id.activity_add_filter_folder_spinner)
    Spinner filterFolderSpinner;

    @BindView(R.id.activity_add_filter_action_submit)
    Button addCustomFilterButton;

    @BindView(R.id.activity_add_filter_action_cancel)
    Button cancelButton;

    private FiltersViewModel filtersModel;

    private String[] filterParameterEntries;
    private String[] filterParameterValues;
    private String[] filterConditionEntries;
    private String[] filterConditionValues;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_filter;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        filterParameterEntries = getResources().getStringArray(R.array.filter_parameter_entries);
        filterParameterValues = getResources().getStringArray(R.array.filter_parameter_values);
        filterConditionEntries = getResources().getStringArray(R.array.filter_condition_entries);
        filterConditionValues = getResources().getStringArray(R.array.filter_condition_values);
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filtersModel = new ViewModelProvider(this).get(FiltersViewModel.class);

        Toolbar toolbar = findViewById(R.id.activity_add_filter_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ArrayAdapter<String> parametersAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterParameterEntries
        );
        parameterSpinner.setAdapter(parametersAdapter);
        ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterConditionEntries
        );
        conditionSpinner.setAdapter(conditionsAdapter);

        filtersModel.getFoldersResponse().observe(this, this::handleCustomFolders);
        getCustomFolders();
        addListeners();
    }

    private void handleCustomFolders(FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            Timber.d("foldersResponse is null");
            return;
        }
        List<FoldersResult> customFolderList = foldersResponse.getFoldersList();
        List<String> folderList = new ArrayList<>(Arrays.asList(MainFolderNames.INBOX,
                MainFolderNames.ARCHIVE, MainFolderNames.SPAM, MainFolderNames.TRASH));
        for (FoldersResult customFolder : customFolderList) {
            folderList.add(customFolder.getName());
        }

        ArrayAdapter<String> foldersAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                folderList
        );
        filterFolderSpinner.setAdapter(foldersAdapter);

        filtersModel.getAddFilterResponseStatus().observe(this, this::handleAddFilterStatus);
    }

    private void handleAddFilterStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), R.string.toast_filter_not_created, Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), R.string.toast_filter_created, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    public void createCustomFilter() {
        String filterName = EditTextUtils.getText(filterNameEditText);
        String filterText = EditTextUtils.getText(filterTextEditText);
        String selectedParameter = filterParameterValues[parameterSpinner.getSelectedItemPosition()];
        String selectedCondition = filterConditionValues[conditionSpinner.getSelectedItemPosition()];
        String selectedFolder = filterFolderSpinner.getSelectedItem().toString();
        boolean isMoveTo = moveToCheckBox.isChecked();
        boolean markAsRead = markAsReadCheckBox.isChecked();
        boolean markAsStarred = markAsStarredCheckBox.isChecked();

        if (!EditTextUtils.isTextValid(filterName) || !EditTextUtils.isTextLength(filterName, 4, 30)) {
            filterNameEditText.setError(getString(R.string.txt_filter_name_hint));
            return;
        }
        if (!EditTextUtils.isTextLength(filterText, 1, 30)) {
            filterTextEditText.setError(getString(R.string.txt_filter_text_hint));
            return;
        }

        EmailFilterRequest emailFilterRequest = new EmailFilterRequest();
        emailFilterRequest.setName(filterName);

        EmailFilterConditionRequest emailFilterConditionRequest = new EmailFilterConditionRequest();
        emailFilterConditionRequest.setFilterText(filterText);
        emailFilterConditionRequest.setParameter(selectedParameter);
        emailFilterConditionRequest.setCondition(selectedCondition);

        emailFilterRequest.setConditions(Collections.singletonList(emailFilterConditionRequest));
        emailFilterRequest.setFolder(selectedFolder);
        emailFilterRequest.setMoveTo(isMoveTo);
        emailFilterRequest.setMarkAsRead(markAsRead);
        emailFilterRequest.setMarkAsStarred(markAsStarred);
        filtersModel.addFilter(emailFilterRequest);
    }

    private void addListeners() {
        filterNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNameEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addCustomFilterButton.setOnClickListener(v -> createCustomFilter());
        cancelButton.setOnClickListener(v -> cancel());
    }

    private void getCustomFolders() {
        filtersModel.getFolders(200, 0);
    }

    public void cancel() {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
