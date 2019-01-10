package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
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

    private ManageFoldersAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_folders;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

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
        manageFoldersModel.getFolders(200, 0);
    }

    private void handleFoldersList(@Nullable FoldersResponse foldersResponse) {
        if (foldersResponse == null) {
            return;
        }

        List<FoldersResult> foldersResults = new ArrayList<>(foldersResponse.getFoldersList());

        imgEmpty.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        frameCompose.setVisibility(View.GONE);

        adapter = new ManageFoldersAdapter(foldersResults);
        recyclerView.setAdapter(adapter);
    }
}
