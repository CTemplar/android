package mobileapp.ctemplar.com.ctemplarapp.filters;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CustomFilterRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class EditFilterActivity extends BaseActivity {
    private FiltersViewModel filtersModel;

    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    public static final String ARG_PARAMETER = "parameter";
    public static final String ARG_CONDITION = "condition";
    public static final String ARG_FILTER_TEXT = "filter_text";
    public static final String ARG_MOVE_TO = "move_to";
    public static final String ARG_FOLDER = "folder";
    public static final String ARG_AS_READ = "mark_as_read";
    public static final String ARG_AS_STARRED = "mark_as_starred";

    private static long filterId;
    private static String filterFolder;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filtersModel = ViewModelProviders.of(this).get(FiltersViewModel.class);

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

        String[] filterParameters = getResources().getStringArray(R.array.filter_parameter_list);
        ArrayAdapter<String> parametersAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterParameters
        );
        parameterSpinner.setAdapter(parametersAdapter);
        List<String> filterParameterList = Arrays.asList(filterParameters);
        int editFilterParameter = filterParameterList.indexOf(filterParameter);
        parameterSpinner.setSelection(editFilterParameter);

        String[] filterConditions = getResources().getStringArray(R.array.filter_condition_list);
        ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterConditions
        );
        conditionSpinner.setAdapter(conditionsAdapter);
        List<String> filterConditionList = Arrays.asList(filterConditions);
        int editFilterCondition = filterConditionList.indexOf(filterCondition);
        conditionSpinner.setSelection(editFilterCondition);

        filtersModel.getFoldersResponse().observe(this, foldersResponse -> {
            if (foldersResponse != null) {
                handleCustomFolders(foldersResponse);
            }
        });
        filtersModel.getEditFilterResponseStatus().observe(this, this::handleEditFilterStatus);
        filtersModel.getDeleteFilterResponseStatus().observe(this, this::handleFilterDeletingStatus);
        getCustomFolders();
        addListeners();
    }

    private void handleEditFilterStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_filter_not_edited), Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_filter_edited), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void handleFilterDeletingStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_filter_not_deleted), Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_filter_deleted), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCustomFolders(FoldersResponse foldersResponse) {
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

    public void editFilter() {
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
    }

    @OnClick(R.id.activity_edit_filter_delete)
    public void onClickFilterDelete() {
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
                editFilter();
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
