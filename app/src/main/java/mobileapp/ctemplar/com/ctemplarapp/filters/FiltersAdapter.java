package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FilterResult;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersViewHolder> {
    private List<FilterResult> filterList;

    FiltersAdapter(List<FilterResult> filterList) {
        this.filterList = filterList;
    }

    @NonNull
    @Override
    public FiltersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filters_holder, viewGroup, false);
        return new FiltersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltersViewHolder holder, int position) {
        FilterResult filterResult = filterList.get(position);
        String name = filterResult.getName();
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }
}
