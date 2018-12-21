package mobileapp.ctemplar.com.ctemplarapp.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class InboxMessagesViewHolder extends RecyclerView.ViewHolder{
    public View root;
    public TextView txtUsername;
    public TextView txtChildren;
    public TextView txtStatus;
    public TextView txtDate;
    public TextView txtSubject;
    public TextView txtContent;
    public ImageView imgUnread;
    public ImageView imgProtection;
    public ImageView imgStarred;
    public ImageView imgAttachment;

    public InboxMessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        txtUsername = itemView.findViewById(R.id.message_holder_username);
        txtChildren = itemView.findViewById(R.id.message_holder_children);
        txtStatus = itemView.findViewById(R.id.message_holder_status);
        txtDate = itemView.findViewById(R.id.message_holder_date);
        txtSubject = itemView.findViewById(R.id.message_holder_subject);
        txtContent = itemView.findViewById(R.id.message_holder_content);
        imgUnread = itemView.findViewById(R.id.message_holder_new);
        imgProtection = itemView.findViewById(R.id.message_holder_protection);
        imgStarred = itemView.findViewById(R.id.message_holder_starred);
        imgAttachment = itemView.findViewById(R.id.message_holder_attachment);
    }
}
