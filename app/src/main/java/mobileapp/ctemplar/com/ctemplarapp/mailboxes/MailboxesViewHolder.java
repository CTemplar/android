package mobileapp.ctemplar.com.ctemplarapp.mailboxes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class MailboxesViewHolder extends RecyclerView.ViewHolder {
    public View root;
    TextView address;
    TextView enabled;
    ImageView checkMark;

    MailboxesViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        address = itemView.findViewById(R.id.item_mailbox_address);
        enabled = itemView.findViewById(R.id.item_mailbox_enabled);
        checkMark = itemView.findViewById(R.id.item_mailbox_check_mark);
    }
}
