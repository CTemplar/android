package mobileapp.ctemplar.com.ctemplarapp.folders;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class EditFolderActivity extends BaseActivity {

    private EditFolderViewModel editFolderModel;
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    private Long folderId;

    @BindView(R.id.activity_edit_folder_input)
    EditText editTextNameFolder;

    @BindView(R.id.activity_edit_folder_colors_layout)
    RadioButtonTableLayout radioGroupLayout;

    @BindView(R.id.folder_color_1)
    RadioButton firstRadioButton;

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

        folderId = getIntent().getLongExtra(ARG_ID, -1);
        if (folderId == -1) {
            return;
        }
        String folderName = getIntent().getStringExtra(ARG_NAME);
        if (folderName != null) {
            editTextNameFolder.setText(folderName);
        }
        editFolderModel = ViewModelProviders.of(this).get(EditFolderViewModel.class);
        editFolderModel.getDeletingStatus()
                .observe(this, responseStatus -> {
                    if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_folder_not_deleted), Toast.LENGTH_SHORT).show();
                    } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_folder_deleted), Toast.LENGTH_SHORT).show();
                    }
                });
        editFolderModel.getResponseStatus()
                .observe(this, responseStatus -> {
                    if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_folder_not_edited), Toast.LENGTH_SHORT).show();
                    } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_folder_edited), Toast.LENGTH_SHORT).show();
                    }
                });
        AddFolderActivity.fillPalette(this, radioGroupLayout);
        radioGroupLayout.setActive(firstRadioButton);
    }

    private void editFolder() {
        String folderName = editTextNameFolder.getText().toString();
        String folderColor = "";

        if (radioGroupLayout.getCheckedRadioButtonId() != -1) {
            int selectedColor = radioGroupLayout.getCheckedRadioButtonId();
            folderColor = AddFolderActivity.getPickerColor(selectedColor);
        }

        if (EditTextUtils.isTextValid(folderName) && EditTextUtils.isTextLength(folderName, 4, 30)) {
            editFolderModel.editFolder(folderId, folderName, folderColor);
            editTextNameFolder.setError(null);
            finish();
        } else {
            editTextNameFolder.setError(getResources().getString(R.string.txt_folder_name_hint));
        }
    }

    @OnClick(R.id.activity_edit_folder_delete)
    public void OnClickFolderDelete() {
        editFolderModel.deleteFolderById(folderId);
        onBackPressed();
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
