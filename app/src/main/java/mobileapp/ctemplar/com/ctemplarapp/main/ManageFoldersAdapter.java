package mobileapp.ctemplar.com.ctemplarapp.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;

public class ManageFoldersAdapter extends RecyclerView.Adapter<ManageFoldersViewHolder> {

    private List<FoldersResult> foldersList;

    ManageFoldersAdapter(List<FoldersResult> foldersList) {
        this.foldersList = foldersList;
    }

    @NonNull
    @Override
    public ManageFoldersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folders_holder, viewGroup, false);

        return new ManageFoldersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageFoldersViewHolder holder, int position) {
        final FoldersResult foldersResult = foldersList.get(position);
        holder.txtName.setText(foldersResult.getName());
    }

    @Override
    public int getItemCount() {
        return foldersList.size();
    }
}
