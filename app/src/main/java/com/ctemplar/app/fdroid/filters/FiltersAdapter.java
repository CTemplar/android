package com.ctemplar.app.fdroid.filters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.response.filters.FilterResult;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {
    private final List<FilterResult> items = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;

    public FiltersAdapter() {
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        context = recyclerView.getContext();
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_filters_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<FilterResult> filterList) {
        this.items.clear();
        this.items.addAll(filterList);
        notifyDataSetChanged();
    }

    public FilterResult removeAt(int position) {
        FilterResult deletedFilter = items.remove(position);
        notifyItemRemoved(position);
        return deletedFilter;
    }

    public void restoreItem(int position, FilterResult filterResult) {
        items.add(position, filterResult);
        notifyItemInserted(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_filter_name);
        }

        public void update(FilterResult item) {
            name.setText(item.getName());
            itemView.setOnClickListener(v -> {
                Intent editFilter = new Intent(context, EditFilterActivity.class);
                editFilter.putExtra(EditFilterActivity.ARG_ID, item.getId());
                editFilter.putExtra(EditFilterActivity.ARG_NAME, item.getName());
                editFilter.putExtra(EditFilterActivity.ARG_PARAMETER, item.getParameter());
                editFilter.putExtra(EditFilterActivity.ARG_CONDITION, item.getCondition());
                editFilter.putExtra(EditFilterActivity.ARG_FILTER_TEXT, item.getFilterText());
                editFilter.putExtra(EditFilterActivity.ARG_MOVE_TO, item.isMoveTo());
                editFilter.putExtra(EditFilterActivity.ARG_FOLDER, item.getFolder());
                editFilter.putExtra(EditFilterActivity.ARG_AS_READ, item.isMarkAsRead());
                editFilter.putExtra(EditFilterActivity.ARG_AS_STARRED, item.isMarkAsStarred());
                context.startActivity(editFilter);
            });
        }
    }
}
