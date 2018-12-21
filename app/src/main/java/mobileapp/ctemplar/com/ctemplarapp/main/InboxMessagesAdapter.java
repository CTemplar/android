package mobileapp.ctemplar.com.ctemplarapp.main;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesViewHolder> {

    private List<MessagesResult> messagesList;
    private List<MessagesResult> filteredList;
    private MailboxEntity currentMailbox;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();
    private final MainActivityViewModel mainModel;

    public InboxMessagesAdapter(List<MessagesResult> messagesList, MainActivityViewModel mainModel) {
        this.messagesList = messagesList;
        filteredList = new ArrayList<>();
        filteredList.addAll(messagesList);
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
        this.mainModel = mainModel;
    }

    @NonNull
    @Override
    public InboxMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_view_holder, viewGroup, false);

        return new InboxMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InboxMessagesViewHolder holder, int position) {
        final MessagesResult messagesResult = filteredList.get(position);

        holder.txtUsername.setText(messagesResult.getSender());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(messagesResult.getId());
            }
        });

        // check for children count
        if (messagesResult.getChildrenCount() > 0) {
            holder.txtChildren.setText(String.valueOf(messagesResult.getChildrenCount()));
            holder.txtChildren.setVisibility(View.VISIBLE);
        } else {
            holder.txtChildren.setVisibility(View.GONE);
        }

        // check for read/unread
        if (messagesResult.isRead()) {
            holder.imgUnread.setVisibility(View.GONE);
            holder.txtUsername.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.imgUnread.setVisibility(View.VISIBLE);
            holder.txtUsername.setTypeface(null, Typeface.BOLD);
        }

        // check for status (time delete, delayed delivery)
        if (TextUtils.isEmpty(messagesResult.getDelayedDelivery()) &&
                TextUtils.isEmpty(messagesResult.getDestructDate())) {
            holder.txtStatus.setVisibility(View.GONE);
        }

        // format creation date
        if (!TextUtils.isEmpty(messagesResult.getCreatedAt())) {
            holder.txtDate.setText(AppUtils.formatDate(messagesResult.getCreatedAt()));
        }

        holder.imgStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isStarred = !messagesResult.isStarred();
                mainModel.markMessageIsStarred(messagesResult.getId(), isStarred);
                messagesResult.setStarred(isStarred);
                holder.imgStarred.setSelected(isStarred);
            }
        });

        holder.imgStarred.setSelected(messagesResult.isStarred());

        if (messagesResult.getAttachments() != null &&
                messagesResult.getAttachments().size() > 0) {
            holder.imgAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.imgAttachment.setVisibility(View.GONE);
        }

        holder.txtSubject.setText(filteredList.get(position).getSubject());
        String password =
                CTemplarApp.getInstance().getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);

        PGPManager pgpManager = new PGPManager();
        String privateKey = currentMailbox.getPrivateKey();
        String messageContent = pgpManager.decryptMessage(messagesResult.getContent(), privateKey, password);
        messageContent = messageContent.replaceAll("<img.+?>", "");

        Spanned contentMessage = Html.fromHtml(messageContent);
        holder.txtContent.setText(contentMessage);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public MessagesResult removeAt(int position) {
        MessagesResult removedMessage = filteredList.remove(position);
        notifyItemRemoved(position);
        return removedMessage;
    }

    public void restoreMessage(MessagesResult deletedMessage, int position) {
        filteredList.add(position, deletedMessage);
        notifyItemInserted(position);
    }

    public PublishSubject<Long> getOnClickSubject() {
        return onClickSubject;
    }

    public void filter(boolean isStarred, boolean isUnread, boolean withAttachment) {
        filteredList = new ArrayList<>();
        for (MessagesResult messageResult :
                messagesList) {
            boolean messageIsStarred = messageResult.isStarred();
            boolean messageUnread = !messageResult.isRead();
            boolean messageWithAttachments = messageResult.getAttachments() != null;
            boolean messageNotEmpty = !messageResult.getAttachments().isEmpty();

            if ((isStarred && messageIsStarred) ||
                    (isUnread && messageUnread) ||
                    (withAttachment && messageWithAttachments && messageNotEmpty)) {
                filteredList.add(messageResult);
            }
        }
        notifyDataSetChanged();
    }
}