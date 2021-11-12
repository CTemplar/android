package mobileapp.ctemplar.com.ctemplarapp.main;

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
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemMessageViewHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MessageActions;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesAdapter.InboxMessagesViewHolder> {
    private LayoutInflater inflater;

    private final List<MessageProvider> messageList;
    private final List<MessageProvider> filteredList;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();

    private final MainActivityViewModel mainModel;

    private OnReachedBottomCallback onReachedBottomCallback;
    private Handler onReachedBottomCallbackHandler;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.inflater = LayoutInflater.from(recyclerView.getContext());
    }

    public InboxMessagesAdapter(MainActivityViewModel mainModel) {
        this.mainModel = mainModel;
        this.messageList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
    }

    @NonNull
    @Override
    public InboxMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.item_message_view_holder, viewGroup, false);
        ViewGroup backOptionsLayout = view.findViewById(R.id.background_layout);
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

        return new InboxMessagesViewHolder(ItemMessageViewHolderBinding.inflate(inflater, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final InboxMessagesViewHolder holder, int position) {
        final Resources resources = holder.binding.getRoot().getResources();
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
            holder.binding.usernameTextView.setText(userDisplay.getName());
        } else {
            holder.binding.usernameTextView.setText(userDisplay.getEmail());
        }

        holder.binding.foregroundLayout.setOnClickListener(v -> onClickSubject.onNext(message.getId()));

        // check for last action (reply, reply all, forward)
        String lastActionThread = message.getLastActionThread();
        if (TextUtils.isEmpty(lastActionThread)) {
            holder.binding.replyMarkImageView.setVisibility(View.GONE);
        } else {
            switch (lastActionThread) {
                case MessageActions.REPLY:
                    holder.binding.replyMarkImageView.setImageResource(R.drawable.ic_reply_message);
                    break;
                case MessageActions.REPLY_ALL:
                    holder.binding.replyMarkImageView.setImageResource(R.drawable.ic_reply_all_message);
                    break;
                case MessageActions.FORWARD:
                    holder.binding.replyMarkImageView.setImageResource(R.drawable.ic_forward_message);
                    break;
            }
            holder.binding.replyMarkImageView.setVisibility(View.VISIBLE);
        }

        // check for children count
        if (message.isHasChildren()) {
            int chainCount = message.getChildrenCount() + 1;
            holder.binding.childrenCounterTextView.setText(String.valueOf(chainCount));
            holder.binding.childrenCounterTextView.setVisibility(View.VISIBLE);
        } else {
            holder.binding.childrenCounterTextView.setVisibility(View.GONE);
        }

        // check for read/unread
        if (message.isRead()) {
            holder.binding.unreadMarkImageView.setVisibility(View.GONE);
            holder.binding.usernameTextView.setTypeface(null, Typeface.NORMAL);
            holder.binding.foregroundLayout.setBackgroundColor(
                    resources.getColor(R.color.colorPrimaryDark));
        } else {
            holder.binding.unreadMarkImageView.setVisibility(View.VISIBLE);
            holder.binding.usernameTextView.setTypeface(null, Typeface.BOLD);
            holder.binding.foregroundLayout.setBackgroundColor(
                    resources.getColor(R.color.colorPrimary));
        }

        // check is verified
        holder.binding.verifiedMarkImageView.setVisibility(
                message.isVerified() ? View.VISIBLE : View.GONE);

        // check for protection
        holder.binding.encryptedImageView.setSelected(message.isProtected());

        // check for status (delivery in, delete in, dead mans in)
        if (message.getDelayedDelivery() != null) {
            String leftTime = DateUtils.elapsedTime(message.getDelayedDelivery());
            if (leftTime != null) {
                holder.binding.statusTextView.setText(resources.getString(R.string.txt_left_time_delay_delivery, leftTime));
                holder.binding.statusTextView.setBackgroundColor(
                        resources.getColor(R.color.colorDarkGreen));
                holder.binding.statusTextView.setVisibility(View.VISIBLE);
            } else {
                holder.binding.statusTextView.setVisibility(View.GONE);
            }
        } else if (message.getDestructDate() != null) {
            String leftTime = DateUtils.elapsedTime(message.getDestructDate());
            if (leftTime != null) {
                holder.binding.statusTextView.setText(resources.getString(R.string.txt_left_time_destruct, leftTime));
                holder.binding.statusTextView.setVisibility(View.VISIBLE);
            } else {
                holder.binding.statusTextView.setVisibility(View.GONE);
            }
        } else if (message.getDeadManDuration() != null) {
            String leftTime = DateUtils.deadMansTime(message.getDeadManDuration());
            if (leftTime != null) {
                holder.binding.statusTextView.setText(resources.getString(R.string.txt_left_time_dead_mans_timer, leftTime));
                holder.binding.statusTextView.setBackgroundColor(
                        resources.getColor(R.color.colorRed0));
                holder.binding.statusTextView.setVisibility(View.VISIBLE);
            } else {
                holder.binding.statusTextView.setVisibility(View.GONE);
            }
        } else {
            holder.binding.statusTextView.setVisibility(View.GONE);
        }

        Date messageDate = DateUtils.getDeliveryDate(message);
        holder.binding.dateTextView.setText(DateUtils.displayMessageDate(messageDate, resources));

        holder.binding.starredLayout.setOnClickListener(v -> {
            boolean isStarred = !message.isStarred();
            mainModel.markMessageIsStarred(message.getId(), isStarred);
            message.setStarred(isStarred);
            holder.binding.starredImageView.setSelected(isStarred);
        });

        holder.binding.starredImageView.setSelected(message.isStarred());

        // check for attachments
        if (message.isHasAttachments()) {
            holder.binding.attachmentImageView.setVisibility(View.VISIBLE);
        } else {
            holder.binding.attachmentImageView.setVisibility(View.GONE);
        }

        // check for subject
        if (message.getEncryptionMessage() != null) {
            holder.binding.subjectTextView.setVisibility(View.GONE);
            holder.binding.keyImageView.setVisibility(View.VISIBLE);
            holder.binding.subjectEncryptedTextView.setVisibility(View.GONE);
            holder.binding.decryptionProgressBar.setVisibility(View.GONE);
        } else if (!message.isSubjectEncrypted() || message.isSubjectDecrypted()) {
            holder.binding.subjectTextView.setText(message.getSubject());
            holder.binding.subjectTextView.setVisibility(View.VISIBLE);
            holder.binding.keyImageView.setVisibility(View.GONE);
            holder.binding.subjectEncryptedTextView.setVisibility(View.GONE);
            holder.binding.decryptionProgressBar.setVisibility(View.GONE);
        } else if (message.getDecryptedSubject() != null) {
            holder.binding.subjectTextView.setText(message.getDecryptedSubject());
            holder.binding.subjectTextView.setVisibility(View.VISIBLE);
            holder.binding.keyImageView.setVisibility(View.GONE);
            holder.binding.subjectEncryptedTextView.setVisibility(View.GONE);
            holder.binding.decryptionProgressBar.setVisibility(View.GONE);
        } else {
            holder.binding.subjectTextView.setVisibility(View.GONE);
            holder.binding.keyImageView.setVisibility(View.GONE);
            holder.binding.subjectEncryptedTextView.setVisibility(View.VISIBLE);
            holder.binding.decryptionProgressBar.setVisibility(View.VISIBLE);
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

    private String filterText = "";

    void filter(String filter) {
        if (TextUtils.isEmpty(filter)) {
            filterText = "";
        } else {
            filterText = filter.toLowerCase();
        }
        filter();
    }

    void clearFilter() {
        filterText = "";
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
        return isValidForFilter(messageProvider);
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

    public static class InboxMessagesViewHolder extends RecyclerView.ViewHolder {
        final ItemMessageViewHolderBinding binding;

        public InboxMessagesViewHolder(@NonNull ItemMessageViewHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
