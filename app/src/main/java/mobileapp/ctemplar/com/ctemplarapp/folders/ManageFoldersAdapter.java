package mobileapp.ctemplar.com.ctemplarapp.folders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemFoldersHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.folders.CustomFolderDTO;

import java.util.ArrayList;
import java.util.List;

public class ManageFoldersAdapter extends RecyclerView.Adapter<ManageFoldersAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;

    private final List<CustomFolderDTO> items = new ArrayList<>();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemFoldersHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<CustomFolderDTO> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public CustomFolderDTO removeAt(int position) {
        CustomFolderDTO deletedFolder = items.remove(position);
        notifyItemRemoved(position);
        return deletedFolder;
    }

    public void restoreItem(int position, CustomFolderDTO customFolderDTO) {
        items.add(position, customFolderDTO);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoldersHolderBinding binding;

        public ViewHolder(ItemFoldersHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(CustomFolderDTO customFolderDTO) {
            binding.itemFolderHolderName.setText(customFolderDTO.getName());
            binding.itemFolderHolderIco.setColorFilter(Color.parseColor(customFolderDTO.getColor()),
                    PorterDuff.Mode.SRC_IN);
            binding.getRoot().setOnClickListener(v -> {
                Intent editFolderIntent = new Intent(context, EditFolderActivity.class);
                editFolderIntent.putExtra(EditFolderActivity.ARG_ID, customFolderDTO.getId());
                editFolderIntent.putExtra(EditFolderActivity.ARG_NAME, customFolderDTO.getName());
                context.startActivity(editFolderIntent);
            });
        }
    }
}
