package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        final Context context = holder.root.getContext();
        final FilterResult filterResult = filterList.get(position);
        String name = filterResult.getName();
        holder.name.setText(name);
        holder.root.setOnClickListener(v -> {
            Intent editFilter = new Intent(context, EditFilterActivity.class);
            editFilter.putExtra(EditFilterActivity.ARG_ID, filterResult.getId());
            editFilter.putExtra(EditFilterActivity.ARG_NAME, filterResult.getName());
            editFilter.putExtra(EditFilterActivity.ARG_PARAMETER, filterResult.getParameter());
            editFilter.putExtra(EditFilterActivity.ARG_CONDITION, filterResult.getCondition());
            editFilter.putExtra(EditFilterActivity.ARG_FILTER_TEXT, filterResult.getFilterText());
            editFilter.putExtra(EditFilterActivity.ARG_MOVE_TO, filterResult.isMoveTo());
            editFilter.putExtra(EditFilterActivity.ARG_FOLDER, filterResult.getFolder());
            editFilter.putExtra(EditFilterActivity.ARG_AS_READ, filterResult.isMarkAsRead());
            editFilter.putExtra(EditFilterActivity.ARG_AS_STARRED, filterResult.isMarkAsStarred());
            context.startActivity(editFilter);
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public FilterResult removeAt(int position) {
        FilterResult deletedFilter = filterList.remove(position);
        notifyItemRemoved(position);
        return deletedFilter;
    }

    void restoreItem(int position, FilterResult filterResult) {
        filterList.add(position, filterResult);
        notifyItemInserted(position);
    }
}
