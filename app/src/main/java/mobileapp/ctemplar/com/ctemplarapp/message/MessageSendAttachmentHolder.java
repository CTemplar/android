package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class MessageSendAttachmentHolder extends RecyclerView.ViewHolder {
    public View root;
    public TextView txtName;
    public TextView txtSize;
    public ImageView imgDelete;

    public MessageSendAttachmentHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        txtName = itemView.findViewById(R.id.item_message_attachment_send_name);
        txtSize = itemView.findViewById(R.id.item_message_attachment_send_size);
        imgDelete = itemView.findViewById(R.id.item_message_attachment_delete);
    }
}
