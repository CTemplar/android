package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class AddFolderActivity extends BaseActivity {

    final private static String[] PICK_COLORS = new String[] {
            "#ced4da", "#868e96", "#212529", "#da77f2", "#be4bdb", "#8e44ad", "#f783ac", "#e64980", "#a61e4d",
            "#748ffc", "#4c6ef5", "#364fc7", "#9775fa", "#7950f2", "#5f3dc4", "#ff8787", "#fa5252", "#c0392b",
            "#4dabf7", "#3498db", "#1864ab", "#2ecc71", "#27ae60", "#16a085", "#ffd43b", "#fab005", "#e67e22",
            "#3bc9db", "#15aabf", "#0b7285", "#a9e34b", "#82c91e", "#5c940d", "#f39c12", "#fd7e14", "#e74c3c"
    };

    private AddFolderViewModel addFolderModel;

    @BindView(R.id.activity_add_folder_action_add)
    Button buttonAddFolder;

    @BindView(R.id.activity_add_folder_colors_layout)
    RadioButtonTableLayout radioGroupLayout;

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
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        addFolderModel = ViewModelProviders.of(this).get(AddFolderViewModel.class);
        addFolderModel.getResponseStatus().observe(this, responseStatus -> {
            if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_folder_not_created), Toast.LENGTH_SHORT).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_folder_created), Toast.LENGTH_SHORT).show();
                onBackPressed();
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
        fillPalette(this, radioGroupLayout);
        buttonAddFolder.setBackgroundResource(R.color.colorGreyLight3);
        radioGroupLayout.setActive(firstRadioButton);
    }

    @OnClick(R.id.activity_add_folder_action_add)
    public void OnClickAddFolder() {
        String folderName = editTextFolderName.getText().toString();
        String folderColor = "";

        if (radioGroupLayout.getCheckedRadioButtonId() != -1) {
            int selectedColor = radioGroupLayout.getCheckedRadioButtonId();
            folderColor = getPickerColor(selectedColor);
        }

        if (EditTextUtils.isTextValid(folderName) && EditTextUtils.isTextLength(folderName, 4, 30)) {
            addFolderModel.addFolder(folderName, folderColor);
            editTextFolderName.setError(null);
        } else {
            editTextFolderName.setError(getResources().getString(R.string.txt_folder_name_hint));
        }
    }

    public static String getPickerColor(int selectedColor) {
        String folderColor = PICK_COLORS[0];

        switch (selectedColor) {
            case R.id.folder_color_2:
                folderColor = PICK_COLORS[1];
                break;
            case R.id.folder_color_3:
                folderColor = PICK_COLORS[2];
                break;
            case R.id.folder_color_4:
                folderColor = PICK_COLORS[3];
                break;
            case R.id.folder_color_5:
                folderColor = PICK_COLORS[4];
                break;
            case R.id.folder_color_6:
                folderColor = PICK_COLORS[5];
                break;
            case R.id.folder_color_7:
                folderColor = PICK_COLORS[6];
                break;
            case R.id.folder_color_8:
                folderColor = PICK_COLORS[7];
                break;
            case R.id.folder_color_9:
                folderColor = PICK_COLORS[8];
                break;
            case R.id.folder_color_10:
                folderColor = PICK_COLORS[9];
                break;
            case R.id.folder_color_11:
                folderColor = PICK_COLORS[10];
                break;
            case R.id.folder_color_12:
                folderColor = PICK_COLORS[11];
                break;
            case R.id.folder_color_13:
                folderColor = PICK_COLORS[12];
                break;
            case R.id.folder_color_14:
                folderColor = PICK_COLORS[13];
                break;
            case R.id.folder_color_15:
                folderColor = PICK_COLORS[14];
                break;
            case R.id.folder_color_16:
                folderColor = PICK_COLORS[15];
                break;
            case R.id.folder_color_17:
                folderColor = PICK_COLORS[16];
                break;
            case R.id.folder_color_18:
                folderColor = PICK_COLORS[17];
                break;
            case R.id.folder_color_19:
                folderColor = PICK_COLORS[18];
                break;
            case R.id.folder_color_20:
                folderColor = PICK_COLORS[19];
                break;
            case R.id.folder_color_21:
                folderColor = PICK_COLORS[20];
                break;
            case R.id.folder_color_22:
                folderColor = PICK_COLORS[21];
                break;
            case R.id.folder_color_23:
                folderColor = PICK_COLORS[22];
                break;
            case R.id.folder_color_24:
                folderColor = PICK_COLORS[23];
                break;
            case R.id.folder_color_25:
                folderColor = PICK_COLORS[24];
                break;
            case R.id.folder_color_26:
                folderColor = PICK_COLORS[25];
                break;
            case R.id.folder_color_27:
                folderColor = PICK_COLORS[26];
                break;
            case R.id.folder_color_28:
                folderColor = PICK_COLORS[27];
                break;
            case R.id.folder_color_29:
                folderColor = PICK_COLORS[28];
                break;
            case R.id.folder_color_30:
                folderColor = PICK_COLORS[29];
                break;
            case R.id.folder_color_31:
                folderColor = PICK_COLORS[30];
                break;
            case R.id.folder_color_32:
                folderColor = PICK_COLORS[31];
                break;
            case R.id.folder_color_33:
                folderColor = PICK_COLORS[32];
                break;
            case R.id.folder_color_34:
                folderColor = PICK_COLORS[33];
                break;
            case R.id.folder_color_35:
                folderColor = PICK_COLORS[34];
                break;
            case R.id.folder_color_36:
                folderColor = PICK_COLORS[35];
                break;
        }

        return folderColor;
    }

    public static void fillPalette(Context ctx, RadioButtonTableLayout radioButtonTableLayout) {
        int backgroundColorNum = 0;
        for (int radioTableCount = 0; radioTableCount < radioButtonTableLayout.getChildCount(); ++radioTableCount) {
            TableRow tableRow = (TableRow) radioButtonTableLayout.getChildAt(radioTableCount);
            for (int radioButtonCount = 0; radioButtonCount < tableRow.getChildCount(); radioButtonCount += 2) {
                RadioButton radioButton = (RadioButton) tableRow.getChildAt(radioButtonCount);
                int radioButtonColor = Color.parseColor(PICK_COLORS[backgroundColorNum++]);
                Drawable radioBackground = ctx.getResources().getDrawable(R.drawable.folder_picker_color);
                radioBackground.setColorFilter(radioButtonColor, PorterDuff.Mode.SRC_IN);
                radioButton.setBackground(radioBackground);
            }
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
