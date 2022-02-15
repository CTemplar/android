package mobileapp.ctemplar.com.ctemplarapp.folders;

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

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityManageFoldersBinding;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.repository.AccountTypeManager;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PageableDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.folders.CustomFolderDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;

import java.util.List;

public class ManageFoldersActivity extends BaseActivity {
    private ActivityManageFoldersBinding binding;
    private ManageFoldersViewModel manageFoldersModel;
    private final ManageFoldersAdapter manageFoldersAdapter = new ManageFoldersAdapter();
    private boolean isPrime = false;

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

        manageFoldersModel = new ViewModelProvider(this).get(ManageFoldersViewModel.class);
        manageFoldersModel.getCustomFoldersLiveData().observe(this, this::handleCustomFolders);
        manageFoldersModel.getDeletingStatus().observe(this, this::handleDeletingStatus);
        manageFoldersModel.getMySelfResponse().observe(this, myselfResponse -> {
            isPrime = myselfResponse != null && myselfResponse.getResult()[0].isPrime();
        });
        getCustomFolders();
        manageFoldersModel.getMyselfData();
        binding.manageFoldersFooter.getRoot().setVisibility(View.GONE);
        binding.manageFoldersFooter.footerButton.setOnClickListener(v -> addFolder());
        binding.addLayout.setOnClickListener(v -> addFolder());
    }

    private void handleDeletingStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            ToastUtils.showToast(getApplicationContext(), R.string.txt_folder_not_deleted);
            return;
        }
        ToastUtils.showToast(getApplicationContext(), R.string.txt_folder_deleted);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCustomFolders();
    }

    private void getCustomFolders() {
        manageFoldersModel.getCustomFolders(200, 0);
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
                                (dialog, which) -> manageFoldersModel.deleteFolder(deletedFolder.getId()))
                        .setNeutralButton(R.string.btn_cancel, (dialog, which)
                                -> adapter.restoreItem(deletedIndex, deletedFolder))
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(binding.foldersRecyclerView);
    }

    private void addFolder() {
        int foldersCount = manageFoldersAdapter.getItemCount();
        boolean canCreate = AccountTypeManager.createFolder(getBaseContext(), foldersCount, isPrime);
        if (canCreate) {
            startActivity(new Intent(this, AddFolderActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
