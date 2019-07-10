package mobileapp.ctemplar.com.ctemplarapp.mailboxes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class MailboxesViewHolder extends RecyclerView.ViewHolder {
    public View root;
    TextView address;
    TextView enabled;

    MailboxesViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        address = itemView.findViewById(R.id.item_mailbox_address);
        enabled = itemView.findViewById(R.id.item_mailbox_enabled);
    }
}
