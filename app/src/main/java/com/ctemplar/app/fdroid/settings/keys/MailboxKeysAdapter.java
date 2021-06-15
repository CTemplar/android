package com.ctemplar.app.fdroid.settings.keys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ItemMailboxKeyHolderBinding;

public class MailboxKeysAdapter extends RecyclerView.Adapter<MailboxKeysAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private final ClickCallback callback;

    private List<GeneralizedMailboxKey> items = new ArrayList<>();

    MailboxKeysAdapter(ClickCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMailboxKeyHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position), position % 2 == 1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void setItems(List<GeneralizedMailboxKey> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMailboxKeyHolderBinding binding;

        public ViewHolder(ItemMailboxKeyHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(GeneralizedMailboxKey generalizedMailboxKey, boolean odd) {
            binding.getRoot().setBackgroundColor(odd
                    ? context.getResources().getColor(R.color.colorDivider)
                    : context.getResources().getColor(R.color.colorPrimary));
            binding.primaryTypeTextView.setVisibility(generalizedMailboxKey.isPrimary() ? View.VISIBLE : View.GONE);
            binding.setPrimaryKeyButton.setVisibility(generalizedMailboxKey.isPrimary() ? View.GONE : View.VISIBLE);
            binding.fingerprintTextView.setText(generalizedMailboxKey.getFingerprint());
            binding.keyTypeTextView.setText(generalizedMailboxKey.getKeyType().toString());
            binding.removeKeyButton.setVisibility(generalizedMailboxKey.isPrimary() ? View.GONE : View.VISIBLE);

            binding.removeKeyButton.setOnClickListener(v -> callback.onRemoveKeyClick(generalizedMailboxKey));
            binding.downloadKeyButton.setOnClickListener(v -> callback.onDownloadKeyClick(generalizedMailboxKey));
            binding.setPrimaryKeyButton.setOnClickListener(v -> callback.onSetAsPrimaryClick(generalizedMailboxKey));
        }
    }

    interface ClickCallback {
        void onSetAsPrimaryClick(GeneralizedMailboxKey key);

        void onDownloadKeyClick(GeneralizedMailboxKey key);

        void onRemoveKeyClick(GeneralizedMailboxKey key);
    }
}
