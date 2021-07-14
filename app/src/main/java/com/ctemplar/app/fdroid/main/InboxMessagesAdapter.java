package com.ctemplar.app.fdroid.main;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.repository.constant.MainFolderNames;
import com.ctemplar.app.fdroid.repository.constant.MessageActions;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.repository.provider.UserDisplayProvider;
import com.ctemplar.app.fdroid.utils.DateUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesViewHolder> {
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();
    private final List<MessageProvider> messageList;
    private final List<MessageProvider> filteredList;
    private final MainActivityViewModel mainModel;
    private OnReachedBottomCallback onReachedBottomCallback;
    private Handler onReachedBottomCallbackHandler;

    public InboxMessagesAdapter(MainActivityViewModel mainModel) {
        this.mainModel = mainModel;
        this.messageList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
    }

    @NonNull
    @Override
    public InboxMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.item_message_view_holder, viewGroup, false);
        ViewGroup backOptionsLayout = view.findViewById(R.id.item_message_view_holder_background_layout);
        View backOptionsView;

        String currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder != null && currentFolder.equals(MainFolderNames.DRAFT)) {
            backOptionsView = inflater.inflate(R.layout.swipe_actions_draft, backOptionsLayout, false);
        } else if (currentFolder != null && currentFolder.equals(MainFolderNames.SPAM)) {
            backOptionsView = inflater.inflate(R.layout.swipe_actions_spam, backOptionsLayout, false);
        } else {
            backOptionsView = inflater.inflate(R.layout.swipe_actions, backOptionsLayout, false);
        }

        backOptionsLayout.removeAllViews();
        backOptionsLayout.addView(backOptionsView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );

        return new InboxMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InboxMessagesViewHolder holder, int position) {
        final Resources resources = holder.root.getResources();
        if (position == getItemCount() - 1 && onReachedBottomCallback != null) {
            if (onReachedBottomCallbackHandler != null) {
                onReachedBottomCallbackHandler.post(() -> onReachedBottomCallback.onReachedBottom());
            } else {
                onReachedBottomCallback.onReachedBottom();
            }
        }
        final MessageProvider message = filteredList.get(position);
        String currentFolder = mainModel.getCurrentFolder().getValue();

        List<UserDisplayProvider> userDisplayList = new ArrayList<>();
        if (currentFolder != null && currentFolder.equals(MainFolderNames.SENT)) {
            userDisplayList.addAll(message.getReceiverDisplayList());
            userDisplayList.addAll(message.getCcDisplayList());
            userDisplayList.addAll(message.getBccDisplayList());
        }
        if (userDisplayList.isEmpty()) {
            userDisplayList.add(message.getSenderDisplay());
        }
        UserDisplayProvider userDisplay = userDisplayList.get(0);
        String name = userDisplay.getName();
        if (name != null && !name.isEmpty()) {
            holder.txtUsername.setText(userDisplay.getName());
        } else {
            holder.txtUsername.setText(userDisplay.getEmail());
        }

        holder.foreground.setOnClickListener(v -> onClickSubject.onNext(message.getId()));

        // check for last action (reply, reply all, forward)
        String lastActionThread = message.getLastActionThread();
        if (TextUtils.isEmpty(lastActionThread)) {
            holder.imgReply.setVisibility(View.GONE);
        } else {
            switch (lastActionThread) {
                case MessageActions.REPLY:
                    holder.imgReply.setImageResource(R.drawable.ic_reply_message);
                    break;
                case MessageActions.REPLY_ALL:
                    holder.imgReply.setImageResource(R.drawable.ic_reply_all_message);
                    break;
                case MessageActions.FORWARD:
                    holder.imgReply.setImageResource(R.drawable.ic_forward_message);
                    break;
            }
            holder.imgReply.setVisibility(View.VISIBLE);
        }

        // check for children count
        if (message.isHasChildren()) {
            int chainCount = message.getChildrenCount() + 1;
            holder.txtChildren.setText(String.valueOf(chainCount));
            holder.txtChildren.setVisibility(View.VISIBLE);
        } else {
            holder.txtChildren.setVisibility(View.GONE);
        }

        // check for read/unread
        if (message.isRead()) {
            holder.imgUnread.setVisibility(View.GONE);
            holder.txtUsername.setTypeface(null, Typeface.NORMAL);
            holder.foreground.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark));
        } else {
            holder.imgUnread.setVisibility(View.VISIBLE);
            holder.txtUsername.setTypeface(null, Typeface.BOLD);
            holder.foreground.setBackgroundColor(resources.getColor(R.color.colorPrimary));
        }

        // check for protection
        holder.imgEncrypted.setSelected(message.isProtected());

        // check for status (delivery in, delete in, dead mans in)
        if (message.getDelayedDelivery() != null) {
            String leftTime = DateUtils.elapsedTime(message.getDelayedDelivery());
            if (leftTime != null) {
                holder.txtStatus.setText(resources.getString(R.string.txt_left_time_delay_delivery, leftTime));
                holder.txtStatus.setBackgroundColor(resources.getColor(R.color.colorDarkGreen));
                holder.txtStatus.setVisibility(View.VISIBLE);
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else if (message.getDestructDate() != null) {
            String leftTime = DateUtils.elapsedTime(message.getDestructDate());
            if (leftTime != null) {
                holder.txtStatus.setText(resources.getString(R.string.txt_left_time_destruct, leftTime));
                holder.txtStatus.setVisibility(View.VISIBLE);
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else if (message.getDeadManDuration() != null) {
            String leftTime = DateUtils.deadMansTime(message.getDeadManDuration());
            if (leftTime != null) {
                holder.txtStatus.setText(resources.getString(R.string.txt_left_time_dead_mans_timer, leftTime));
                holder.txtStatus.setBackgroundColor(resources.getColor(R.color.colorRed0));
                holder.txtStatus.setVisibility(View.VISIBLE);
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else {
            holder.txtStatus.setVisibility(View.GONE);
        }

        Date messageDate = DateUtils.getDeliveryDate(message);
        holder.txtDate.setText(DateUtils.displayMessageDate(messageDate, resources));

        holder.imgStarredLayout.setOnClickListener(v -> {
            boolean isStarred = !message.isStarred();
            mainModel.markMessageIsStarred(message.getId(), isStarred);
            message.setStarred(isStarred);
            holder.imgStarred.setSelected(isStarred);
        });

        holder.imgStarred.setSelected(message.isStarred());

        // check for attachments
        if (message.isHasAttachments()) {
            holder.imgAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.imgAttachment.setVisibility(View.GONE);
        }

        // check for subject
        if (message.getEncryptionMessage() != null) {
            holder.txtSubject.setVisibility(View.INVISIBLE);
            holder.imgPasswordKey.setVisibility(View.VISIBLE);
            holder.txtSubjectEncrypted.setVisibility(View.GONE);
            holder.decryptionProgressBar.setVisibility(View.GONE);
        } else if (!message.isSubjectEncrypted() || message.isSubjectDecrypted()) {
            holder.txtSubject.setText(message.getSubject());
            holder.txtSubject.setVisibility(View.VISIBLE);
            holder.imgPasswordKey.setVisibility(View.GONE);
            holder.txtSubjectEncrypted.setVisibility(View.GONE);
            holder.decryptionProgressBar.setVisibility(View.GONE);
        } else if (message.getDecryptedSubject() != null) {
            holder.txtSubject.setText(message.getDecryptedSubject());
            holder.txtSubject.setVisibility(View.VISIBLE);
            holder.imgPasswordKey.setVisibility(View.GONE);
            holder.txtSubjectEncrypted.setVisibility(View.GONE);
            holder.decryptionProgressBar.setVisibility(View.GONE);
        } else {
            holder.txtSubject.setVisibility(View.INVISIBLE);
            holder.imgPasswordKey.setVisibility(View.GONE);
            holder.txtSubjectEncrypted.setVisibility(View.VISIBLE);
            holder.decryptionProgressBar.setVisibility(View.VISIBLE);
        }
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

    public MessageProvider getLast() {
        return messageList.isEmpty() ? null : messageList.get(messageList.size() - 1);
    }

    void restoreMessage(MessageProvider deletedMessage, int position) {
        filteredList.add(position, deletedMessage);
        notifyItemInserted(position);
    }

    public List<MessageProvider> getAll() {
        return filteredList;
    }

    public Long[] getAllMessagesIds() {
        Long[] messageIds = new Long[filteredList.size()];
        for (int messageIt = 0; messageIt < messageIds.length; messageIt++) {
            messageIds[messageIt] = filteredList.get(messageIt).getId();
        }
        return messageIds;
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
        if (TextUtils.isEmpty(filter)) {
            filterText = "";
        } else {
            filterText = filter.toLowerCase();
        }
        filter();
    }

    private void filter() {
        filteredList.clear();
        for (MessageProvider messageResult : messageList) {
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

    public void setOnReachedBottomCallback(OnReachedBottomCallback onReachedBottomCallback) {
        this.onReachedBottomCallback = onReachedBottomCallback;
        if (onReachedBottomCallback == null || Looper.myLooper() == null) {
            onReachedBottomCallbackHandler = null;
        } else {
            onReachedBottomCallbackHandler = new Handler();
        }
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

    public void clear() {
        messageList.clear();
        filteredList.clear();
        notifyDataSetChanged();
    }

    public void addMessages(List<MessageProvider> messages) {
        messageList.addAll(messages);
        int beforeCount = getItemCount();
        for (MessageProvider message : messages) {
            if (matchFiltering(message)) {
                filteredList.add(message);
            }
        }
        int afterCount = getItemCount();
        notifyItemRangeInserted(beforeCount, afterCount - beforeCount);
    }

    public void addMessage(MessageProvider message) {
        messageList.add(0, message);
        if (matchFiltering(message)) {
            filteredList.add(0, message);
            notifyItemInserted(0);
        }
    }

    public void onItemUpdated(MessageProvider message) {
        int index = filteredList.indexOf(message);
        if (index == -1) {
            return;
        }
        notifyItemChanged(index);
    }

    public interface OnReachedBottomCallback {
        void onReachedBottom();
    }
}
