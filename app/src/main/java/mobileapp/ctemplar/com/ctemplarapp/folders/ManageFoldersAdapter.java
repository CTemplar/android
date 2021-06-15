package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;

public class ManageFoldersAdapter extends RecyclerView.Adapter<ManageFoldersViewHolder> {
    private final List<FoldersResult> items = new ArrayList<>();

    ManageFoldersAdapter() {

    }

    @NonNull
    @Override
    public ManageFoldersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folders_holder, viewGroup, false);

        return new ManageFoldersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManageFoldersViewHolder holder, int position) {
        final FoldersResult folder = items.get(position);
        holder.txtName.setText(folder.getName());
        final int folderColor = Color.parseColor(folder.getColor());
        holder.icoFolder.setColorFilter(folderColor, PorterDuff.Mode.SRC_IN);
        holder.root.setOnClickListener(v -> {
            Intent editFolderIntent = new Intent(holder.root.getContext(), EditFolderActivity.class);
            editFolderIntent.putExtra(EditFolderActivity.ARG_ID, folder.getId());
            editFolderIntent.putExtra(EditFolderActivity.ARG_NAME, folder.getName());
            holder.root.getContext().startActivity(editFolderIntent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<FoldersResult> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public FoldersResult removeAt(int position) {
        FoldersResult deletedFolder = items.remove(position);
        notifyItemRemoved(position);
        return deletedFolder;
    }

    public void restoreItem(int position, FoldersResult foldersResult) {
        items.add(position, foldersResult);
        notifyItemInserted(position);
    }
}
