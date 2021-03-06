package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.utils.AccountTypeManager;

public class ManageFoldersActivity extends BaseActivity {
    private ManageFoldersViewModel manageFoldersModel;
    private final ManageFoldersAdapter manageFoldersAdapter = new ManageFoldersAdapter();
    private boolean isPrime = false;

    @BindView(R.id.activity_manage_folders_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.activity_manage_folders_icon_empty)
    ImageView imgEmpty;

    @BindView(R.id.activity_manage_folders_title_empty)
    TextView txtEmpty;

    @BindView(R.id.activity_manage_folders_add_layout)
    FrameLayout frameCompose;

    @BindView(R.id.manager_folders_footer_btn)
    Button footerAddFolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_folders;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.activity_manage_folders_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        setupSwiperForRecyclerView();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        manageFoldersModel = new ViewModelProvider(this).get(ManageFoldersViewModel.class);
        manageFoldersModel.getFoldersResponse().observe(this, this::handleFoldersList);
        manageFoldersModel.getDeletingStatus().observe(this, responseStatus -> {
            if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(getApplicationContext(), getString(R.string.txt_folder_not_deleted), Toast.LENGTH_SHORT).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(getApplicationContext(), getString(R.string.txt_folder_deleted), Toast.LENGTH_SHORT).show();
            }
        });
        manageFoldersModel.getMySelfResponse().observe(this, myselfResponse -> {
            if (myselfResponse != null) {
                isPrime = myselfResponse.getResult()[0].isPrime();
            }
        });
        footerAddFolder.setVisibility(View.GONE);
        getCustomFolders();
        manageFoldersModel.getMyselfData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCustomFolders();
    }

    private void getCustomFolders() {
        manageFoldersModel.getFolders(200, 0);
    }

    private void handleFoldersList(@Nullable FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            return;
        }

        List<FoldersResult> foldersResults = new ArrayList<>(foldersResponse.getFoldersList());
        if (!foldersResults.isEmpty()) {
            imgEmpty.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.GONE);
            frameCompose.setVisibility(View.GONE);
            footerAddFolder.setVisibility(View.VISIBLE);
        }
        manageFoldersAdapter.setItems(foldersResults);
        recyclerView.setAdapter(manageFoldersAdapter);
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
                final FoldersResult deletedFolder = adapter.removeAt(deletedIndex);

                new AlertDialog.Builder(ManageFoldersActivity.this)
                        .setTitle(getString(R.string.txt_delete_folder_quest_title))
                        .setMessage(getString(R.string.txt_delete_folder_quest_message))
                        .setPositiveButton(getString(R.string.btn_delete).toUpperCase(),
                                (dialog, which) -> manageFoldersModel.deleteFolder(deletedFolder))
                        .setNeutralButton(getString(R.string.btn_cancel), (dialog, which)
                                -> adapter.restoreItem(deletedIndex, deletedFolder))
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addFolder() {
        int foldersCount = manageFoldersAdapter.getItemCount();
        boolean canCreate = AccountTypeManager.createFolder(getBaseContext(), foldersCount, isPrime);
        if (canCreate) {
            Intent addFolder = new Intent(this, AddFolderActivity.class);
            startActivity(addFolder);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.manager_folders_footer_btn)
    public void OnClickAddFolderFooter() {
        addFolder();
    }

    @OnClick(R.id.activity_manage_folders_add_layout)
    public void OnClickAddFolder() {
        addFolder();
    }
}
