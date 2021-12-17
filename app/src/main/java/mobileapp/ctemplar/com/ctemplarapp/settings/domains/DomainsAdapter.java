package mobileapp.ctemplar.com.ctemplarapp.settings.domains;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemDomainHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;

public class DomainsAdapter extends RecyclerView.Adapter<DomainsAdapter.ViewHolder> {
    private LayoutInflater inflater;

    private List<CustomDomainDTO> items = new ArrayList<>();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDomainHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(CustomDomainDTO[] items) {
        this.items = new ArrayList<>(items.length);
        Collections.addAll(this.items, items);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDomainHolderBinding binding;

        public ViewHolder(ItemDomainHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(CustomDomainDTO dto) {
            binding.domainValueTextView.setText(dto.getDomain());
        }
    }
}
