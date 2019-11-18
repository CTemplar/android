package mobileapp.ctemplar.com.ctemplarapp.filters;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
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

public class AddFilterActivity extends BaseActivity {
    private FiltersViewModel filtersModel;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filtersModel = ViewModelProviders.of(this).get(FiltersViewModel.class);

        Toolbar toolbar = findViewById(R.id.activity_add_filter_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String[] filterParameters = getResources().getStringArray(R.array.filter_parameter_list);
        ArrayAdapter<String> parametersAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterParameters
        );
        parameterSpinner.setAdapter(parametersAdapter);

        String[] filterConditions = getResources().getStringArray(R.array.filter_condition_list);
        ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                filterConditions
        );
        conditionSpinner.setAdapter(conditionsAdapter);

        filtersModel.getFoldersResponse().observe(this, foldersResponse -> {
            if (foldersResponse != null) {
                handleCustomFolders(foldersResponse);
            }
        });
        getCustomFolders();
        addListeners();
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

        filtersModel.getAddFilterResponseStatus().observe(this, this::handleAddFilterStatus);
    }

    private void handleAddFilterStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_filter_not_created), Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_filter_created), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void getCustomFolders() {
        filtersModel.getFolders(200, 0);
    }

    @OnClick(R.id.activity_add_filter_action_submit)
    public void onClickSubmit() {
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
        filtersModel.addFilter(customFilterRequest);
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

    @OnClick(R.id.activity_add_filter_action_cancel)
    public void onClickCancel() {
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
