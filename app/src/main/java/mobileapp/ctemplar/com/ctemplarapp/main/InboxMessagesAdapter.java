package mobileapp.ctemplar.com.ctemplarapp.main;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kibotu.pgp.Pgp;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessageViewHolder> {

    private List<MessagesResult> messagesList;
    private MailboxEntity currentMailbox;

    public InboxMessagesAdapter(List<MessagesResult> messagesList) {
        this.messagesList = messagesList;
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
    }

    @NonNull
    @Override
    public InboxMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_view_holder, viewGroup, false);

        return new InboxMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxMessageViewHolder holder, int position) {
        holder.txtUsername.setText(messagesList.get(position).getSender());

        // check for children count
        if(messagesList.get(position).getChildrenCount() > 0) {
            holder.txtChildren.setText(String.valueOf(messagesList.get(position).getChildrenCount()));
            holder.txtChildren.setVisibility(View.VISIBLE);
        } else {
            holder.txtChildren.setVisibility(View.GONE);
        }

        // check for read/unread
        if(messagesList.get(position).isRead()) {
            holder.imgUnread.setVisibility(View.GONE);
            holder.txtUsername.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.imgUnread.setVisibility(View.VISIBLE);
            holder.txtUsername.setTypeface(null, Typeface.BOLD);
        }

        // check for status (time delete, delayed delivery)
        if(TextUtils.isEmpty(messagesList.get(position).getDelayedDelivery()) &&
                TextUtils.isEmpty(messagesList.get(position).getDestructDate())) {
            holder.txtStatus.setVisibility(View.GONE);
        }

        // format creation date
        if(TextUtils.isEmpty(messagesList.get(position).getCreatedAt())) {
            holder.txtDate.setText(AppUtils.formatDate(messagesList.get(position).getCreatedAt()));
        }

        holder.imgStarred.setEnabled(messagesList.get(position).isStarred());

        if(messagesList.get(position).getAttachments() != null &&
                messagesList.get(position).getAttachments().size() > 0) {
            holder.imgAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.imgAttachment.setVisibility(View.GONE);
        }

        holder.txtSubject.setText(messagesList.get(position).getSubject());
        // Commented because PGP library requires password that can't be obtained
        //holder.txtContent.setText(decodeContent(messagesList.get(position).getContent(), messagesList.get(position).getHash()));
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private String decodeContent(String encodedString, String password) {
        Pgp.setPrivateKey(currentMailbox.getPrivateKey());
        Pgp.setPublicKey(currentMailbox.getPrivateKey());
        String result = "";

        try {
            result = Pgp.decrypt(encodedString, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}