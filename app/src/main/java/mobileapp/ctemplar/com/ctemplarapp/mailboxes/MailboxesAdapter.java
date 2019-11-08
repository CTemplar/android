package mobileapp.ctemplar.com.ctemplarapp.mailboxes;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class MailboxesAdapter extends RecyclerView.Adapter<MailboxesViewHolder> {

    private MailboxesViewModel mailboxesModel;
    private List<MailboxEntity> mailboxEntityList;
    private MailboxesViewHolder lastSelectedHolder;
    private long lastSelectedMailboxId;

    MailboxesAdapter(MailboxesViewModel mailboxesModel, List<MailboxEntity> mailboxEntityList) {
        this.mailboxesModel = mailboxesModel;
        this.mailboxEntityList = mailboxEntityList;
    }

    @NonNull
    @Override
    public MailboxesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mailbox_holder, viewGroup, false);
        return new MailboxesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MailboxesViewHolder holder, int position) {
        final MailboxEntity mailboxEntity = mailboxEntityList.get(position);
        holder.address.setText(mailboxEntity.getEmail());
        setMailboxEnabled(holder, mailboxEntity.isEnabled());
        if (mailboxEntity.isDefault()) {
            holder.enabled.setVisibility(View.GONE);
            holder.checkMark.setVisibility(View.VISIBLE);
            holder.address.setSelected(true);

            lastSelectedHolder = holder;
            lastSelectedMailboxId = mailboxEntity.getId();
        }

        holder.address.setOnClickListener(addressView -> {
            mailboxesModel.updateDefaultMailbox(lastSelectedMailboxId, mailboxEntity.getId());
            lastSelectedHolder.address.setSelected(false);
            lastSelectedHolder.enabled.setVisibility(View.VISIBLE);
            lastSelectedHolder.checkMark.setVisibility(View.GONE);

            lastSelectedHolder = holder;
            lastSelectedMailboxId = mailboxEntity.getId();

            addressView.setSelected(true);
            holder.enabled.setVisibility(View.GONE);
            holder.checkMark.setVisibility(View.VISIBLE);
        });

        holder.enabled.setOnClickListener(view -> {
            boolean isSelected = view.isSelected();
            setMailboxEnabled(holder, !isSelected);
            mailboxesModel.updateEnabledMailbox(mailboxEntity.getId(), !isSelected);
        });
    }

    @Override
    public int getItemCount() {
        return mailboxEntityList.size();
    }

    private void setMailboxEnabled(MailboxesViewHolder view, boolean state) {
        Context context = view.root.getContext();
        view.address.setEnabled(state);
        view.enabled.setSelected(state);
        if (state) {
            view.enabled.setText(context.getString(R.string.mailbox_disable));
        } else {
            view.enabled.setText(context.getString(R.string.mailbox_enable));
        }
    }
}
