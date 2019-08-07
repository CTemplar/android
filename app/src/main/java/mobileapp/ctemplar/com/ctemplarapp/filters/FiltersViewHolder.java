package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class FiltersViewHolder extends RecyclerView.ViewHolder {
    View root;
    TextView name;

    FiltersViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        name = itemView.findViewById(R.id.item_filter_name);
    }
}
