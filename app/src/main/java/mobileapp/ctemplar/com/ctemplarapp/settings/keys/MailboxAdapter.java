package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemMailboxAddressHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class MailboxAdapter extends RecyclerView.Adapter<MailboxAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;

    private List<MailboxEntity> items = new ArrayList<>();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMailboxAddressHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void setItems(List<MailboxEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMailboxAddressHolderBinding binding;

        public ViewHolder(ItemMailboxAddressHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(MailboxEntity mailboxEntity) {
            binding.emailAddressTextView.setText(mailboxEntity.getEmail());
            binding.fingerprintTextView.setText(mailboxEntity.getFingerprint());
            binding.keyTypeTextView.setText(mailboxEntity.getKeyType().toString());
        }
    }
}
