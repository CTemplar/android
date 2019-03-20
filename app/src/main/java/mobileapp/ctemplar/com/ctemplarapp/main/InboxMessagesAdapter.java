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
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesViewHolder> {

    private List<MessageProvider> messagesList;
    private List<MessageProvider> filteredList;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();
    private final MainActivityViewModel mainModel;

    InboxMessagesAdapter(List<MessageProvider> messagesList, MainActivityViewModel mainModel) {
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
        String currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder != null && currentFolder.equals("draft")) {
            backOptionsView = inflater.inflate(R.layout.swipe_actions_draft, backOptionsLayout, false);
        } else if (currentFolder != null && currentFolder.equals("spam")) {
            backOptionsView = inflater.inflate(R.layout.swipe_actions_spam, backOptionsLayout, false);
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
        if (!TextUtils.isEmpty(messages.getDelayedDelivery())) {
            String leftTime = AppUtils.elapsedTime(messages.getDelayedDelivery());
            if (leftTime != null) {
                holder.txtStatus.setText(holder.root.getResources().getString(R.string.txt_left_time_delay_delivery, leftTime));
                holder.txtStatus.setBackgroundColor(holder.root.getResources().getColor(R.color.colorDarkGreen));
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else if (!TextUtils.isEmpty(messages.getDestructDate())) {
            String leftTime = AppUtils.elapsedTime(messages.getDestructDate());
            if (leftTime != null) {
                holder.txtStatus.setText(holder.root.getResources().getString(R.string.txt_left_time_destruct, leftTime));
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else {
            holder.txtStatus.setVisibility(View.GONE);
        }

        // format creation date
        if (!TextUtils.isEmpty(messages.getCreatedAt())) {
            holder.txtDate.setText(AppUtils.messageDate(messages.getCreatedAt()));
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

    MessageProvider removeAt(int position) {
        MessageProvider removedMessage = filteredList.remove(position);
        notifyItemRemoved(position);
        return removedMessage;
    }

    public MessageProvider get(int position) {
        return filteredList.get(position);
    }

    void restoreMessage(MessageProvider deletedMessage, int position) {
        filteredList.add(position, deletedMessage);
        notifyItemInserted(position);
    }

    public List<MessageProvider> getAll() {
        return filteredList;
    }


    private boolean isStarred = false, isUnread = false, withAttachment = false;
    private String filterText = "";

    void filter(boolean isStarred, boolean isUnread, boolean withAttachment) {
        this.isStarred = isStarred;
        this.isUnread = isUnread;
        this.withAttachment = withAttachment;
        filter();

    }

    void filter(String filter) {
        if (filter == null) {
            filterText = "";
        } else {
            filterText = filter.toLowerCase();
        }
        filter();
    }

    private void filter() {
        filteredList.clear();
        for (MessageProvider messageResult : messagesList) {
            if (matchFiltering(messageResult)) {
                filteredList.add(messageResult);
            }
        }
        notifyDataSetChanged();
    }

    private boolean matchFiltering(MessageProvider messageProvider) {
        boolean messageIsStarred = messageProvider.isStarred();
        boolean messageUnread = !messageProvider.isRead();

        if (!isValidForFilter(messageProvider)) {
            return false;
        }

        if ((isStarred && messageIsStarred) ||
                (isUnread && messageUnread) ||
                (withAttachment && messageProvider.isHasAttachments())) {
            return true;
        } else {
            return !isStarred && !isUnread && !withAttachment;
        }
    }

    private boolean isValidForFilter(MessageProvider messageProvider) {
        return wrap(messageProvider.getContent()).contains(filterText) ||
                wrap(messageProvider.getSubject()).contains(filterText) ||
                containsInStringArrayWrapped(messageProvider.getReceivers(), filterText) ||
                containsInStringArrayWrapped(messageProvider.getBcc(), filterText) ||
                containsInStringArrayWrapped(messageProvider.getCc(), filterText) ||
                wrap(messageProvider.getSender()).contains(filterText);

    }

    PublishSubject<Long> getOnClickSubject() {
        return onClickSubject;
    }

    private static boolean containsInStringArrayWrapped(String[] array, String filter) {
        if (array == null) {
            return false;
        }
        for (String item : array) {
            if (wrap(item).contains(filter)) {
                return true;
            }
        }
        return false;
    }

    private static String wrap(String str) {
        if (str == null) {
            return "";
        }
        return str.toLowerCase();
    }
}