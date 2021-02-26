package com.ctemplar.app.fdroid.folders;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class EditFolderActivity extends BaseActivity {
    @BindView(R.id.activity_edit_folder_input)
    EditText editTextNameFolder;

    @BindView(R.id.activity_edit_folder_colors_layout)
    RadioButtonTableLayout radioGroupLayout;

    @BindView(R.id.folder_color_1)
    RadioButton firstRadioButton;

    @BindInt(R.integer.restriction_folder_name_min)
    int FOLDER_NAME_MIN;

    @BindInt(R.integer.restriction_folder_name_max)
    int FOLDER_NAME_MAX;

    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";

    private EditFolderViewModel editFolderModel;
    private int folderId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_folder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.activity_edit_folder_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        folderId = getIntent().getIntExtra(ARG_ID, -1);
        if (folderId == -1) {
            return;
        }
        String folderName = getIntent().getStringExtra(ARG_NAME);
        if (folderName != null) {
            editTextNameFolder.setText(folderName);
            editTextNameFolder.setSelection(editTextNameFolder.getText().length());
        }
        editFolderModel = new ViewModelProvider(this).get(EditFolderViewModel.class);
        setListeners();
        AddFolderActivity.fillPalette(this, radioGroupLayout);
        radioGroupLayout.setActive(firstRadioButton);
    }

    private void editFolder() {
        String folderName = EditTextUtils.getText(editTextNameFolder);
        String folderColor = "";

        if (radioGroupLayout.getCheckedRadioButtonId() != -1) {
            int selectedColor = radioGroupLayout.getCheckedRadioButtonId();
            folderColor = AddFolderActivity.getPickerColor(selectedColor);
        }
        if (EditTextUtils.isTextLength(folderName, FOLDER_NAME_MIN, FOLDER_NAME_MAX)) {
            editFolderModel.editFolder(folderId, folderName, folderColor);
            editTextNameFolder.setError(null);
        } else {
            editTextNameFolder.setError(getString(R.string.txt_folder_name_hint));
        }
    }

    private void setListeners() {
        editFolderModel.getDeletingStatus().observe(this, responseStatus -> {
            if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(this, R.string.txt_folder_not_deleted, Toast.LENGTH_SHORT).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(this, R.string.txt_folder_deleted, Toast.LENGTH_SHORT).show();
            }
            finish();
        });
        editFolderModel.getEditResponse().observe(this, foldersResult -> {
            if (foldersResult == null) {
                return;
            }
            Toast.makeText(this, R.string.txt_folder_edited, Toast.LENGTH_SHORT).show();
            finish();
        });
        editFolderModel.getEditErrorResponse().observe(this, this::handleResponseError);
    }

    private void handleResponseError(@Nullable String errorText) {
        if (errorText == null) {
            return;
        }
        Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.activity_edit_folder_delete)
    public void OnClickFolderDelete() {
        editFolderModel.deleteFolderById(folderId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_folder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_folder:
                editFolder();
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
