package com.ctemplar.app.fdroid.folders;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityManageFoldersBinding;
import com.ctemplar.app.fdroid.main.RecycleDeleteSwiper;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import retrofit2.Response;

public class ManageFoldersActivity extends BaseActivity {
    private ActivityManageFoldersBinding binding;
    private ManageFoldersViewModel viewModel;
    private final ManageFoldersAdapter manageFoldersAdapter = new ManageFoldersAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageFoldersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.foldersRecyclerView.setLayoutManager(mLayoutManager);
        setupSwiperForRecyclerView();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.foldersRecyclerView.getContext(), mLayoutManager.getOrientation());
        binding.foldersRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.foldersRecyclerView.setAdapter(manageFoldersAdapter);

        viewModel = new ViewModelProvider(this).get(ManageFoldersViewModel.class);
        viewModel.getCustomFoldersLiveData().observe(this, this::handleCustomFolders);
        getCustomFolders();
        binding.manageFoldersFooter.getRoot().setVisibility(View.GONE);
        binding.manageFoldersFooter.footerButton.setOnClickListener(v -> addFolder());
        binding.addLayout.setOnClickListener(v -> addFolder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCustomFolders();
    }

    private void getCustomFolders() {
        viewModel.getCustomFolders(200, 0);
    }

    private void handleCustomFolders(DTOResource<PageableDTO<CustomFolderDTO>> resource) {
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        List<CustomFolderDTO> results = resource.getDto().getResults();
        if (results == null || results.size() == 0) {
            return;
        }
        binding.iconEmptyImageView.setVisibility(View.GONE);
        binding.titleEmptyTextView.setVisibility(View.GONE);
        binding.addLayout.setVisibility(View.GONE);
        binding.manageFoldersFooter.getRoot().setVisibility(View.VISIBLE);
        manageFoldersAdapter.setItems(results);
    }

    private void setupSwiperForRecyclerView() {
        RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(this) {
            @Override
            public void onSwiped(final @NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final ManageFoldersAdapter adapter = manageFoldersAdapter;
                if (adapter == null) {
                    return;
                }
                final int deletedIndex = viewHolder.getAdapterPosition();
                final CustomFolderDTO deletedFolder = adapter.removeAt(deletedIndex);
                new AlertDialog.Builder(ManageFoldersActivity.this)
                        .setTitle(R.string.txt_delete_folder_quest_title)
                        .setMessage(R.string.txt_delete_folder_quest_message)
                        .setPositiveButton(getString(R.string.btn_delete).toUpperCase(),
                                (dialog, which) -> viewModel.deleteFolder(deletedFolder.getId())
                                        .observe(ManageFoldersActivity.this,
                                                resource -> handleDeleteFolderResponse(resource)))
                        .setNeutralButton(R.string.btn_cancel, (dialog, which)
                                -> adapter.restoreItem(deletedIndex, deletedFolder))
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(binding.foldersRecyclerView);
    }

    private void handleDeleteFolderResponse(DTOResource<Response<Void>> resource) {
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        ToastUtils.showToast(this, R.string.txt_folder_deleted);
    }

    private void addFolder() {
        startActivity(new Intent(this, AddFolderActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
