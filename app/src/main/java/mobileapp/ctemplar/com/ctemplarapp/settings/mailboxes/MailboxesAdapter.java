package mobileapp.ctemplar.com.ctemplarapp.settings.mailboxes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemMailboxHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class MailboxesAdapter extends RecyclerView.Adapter<MailboxesAdapter.ViewHolder> {
    private LayoutInflater inflater;

    private final MailboxesViewModel mailboxesModel;

    private List<MailboxEntity> mailboxEntityList = new ArrayList<>();
    private ItemMailboxHolderBinding lastSelectedBinding;
    private long lastSelectedMailboxId;

    MailboxesAdapter(MailboxesViewModel mailboxesModel) {
        this.mailboxesModel = mailboxesModel;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMailboxHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(mailboxEntityList.get(position));
    }

    @Override
    public int getItemCount() {
        return mailboxEntityList.size();
    }

    public void setItems(List<MailboxEntity> mailboxEntityList) {
        this.mailboxEntityList = mailboxEntityList;
        notifyDataSetChanged();
    }

    private void setMailboxEnabled(ItemMailboxHolderBinding binding, boolean state) {
        binding.itemMailboxStateTextView.setSelected(state);
        binding.itemMailboxAddressTextView.setEnabled(state);
        binding.itemMailboxStateTextView.setText(state ? R.string.mailbox_disable : R.string.mailbox_enable);
    }

    private void setMailboxDefault(ItemMailboxHolderBinding binding, long lastMailboxId, boolean state) {
        binding.itemMailboxStateTextView.setVisibility(state ? View.GONE : View.VISIBLE);
        binding.itemMailboxCheckMarkImageView.setVisibility(state ? View.VISIBLE : View.GONE);
        if (state) {
            lastSelectedBinding = binding;
            lastSelectedMailboxId = lastMailboxId;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMailboxHolderBinding binding;

        public ViewHolder(ItemMailboxHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(MailboxEntity mailboxEntity) {
            binding.itemMailboxAddressTextView.setText(mailboxEntity.getEmail());
            setMailboxEnabled(binding, mailboxEntity.isEnabled());
            setMailboxDefault(binding, mailboxEntity.getId(), mailboxEntity.isDefault());

            binding.itemMailboxAddressTextView.setOnClickListener(addressView -> {
                mailboxesModel.updateDefaultMailbox(lastSelectedMailboxId, mailboxEntity.getId());
                setMailboxDefault(lastSelectedBinding, lastSelectedMailboxId, false);
                setMailboxDefault(binding, mailboxEntity.getId(), true);
            });

            binding.itemMailboxStateTextView.setOnClickListener(view -> {
                boolean isSelected = !view.isSelected();
                setMailboxEnabled(binding, isSelected);
                mailboxesModel.updateEnabledMailbox(mailboxEntity.getId(), isSelected);
            });
        }
    }
}
