package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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

public class EditFolderActivity extends BaseActivity {

    private EditFolderViewModel editFolderModel;
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    private Long folderId;

    @BindView(R.id.activity_edit_folder_input)
    EditText editTextNameFolder;
    @BindView(R.id.activity_edit_folder_colors_layout)
    RadioButtonTableLayout radioGroupColor;
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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                .observe(this, new Observer<ResponseStatus>() {
                    @Override
                    public void onChanged(@Nullable ResponseStatus responseStatus) {
                        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                            Toast.makeText(getApplicationContext(), "Folder not deleted", Toast.LENGTH_SHORT).show();
                        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                            Toast.makeText(getApplicationContext(), "Folder deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        editFolderModel.getResponseStatus()
                .observe(this, new Observer<ResponseStatus>() {
                    @Override
                    public void onChanged(@Nullable ResponseStatus responseStatus) {
                        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                            Toast.makeText(getApplicationContext(), "Folder not edited", Toast.LENGTH_SHORT).show();
                        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                            Toast.makeText(getApplicationContext(), "Folder edited", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        radioGroupColor.setActive(firstRadioButton);
    }

    private void editFolder() {
        String folderName = editTextNameFolder.getText().toString();
        String folderColor = "";

        if (radioGroupColor.getCheckedRadioButtonId() != -1) {
            int selectedColor = radioGroupColor.getCheckedRadioButtonId();
            folderColor = AddFolderActivity.getPickerColor(selectedColor);
        }

        if (folderName.matches("^[a-zA-Z0-9]*$") && folderName.length() > 3 && folderName.length() < 31) {
            editFolderModel.editFolder(folderId, folderName, folderColor);
            editTextNameFolder.setError(null);
            finish();

        } else {
            editTextNameFolder.setError("Folder name must be between 4 and 30 letters and digits");
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
