package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;

public class AddFolderActivity extends BaseActivity {

    private AddFolderViewModel addFolderModel;

    @BindView(R.id.activity_add_folder_action_add)
    Button buttonAddFolder;
    @BindView(R.id.activity_add_folder_colors_layout)
    RadioButtonTableLayout radioGroupColor;
    @BindView(R.id.folder_color_1)
    RadioButton firstRadioButton;
    @BindView(R.id.activity_add_folder_input)
    EditText editTextFolderName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_folder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.activity_add_folder_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addFolderModel = ViewModelProviders.of(this).get(AddFolderViewModel.class);
        addFolderModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                    Toast.makeText(getApplicationContext(), "Folder not created", Toast.LENGTH_SHORT).show();
                } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                    Toast.makeText(getApplicationContext(), "Folder created", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
        editTextFolderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextFolderName.getText().toString().length() > 3) {
                    buttonAddFolder.setBackgroundResource(R.color.colorDarkBlue2);
                } else {
                    buttonAddFolder.setBackgroundResource(R.color.colorGreyLight3);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        buttonAddFolder.setBackgroundResource(R.color.colorGreyLight3);
        radioGroupColor.setActive(firstRadioButton);
    }

    @OnClick(R.id.activity_add_folder_action_add)
    public void OnClickAddFolder() {
        String folderName = editTextFolderName.getText().toString();
        String folderColor = "#7272a8";

        if (radioGroupColor.getCheckedRadioButtonId() != -1) {
            int selectedColor = radioGroupColor.getCheckedRadioButtonId();

            if (selectedColor == R.id.folder_color_2) {
                folderColor = "#d05859";
            } else if (selectedColor == R.id.folder_color_3) {
                folderColor = "#c26cc7";
            } else if (selectedColor == R.id.folder_color_4) {
                folderColor = "#7568d1";
            } else if (selectedColor == R.id.folder_color_5) {
                folderColor = "#6aa9d2";
            } else if (selectedColor == R.id.folder_color_6) {
                folderColor = "#5fc7b8";
            } else if (selectedColor == R.id.folder_color_7) {
                folderColor = "#72bb74";
            } else if (selectedColor == R.id.folder_color_8) {
                folderColor = "#72bb74";
            } else if (selectedColor == R.id.folder_color_9) {
                folderColor = "#e6c14c";
            } else if (selectedColor == R.id.folder_color_10) {
                folderColor = "#e6994d";
            } else if (selectedColor == R.id.folder_color_11) {
                folderColor = "#cf7e7d";
            } else if (selectedColor == R.id.folder_color_12) {
                folderColor = "#c893cb";
            } else if (selectedColor == R.id.folder_color_13) {
                folderColor = "#9c94d0";
            } else if (selectedColor == R.id.folder_color_14) {
                folderColor = "#a9c4d5";
            }
        }

        if (folderName.matches("^[a-zA-Z0-9]*$") && folderName.length() > 3 && folderName.length() < 31) {
            addFolderModel.addFolder(folderName, folderColor);
            editTextFolderName.setError(null);
        } else {
            editTextFolderName.setError("Folder name must be between 4 and 30 letters and digits");
        }
    }

    @OnClick(R.id.activity_add_folder_action_cancel)
    public void OnClickCancel() {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
