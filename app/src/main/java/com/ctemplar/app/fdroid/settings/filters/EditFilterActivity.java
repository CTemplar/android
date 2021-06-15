package com.ctemplar.app.fdroid.settings.filters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterConditionRequest;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterRequest;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterConditionResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResult;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import timber.log.Timber;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

public class EditFilterActivity extends BaseActivity {
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    public static final String ARG_CONDITIONS = "conditions";
    public static final String ARG_MOVE_TO = "move_to";
    public static final String ARG_FOLDER = "folder";
    public static final String ARG_AS_READ = "mark_as_read";
    public static final String ARG_AS_STARRED = "mark_as_starred";
    public static final String ARG_DELETE_MSG = "delete_msg";

    @BindView(R.id.activity_edit_filter_name_edit_text)
    TextInputEditText filterNameEditText;

    @BindView(R.id.activity_edit_filter_conditions_holder)
    ViewGroup conditionsHolder;

    @BindView(R.id.activity_edit_filter_add_condition)
    Button addConditionButton;

    @BindView(R.id.activity_edit_filter_delete_check_box)
    CheckBox deleteMsgCheckBox;

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
    private final List<ConditionViews> conditionViewsList = new ArrayList<>();

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
        conditionsHolder.removeAllViews();
        conditionViewsList.clear();

        Intent intent = getIntent();
        filterId = intent.getLongExtra(ARG_ID, -1);
        if (filterId == -1) {
            return;
        }
        filterFolder = intent.getStringExtra(ARG_FOLDER);
        String filterName = intent.getStringExtra(ARG_NAME);
        String[] conditionStringArray = intent.getStringArrayExtra(ARG_CONDITIONS);
        for (String conditionResponse : conditionStringArray) {
            try {
                EmailFilterConditionResponse response = GENERAL_GSON.fromJson(conditionResponse, EmailFilterConditionResponse.class);
                addCondition(response.getParameter(), response.getCondition(), response.getFilterText());
            } catch (JsonSyntaxException e) {
                Timber.e(e, "Cannot parse conditionStringArray");
            }
        }
        boolean filterMoveTo = intent.getBooleanExtra(ARG_MOVE_TO, false);
        boolean filterAsRead = intent.getBooleanExtra(ARG_AS_READ, false);
        boolean filterAsStarred = intent.getBooleanExtra(ARG_AS_STARRED, false);
        boolean filterDeleteMsg = intent.getBooleanExtra(ARG_DELETE_MSG, false);

        filterNameEditText.setText(filterName);
        moveToCheckBox.setChecked(filterMoveTo);
        markAsReadCheckBox.setChecked(filterAsRead);
        markAsStarredCheckBox.setChecked(filterAsStarred);
        deleteMsgCheckBox.setChecked(filterDeleteMsg);

        filtersModel.getFoldersResponse().observe(this, this::handleCustomFolders);
        filtersModel.getEditFilterResponseStatus().observe(this, this::handleEditFilterStatus);
        filtersModel.getDeleteFilterResponseStatus().observe(this, this::handleFilterDeletingStatus);
        getCustomFolders();
        addListeners();
    }

    private void addCondition() {
        addCondition(null, null, null);
    }

    private void addCondition(String parameter, String condition, String filterText) {
        View view = getLayoutInflater().inflate(R.layout.filter_condition, conditionsHolder, false);
        ConditionViews conditionViews = new ConditionViews(view);
        if (parameter != null) {
            int parameterPosition = Arrays.asList(filterParameterValues).indexOf(parameter);
            conditionViews.parameterSpinner.setSelection(parameterPosition);
        }
        if (condition != null) {
            int conditionPosition = Arrays.asList(filterConditionValues).indexOf(condition);
            conditionViews.conditionSpinner.setSelection(conditionPosition);
        }
        if (filterText != null) {
            conditionViews.editText.setText(filterText);
        }
        conditionViewsList.add(conditionViews);
        conditionsHolder.addView(view);
        updateConditionCloseVisibility();
    }

    private void updateConditionCloseVisibility() {
        if (conditionViewsList.size() > 1) {
            for (ConditionViews conditionViews : conditionViewsList) {
                conditionViews.closeView.setVisibility(View.VISIBLE);
            }
        } else {
            for (ConditionViews conditionViews : conditionViewsList) {
                conditionViews.closeView.setVisibility(View.GONE);
            }
        }
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
        filterFolderSpinner.setSelection(folderList.indexOf(filterFolder));
    }

    private void getCustomFolders() {
        filtersModel.getFolders(200, 0);
    }

    public void updateFilter() {
        String filterName = EditTextUtils.getText(filterNameEditText);
        if (!EditTextUtils.isTextValid(filterName) || !EditTextUtils.isTextLength(filterName, 4, 30)) {
            filterNameEditText.setError(getString(R.string.txt_filter_name_hint));
            return;
        }
        String selectedFolder = filterFolderSpinner.getSelectedItem().toString();
        boolean moveTo = moveToCheckBox.isChecked();
        boolean markAsRead = markAsReadCheckBox.isChecked();
        boolean markAsStarred = markAsStarredCheckBox.isChecked();
        boolean deleteMsg = deleteMsgCheckBox.isChecked();

        EmailFilterRequest emailFilterRequest = new EmailFilterRequest();
        emailFilterRequest.setName(filterName);
        List<EmailFilterConditionRequest> conditionRequestList = new ArrayList<>();
        for (ConditionViews conditionViews : conditionViewsList) {
            String filterText = EditTextUtils.getText(conditionViews.editText);
            if (!EditTextUtils.isTextLength(filterText, 1, 30)) {
                conditionViews.editText.setError(getString(R.string.txt_filter_text_hint));
                return;
            }
            String selectedParameter = conditionViews.getSelectedParameter();
            String selectedCondition = conditionViews.getSelectedCondition();
            EmailFilterConditionRequest emailFilterConditionRequest = new EmailFilterConditionRequest();
            emailFilterConditionRequest.setFilterText(filterText);
            emailFilterConditionRequest.setParameter(selectedParameter);
            emailFilterConditionRequest.setCondition(selectedCondition);
            conditionRequestList.add(emailFilterConditionRequest);
        }
        emailFilterRequest.setConditions(conditionRequestList);
        emailFilterRequest.setFolder(selectedFolder);
        emailFilterRequest.setMoveTo(moveTo);
        emailFilterRequest.setMarkAsRead(markAsRead);
        emailFilterRequest.setMarkAsStarred(markAsStarred);
        emailFilterRequest.setDeleteMsg(deleteMsg);
        filtersModel.editFilter(filterId, emailFilterRequest);
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
        moveToCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                deleteMsgCheckBox.setChecked(false);
            }
        });
        deleteMsgCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                moveToCheckBox.setChecked(false);
            }
            disableOptions(isChecked);
        });
        addConditionButton.setOnClickListener(v -> addCondition());
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

    private void disableOptions(boolean state) {
        if (state) {
            markAsReadCheckBox.setChecked(false);
            markAsStarredCheckBox.setChecked(false);
        }
        markAsReadCheckBox.setEnabled(!state);
        markAsStarredCheckBox.setEnabled(!state);
    }

    class ConditionViews {
        private final View rootView;
        private final Spinner parameterSpinner;
        private final Spinner conditionSpinner;
        private final EditText editText;
        private final View closeView;

        private ConditionViews(View view) {
            rootView = view;
            parameterSpinner = view.findViewById(R.id.parameter_spinner);
            conditionSpinner = view.findViewById(R.id.condition_spinner);
            editText = view.findViewById(R.id.text_edit_text);
            closeView = view.findViewById(R.id.delete_button_image_view);
            ArrayAdapter<String> parametersAdapter = new ArrayAdapter<>(
                    EditFilterActivity.this,
                    R.layout.item_domain_spinner,
                    filterParameterEntries
            );
            parameterSpinner.setAdapter(parametersAdapter);
            ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<>(
                    EditFilterActivity.this,
                    R.layout.item_domain_spinner,
                    filterConditionEntries
            );
            conditionSpinner.setAdapter(conditionsAdapter);
            closeView.setOnClickListener(v -> {
                if (conditionViewsList.size() <= 1) {
                    return;
                }
                conditionViewsList.remove(this);
                conditionsHolder.removeView(rootView);
                updateConditionCloseVisibility();
            });
        }

        private String getSelectedParameter() {
            return filterParameterValues[parameterSpinner.getSelectedItemPosition()];
        }

        private String getSelectedCondition() {
            return filterConditionValues[conditionSpinner.getSelectedItemPosition()];
        }
    }
}
