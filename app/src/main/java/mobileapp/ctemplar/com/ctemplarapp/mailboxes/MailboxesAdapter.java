package mobileapp.ctemplar.com.ctemplarapp.mailboxes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
        holder.address.setText(mailboxEntity.email);
        setMailboxEnabled(holder, mailboxEntity.isEnabled);
        if (mailboxEntity.isDefault) {
            holder.enabled.setVisibility(View.INVISIBLE);
            holder.address.setSelected(true);
            lastSelectedHolder = holder;
            lastSelectedMailboxId = mailboxEntity.id;
        }

        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addressView) {
                mailboxesModel.updateDefaultMailbox(lastSelectedMailboxId, mailboxEntity.id);
                lastSelectedHolder.address.setSelected(false);
                lastSelectedHolder.enabled.setVisibility(View.VISIBLE);

                lastSelectedHolder = holder;
                lastSelectedMailboxId = mailboxEntity.id;

                addressView.setSelected(true);
                holder.enabled.setVisibility(View.INVISIBLE);
            }
        });

        holder.enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = view.isSelected();
                setMailboxEnabled(holder, !isSelected);
                mailboxesModel.updateEnabledMailbox(mailboxEntity.id, !isSelected);
            }
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