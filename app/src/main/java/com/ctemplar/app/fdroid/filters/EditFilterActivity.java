package com.ctemplar.app.fdroid.filters;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Menu;
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
import java.util.List;

import butterknife.BindView;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.CustomFilterRequest;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResult;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import timber.log.Timber;

public class EditFilterActivity extends BaseActivity {
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    public static final String ARG_PARAMETER = "parameter";
    public static final String ARG_CONDITION = "condition";
    public static final String ARG_FILTER_TEXT = "filter_text";
    public static final String ARG_MOVE_TO = "move_to";
    public static final String ARG_FOLDER = "folder";
    public static final String ARG_AS_READ = "mark_as_read";
    public static final String ARG_AS_STARRED = "mark_as_starred";

    @BindView(R.id.activity_edit_filter_name_edit_text)
    TextInputEditText filterNameEditText;

    @BindView(R.id.activity_edit_parameter_spinner)
    Spinner parameterSpinner;

    @BindView(R.id.activity_edit_condition_spinner)
    Spinner conditionSpinner;

    @BindView(R.id.activity_edit_filter_text_edit_text)
    TextInputEditText filterTextEditText;

    @BindView(R.id.activity_edit_filter_move_to_check_box)
    CheckBox moveToCheckBox;

    @BindView(R.id.activity_edit_filter_as_read_check_box)
    CheckBox markAsReadCheckBox;

    @BindView(R.id.activity_edit_filter_as_starred_check_box)
    CheckBox markAsStarredCheckBox;

    @BindView(R.id.activity_edit_filter_folder_spinner)
    Spinner filterFolderSpinner;

    @BindView(R.id.activity_edit_filter_delete)
    Button deleteFilterButton;

    private FiltersViewModel filtersModel;

    private static long filterId;
    private static String filterFolder;

    private String[] filterParameterEntries;
    private String[] filterParameterValues;
    private String[] filterConditionEntries;
    private String[] filterConditionValues;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_filter;
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

        Toolbar toolbar = findViewById(R.id.activity_edit_filter_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        filterId = getIntent().getLongExtra(ARG_ID, -1);
        if (filterId == -1) {
            return;
        }
        filterFolder = getIntent().getStringExtra(ARG_FOLDER);
        String filterName = getIntent().getStringExtra(ARG_NAME);
        String filterParameter = getIntent().getStringExtra(ARG_PARAMETER);
        String filterCondition = getIntent().getStringExtra(ARG_CONDITION);
        String filterText = getIntent().getStringExtra(ARG_FILTER_TEXT);
        boolean filterMoveTo = getIntent().getBooleanExtra(ARG_MOVE_TO, false);
        boolean filterAsRead = getIntent().getBooleanExtra(ARG_AS_READ, false);
        boolean filterAsStarred = getIntent().getBooleanExtra(ARG_AS_STARRED, false);

        filterNameEditText.setText(filterName);
        filterTextEditText.setText(filterText);
        moveToCheckBox.setChecked(filterMoveTo);
        markAsReadCheckBox.setChecked(filterAsRead);
        markAsStarredCheckBox.setChecked(filterAsStarred);

        ArrayAdapter<String> parametersAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterParameterEntries
        );
        parameterSpinner.setAdapter(parametersAdapter);
        int filterParameterPosition = Arrays.asList(filterParameterValues).indexOf(filterParameter);
        parameterSpinner.setSelection(filterParameterPosition);

        ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterConditionEntries
        );
        conditionSpinner.setAdapter(conditionsAdapter);
        int filterConditionPosition = Arrays.asList(filterConditionValues).indexOf(filterCondition);
        conditionSpinner.setSelection(filterConditionPosition);

        filtersModel.getFoldersResponse().observe(this, this::handleCustomFolders);
        filtersModel.getEditFilterResponseStatus().observe(this, this::handleEditFilterStatus);
        filtersModel.getDeleteFilterResponseStatus().observe(this, this::handleFilterDeletingStatus);
        getCustomFolders();
        addListeners();
    }

    private void handleEditFilterStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), R.string.txt_filter_not_edited, Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), R.string.txt_filter_edited, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void handleFilterDeletingStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), R.string.txt_filter_not_deleted, Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), R.string.txt_filter_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCustomFolders(FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            Timber.d("foldersResponse is null");
            return;
        }
        List<FoldersResult> customFolderList = foldersResponse.getFoldersList();
        List<String> folderList = new ArrayList<>();
        folderList.add(MainFolderNames.INBOX);
        folderList.add(MainFolderNames.ARCHIVE);
        folderList.add(MainFolderNames.SPAM);
        folderList.add(MainFolderNames.TRASH);
        for (FoldersResult customFolder : customFolderList) {
            String folderName = customFolder.getName();
            folderList.add(folderName);
        }

        ArrayAdapter<String> foldersAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                folderList
        );
        filterFolderSpinner.setAdapter(foldersAdapter);
        int editSelectedFolder = folderList.indexOf(filterFolder);
        filterFolderSpinner.setSelection(editSelectedFolder);
    }

    private void getCustomFolders() {
        filtersModel.getFolders(200, 0);
    }

    public void updateFilter() {
        String filterName = EditTextUtils.getText(filterNameEditText);
        String filterText = EditTextUtils.getText(filterTextEditText);
        String selectedParameter = parameterSpinner.getSelectedItem().toString();
        String selectedCondition = conditionSpinner.getSelectedItem().toString();
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

        CustomFilterRequest customFilterRequest = new CustomFilterRequest();
        customFilterRequest.setName(filterName);
        customFilterRequest.setFilterText(filterText);
        customFilterRequest.setParameter(selectedParameter);
        customFilterRequest.setCondition(selectedCondition);
        customFilterRequest.setFolder(selectedFolder);
        customFilterRequest.setMoveTo(isMoveTo);
        customFilterRequest.setMarkAsRead(markAsRead);
        customFilterRequest.setMarkAsStarred(markAsStarred);
        filtersModel.editFilter(filterId, customFilterRequest);
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
        deleteFilterButton.setOnClickListener(v -> deleteFilter());
    }

    public void deleteFilter() {
        filtersModel.deleteFilter(filterId);
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_filter:
                updateFilter();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
