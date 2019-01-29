package mobileapp.ctemplar.com.ctemplarapp.main;

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
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesViewHolder> {

    private List<MessageProvider> messagesList;
    private List<MessageProvider> filteredList;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();
    private final MainActivityViewModel mainModel;

    public InboxMessagesAdapter(List<MessageProvider> messagesList, MainActivityViewModel mainModel) {
        this.messagesList = messagesList;
        filteredList = new ArrayList<>();
        filteredList.addAll(messagesList);
        this.mainModel = mainModel;
    }

    @NonNull
    @Override
    public InboxMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup view = (ViewGroup) inflater
                .inflate(R.layout.item_message_view_holder, viewGroup, false);

        ViewGroup backOptionsLayout = view.findViewById(R.id.item_message_view_holder_background_layout);
        View backOptionsView;
        if (mainModel.getCurrentFolder().getValue().equals("draft")) {
            backOptionsView = inflater.inflate(R.layout.swipe_actions_draft, backOptionsLayout, false);
        } else {
            backOptionsView = inflater.inflate(R.layout.swipe_actions, backOptionsLayout, false);
        }

        backOptionsLayout.removeAllViews();
        backOptionsLayout.addView(backOptionsView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new InboxMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InboxMessagesViewHolder holder, int position) {
        final MessageProvider messages = filteredList.get(position);

        holder.txtUsername.setText(messages.getSender());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(messages.getId());
            }
        });

        // check for children count
        if (messages.getChildrenCount() > 0) {
            holder.txtChildren.setText(String.valueOf(messages.getChildrenCount()));
            holder.txtChildren.setVisibility(View.VISIBLE);
        } else {
            holder.txtChildren.setVisibility(View.GONE);
        }

        // check for read/unread
        if (messages.isRead()) {
            holder.imgUnread.setVisibility(View.GONE);
            holder.txtUsername.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.imgUnread.setVisibility(View.VISIBLE);
            holder.txtUsername.setTypeface(null, Typeface.BOLD);
        }

        // check for status (time delete, delayed delivery)
        if (TextUtils.isEmpty(messages.getDelayedDelivery()) &&
                TextUtils.isEmpty(messages.getDestructDate())) {
            holder.txtStatus.setVisibility(View.GONE);
        }

        // format creation date
        if (!TextUtils.isEmpty(messages.getCreatedAt())) {
            holder.txtDate.setText(AppUtils.formatDate(messages.getCreatedAt()));
        }

        holder.imgStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isStarred = !messages.isStarred();
                mainModel.markMessageIsStarred(messages.getId(), isStarred);
                messages.setStarred(isStarred);
                holder.imgStarred.setSelected(isStarred);
            }
        });

        holder.imgStarred.setSelected(messages.isStarred());

        if (messages.isHasAttachments()) {
            holder.imgAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.imgAttachment.setVisibility(View.GONE);
        }

        holder.txtSubject.setText(filteredList.get(position).getSubject());

        Spanned contentMessage = Html.fromHtml(messages.getContent());
        holder.txtContent.setText(contentMessage);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public MessageProvider removeAt(int position) {
        MessageProvider removedMessage = filteredList.remove(position);
        notifyItemRemoved(position);
        return removedMessage;
    }

    public MessageProvider get(int position) {
        return filteredList.get(position);
    }

    public void restoreMessage(MessageProvider deletedMessage, int position) {
        filteredList.add(position, deletedMessage);
        notifyItemInserted(position);
    }

    public List<MessageProvider> getAll() {
        return filteredList;
    }

    public boolean filter(boolean isStarred, boolean isUnread, boolean withAttachment) {
        boolean filtered = false;
        filteredList = new ArrayList<>();
        for (MessageProvider messageResult :
                messagesList) {
            boolean messageIsStarred = messageResult.isStarred();
            boolean messageUnread = !messageResult.isRead();

            if ((isStarred && messageIsStarred) ||
                    (isUnread && messageUnread) ||
                    (withAttachment && messageResult.isHasAttachments())) {
                filteredList.add(messageResult);
            } else if (!isStarred && !isUnread && !withAttachment) {
                filteredList.add(messageResult);
            } else {
                filtered = true;
            }
        }
        notifyDataSetChanged();
        return filtered;
    }

    public PublishSubject<Long> getOnClickSubject() {
        return onClickSubject;
    }
}