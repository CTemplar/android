package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;

public class ManageFoldersActivity extends BaseActivity {

    private ManageFoldersViewModel manageFoldersModel;

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

    private ManageFoldersAdapter manageFoldersAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_folders;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.activity_manage_folders_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        setupSwiperForRecyclerView();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        manageFoldersModel = ViewModelProviders.of(this).get(ManageFoldersViewModel.class);
        manageFoldersModel.getFoldersResponse()
                .observe(this, new Observer<FoldersResponse>() {
                    @Override
                    public void onChanged(@Nullable FoldersResponse foldersResponse) {
                        handleFoldersList(foldersResponse);
                    }
                });
        manageFoldersModel.getDeletingStatus()
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
        getCustomFolders();
        footerAddFolder.setVisibility(View.GONE);
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

        manageFoldersAdapter = new ManageFoldersAdapter(foldersResults);
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
                        .setTitle("Delete folder?")
                        .setMessage("Are you sure you want to delete folder?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        manageFoldersModel.deleteFolder(deletedFolder);
                                    }
                                }
                        )
                        .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.restoreItem(deletedIndex, deletedFolder);
                            }
                        })
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
        Intent addFolder = new Intent(this, AddFolderActivity.class);
        startActivity(addFolder);
    }

    @OnClick(R.id.activity_manage_folders_add_layout)
    public void OnClickAddFolder() {
        Intent addFolder = new Intent(this, AddFolderActivity.class);
        startActivity(addFolder);
    }
}
