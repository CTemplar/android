package mobileapp.ctemplar.com.ctemplarapp.main;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class InboxMessagesViewHolder extends RecyclerView.ViewHolder{
    View root;
    ConstraintLayout foreground;
    TextView txtUsername;
    TextView txtChildren;
    TextView txtStatus;
    TextView txtDate;
    TextView txtSubjectEncrypted;
    TextView txtSubject;
    ImageView imgReply;
    ImageView imgUnread;
    ImageView imgEncrypted;
    ImageView imgStarred;
    LinearLayout imgStarredLayout;
    ImageView imgAttachment;

    InboxMessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        foreground = itemView.findViewById(R.id.item_message_view_holder_foreground);
        txtUsername = itemView.findViewById(R.id.message_holder_username);
        txtChildren = itemView.findViewById(R.id.message_holder_children);
        txtStatus = itemView.findViewById(R.id.message_holder_status);
        txtDate = itemView.findViewById(R.id.message_holder_date);
        txtSubjectEncrypted = itemView.findViewById(R.id.message_holder_subject_encrypted);
        txtSubject = itemView.findViewById(R.id.message_holder_subject);
        imgReply = itemView.findViewById(R.id.message_holder_reply);
        imgUnread = itemView.findViewById(R.id.message_holder_new);
        imgEncrypted = itemView.findViewById(R.id.message_holder_encrypted);
        imgStarred = itemView.findViewById(R.id.message_holder_starred);
        imgStarredLayout = itemView.findViewById(R.id.message_holder_starred_layout);
        imgAttachment = itemView.findViewById(R.id.message_holder_attachment);
    }
}
