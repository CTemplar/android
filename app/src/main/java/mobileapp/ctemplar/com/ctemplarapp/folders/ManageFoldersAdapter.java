package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
    public void onBindViewHolder(@NonNull final ManageFoldersViewHolder holder, int position) {
        final FoldersResult folder = foldersList.get(position);
        holder.txtName.setText(folder.getName());
        final int folderColor = Color.parseColor(folder.getColor());
        holder.icoFolder.setColorFilter(folderColor, PorterDuff.Mode.SRC_IN);
        holder.root.setOnClickListener(v -> {
            Intent editFolder = new Intent(holder.root.getContext(), EditFolderActivity.class);
            editFolder.putExtra(EditFolderActivity.ARG_ID, folder.getId());
            editFolder.putExtra(EditFolderActivity.ARG_NAME, folder.getName());
            holder.root.getContext().startActivity(editFolder);
        });
    }

    @Override
    public int getItemCount() {
        return foldersList.size();
    }

    public FoldersResult removeAt(int position) {
        FoldersResult deletedFolder = foldersList.remove(position);
        notifyItemRemoved(position);
        return deletedFolder;
    }

    public void restoreItem(int position, FoldersResult foldersResult) {
        foldersList.add(position, foldersResult);
        notifyItemInserted(position);
    }
}
