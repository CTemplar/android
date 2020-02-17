package com.ctemplar.app.fdroid.filters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ctemplar.app.fdroid.R;

public class FiltersViewHolder extends RecyclerView.ViewHolder {
    View root;
    TextView name;

    FiltersViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        name = itemView.findViewById(R.id.item_filter_name);
    }
}
