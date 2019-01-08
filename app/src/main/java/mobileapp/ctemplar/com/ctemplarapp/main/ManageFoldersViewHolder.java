package mobileapp.ctemplar.com.ctemplarapp.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class ManageFoldersViewHolder extends RecyclerView.ViewHolder {
    public View root;
    public TextView txtName;

    public ManageFoldersViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        txtName = itemView.findViewById(R.id.item_folder_holder_name);
    }
}
