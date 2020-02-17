package com.ctemplar.app.fdroid.folders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctemplar.app.fdroid.R;

public class ManageFoldersViewHolder extends RecyclerView.ViewHolder {
    public View root;
    public TextView txtName;
    ImageView icoFolder;

    public ManageFoldersViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        txtName = itemView.findViewById(R.id.item_folder_holder_name);
        icoFolder = itemView.findViewById(R.id.item_folder_holder_ico);
    }
}
