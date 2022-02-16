package com.ctemplar.app.fdroid.folders;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityEditFolderBinding;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import retrofit2.Response;

public class EditFolderActivity extends BaseActivity {
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";

    private ActivityEditFolderBinding binding;
    private ManageFoldersViewModel viewModel;

    private int folderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
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
            binding.folderNameEditText.setText(folderName);
            binding.folderNameEditText.setSelection(EditTextUtils.getText(
                    binding.folderNameEditText).length());
        }
        viewModel = new ViewModelProvider(this).get(ManageFoldersViewModel.class);
        AddFolderActivity.fillPalette(this, binding.folderColorsLayout);
        binding.folderColorsLayout.setActive(binding.folderColor1);
        binding.deleteFolderButton.setOnClickListener(v -> {
            viewModel.deleteFolder(folderId).observe(this, this::handleDeleteFolderResponse);
            showProgressBar(true);
        });
        showProgressBar(false);
    }

    private void editFolder() {
        String folderName = EditTextUtils.getText(binding.folderNameEditText);
        String folderColor = "";
        if (binding.folderColorsLayout.getCheckedRadioButtonId() != -1) {
            int selectedColor = binding.folderColorsLayout.getCheckedRadioButtonId();
            folderColor = AddFolderActivity.getPickerColor(selectedColor);
        }
        if (EditTextUtils.isTextLength(folderName,
                getResources().getInteger(R.integer.restriction_folder_name_min),
                getResources().getInteger(R.integer.restriction_folder_name_max))) {
            viewModel.editFolder(folderId, folderName, folderColor).observe(this,
                    this::handleEditFolderResponse);
            binding.folderNameEditText.setError(null);
            showProgressBar(true);
        } else {
            binding.folderNameEditText.setError(getString(R.string.txt_folder_name_hint));
        }
    }

    private void handleDeleteFolderResponse(DTOResource<Response<Void>> resource) {
        showProgressBar(false);
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        ToastUtils.showToast(this, R.string.txt_folder_deleted);
        finish();
    }

    private void handleEditFolderResponse(DTOResource<CustomFolderDTO> resource) {
        showProgressBar(false);
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        ToastUtils.showToast(this, R.string.txt_folder_edited);
        finish();
    }

    private void showProgressBar(boolean value) {
        binding.progressBar.setVisibility(value ? View.VISIBLE : View.GONE);
        binding.deleteFolderButton.setEnabled(!value);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
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
