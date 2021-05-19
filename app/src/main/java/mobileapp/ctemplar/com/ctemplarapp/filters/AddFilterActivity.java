package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterConditionRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class AddFilterActivity extends BaseActivity {
    @BindView(R.id.activity_add_filter_name_edit_text)
    TextInputEditText filterNameEditText;

    @BindView(R.id.activity_add_filter_conditions_holder)
    ViewGroup conditionsHolder;

    @BindView(R.id.activity_add_filter_add_condition)
    Button addConditionButton;

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
    private final List<ConditionViews> conditionViewsList = new ArrayList<>();

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
        conditionsHolder.removeAllViews();
        conditionViewsList.clear();
        addCondition();
        filtersModel.getFoldersResponse().observe(this, this::handleCustomFolders);
        getCustomFolders();
        addListeners();
    }

    private void addCondition() {
        View view = getLayoutInflater().inflate(R.layout.filter_condition, conditionsHolder, false);
        ConditionViews conditionViews = new ConditionViews(view);
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
        if (!EditTextUtils.isTextValid(filterName) || !EditTextUtils.isTextLength(filterName, 4, 30)) {
            filterNameEditText.setError(getString(R.string.txt_filter_name_hint));
            return;
        }
        String selectedFolder = filterFolderSpinner.getSelectedItem().toString();
        boolean isMoveTo = moveToCheckBox.isChecked();
        boolean markAsRead = markAsReadCheckBox.isChecked();
        boolean markAsStarred = markAsStarredCheckBox.isChecked();

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
        addConditionButton.setOnClickListener(v -> addCondition());
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
                    AddFilterActivity.this,
                    R.layout.item_domain_spinner,
                    filterParameterEntries
            );
            parameterSpinner.setAdapter(parametersAdapter);
            ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<>(
                    AddFilterActivity.this,
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
