package mobileapp.ctemplar.com.ctemplarapp.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class InboxMessagesViewHolder extends RecyclerView.ViewHolder{
    View root;
    TextView txtUsername;
    TextView txtChildren;
    TextView txtStatus;
    TextView txtDate;
    TextView txtSubjectEncrypted;
    TextView txtSubject;
    ImageView imgReply;
    ImageView imgUnread;
    ImageView imgProtection;
    ImageView imgStarred;
    ImageView imgAttachment;

    InboxMessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        txtUsername = itemView.findViewById(R.id.message_holder_username);
        txtChildren = itemView.findViewById(R.id.message_holder_children);
        txtStatus = itemView.findViewById(R.id.message_holder_status);
        txtDate = itemView.findViewById(R.id.message_holder_date);
        txtSubjectEncrypted = itemView.findViewById(R.id.message_holder_subject_encrypted);
        txtSubject = itemView.findViewById(R.id.message_holder_subject);
        imgReply = itemView.findViewById(R.id.message_holder_reply);
        imgUnread = itemView.findViewById(R.id.message_holder_new);
        imgProtection = itemView.findViewById(R.id.message_holder_protection);
        imgStarred = itemView.findViewById(R.id.message_holder_starred);
        imgAttachment = itemView.findViewById(R.id.message_holder_attachment);
    }
}
