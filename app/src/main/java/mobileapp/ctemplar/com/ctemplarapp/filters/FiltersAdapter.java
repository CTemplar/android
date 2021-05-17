package mobileapp.ctemplar.com.ctemplarapp.filters;

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

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterOrderListRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterOrderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.EmailFilterResult;
import mobileapp.ctemplar.com.ctemplarapp.view.recycler.ReorderableRecyclerViewAdapter;

public class FiltersAdapter extends ReorderableRecyclerViewAdapter<FiltersAdapter.ViewHolder> {
    private final List<EmailFilterResult> items = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;

    private OnChangeOrderListener onChangeOrderListener;

    public FiltersAdapter() {
    }

    @Override
    public void onItemMove(int oldPosition, int newPosition) {
        EmailFilterResult item = items.remove(oldPosition);
        items.add(newPosition > oldPosition ? newPosition - 1 : newPosition, item);
        notifyItemMoved(oldPosition, newPosition);
        onChangeOrder();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_filters_holder, parent, false);
        return new ViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<EmailFilterResult> filterList) {
        this.items.clear();
        this.items.addAll(filterList);
        notifyDataSetChanged();
    }

    public EmailFilterResult removeAt(int position) {
        EmailFilterResult deletedFilter = items.remove(position);
        notifyItemRemoved(position);
        return deletedFilter;
    }

    public void restoreItem(int position, EmailFilterResult emailFilterResult) {
        items.add(position, emailFilterResult);
        notifyItemInserted(position);
    }

    public void setOnChangeOrderListener(OnChangeOrderListener onChangeOrderListener) {
        this.onChangeOrderListener = onChangeOrderListener;
    }

    void onChangeOrder() {
        EmailFilterOrderListRequest emailFilterOrderListRequest = new EmailFilterOrderListRequest();
        List<EmailFilterOrderRequest> emailFilterOrderRequestList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            emailFilterOrderRequestList.add(new EmailFilterOrderRequest(
                    items.get(i).getId(), i + 1
            ));
        }
        emailFilterOrderListRequest.setFilterList(emailFilterOrderRequestList);
        onChangeOrderListener.onChange(emailFilterOrderListRequest);
    }

    class ViewHolder extends ReorderableRecyclerViewAdapter.ViewHolder {
        private final TextView name;
        private final View reorderTouchView;

        public ViewHolder(FiltersAdapter adapter, @NonNull View itemView) {
            super(itemView, adapter);
            name = itemView.findViewById(R.id.item_filter_name);
            reorderTouchView = itemView.findViewById(R.id.reorder_touch_view);
        }

        public void update(EmailFilterResult item) {
            name.setText(item.getName());
            itemView.setOnClickListener(v -> {
                Intent editFilter = new Intent(context, EditFilterActivity.class);
                editFilter.putExtra(EditFilterActivity.ARG_ID, item.getId());
                editFilter.putExtra(EditFilterActivity.ARG_NAME, item.getName());
//                editFilter.putExtra(EditFilterActivity.ARG_PARAMETER, item.getParameter());
//                editFilter.putExtra(EditFilterActivity.ARG_CONDITION, item.getCondition());
//                editFilter.putExtra(EditFilterActivity.ARG_FILTER_TEXT, item.getFilterText());
                editFilter.putExtra(EditFilterActivity.ARG_MOVE_TO, item.isMoveTo());
                editFilter.putExtra(EditFilterActivity.ARG_FOLDER, item.getFolder());
                editFilter.putExtra(EditFilterActivity.ARG_AS_READ, item.isMarkAsRead());
                editFilter.putExtra(EditFilterActivity.ARG_AS_STARRED, item.isMarkAsStarred());
                context.startActivity(editFilter);
            });
            setDraggableView(reorderTouchView);
        }
    }
}
