package mobileapp.ctemplar.com.ctemplarapp.main;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kibotu.pgp.Pgp;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessageViewHolder> {

    private List<MessagesResult> messagesList;
    private MailboxEntity currentMailbox;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();

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
        final MessagesResult messagesResult = messagesList.get(position);
        holder.txtUsername.setText(messagesResult.getSender());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(messagesResult.getId());
            }
        });

        // check for children count
        if(messagesList.get(position).getChildrenCount() > 0) {
            holder.txtChildren.setText(String.valueOf(messagesResult.getChildrenCount()));
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
        if(TextUtils.isEmpty(messagesResult.getDelayedDelivery()) &&
                TextUtils.isEmpty(messagesResult.getDestructDate())) {
            holder.txtStatus.setVisibility(View.GONE);
        }

        // format creation date
        if(!TextUtils.isEmpty(messagesResult.getCreatedAt())) {
            holder.txtDate.setText(AppUtils.formatDate(messagesResult.getCreatedAt()));
        }

        holder.imgStarred.setEnabled(messagesResult.isStarred());

        if(messagesResult.getAttachments() != null &&
                messagesResult.getAttachments().size() > 0) {
            holder.imgAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.imgAttachment.setVisibility(View.GONE);
        }

        holder.txtSubject.setText(messagesList.get(position).getSubject());
        String password =
                CTemplarApp.getInstance().getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);
        holder.txtContent.setText(decodeContent(messagesResult.getContent(), password));
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private String decodeContent(String encodedString, String password) {

        Pgp.setPrivateKey(currentMailbox.getPrivateKey());
        Pgp.setPublicKey(currentMailbox.getPublicKey());
        String result = "";

        try {
            result = Pgp.decrypt(encodedString, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public PublishSubject<Long> getOnClickSubject() {
        return onClickSubject;
    }
}