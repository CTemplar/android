package mobileapp.ctemplar.com.ctemplarapp.main;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MessageActions;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesViewHolder> {

    private final PublishSubject<Long> onClickSubject = PublishSubject.create();
    private List<MessageProvider> messageList;
    private List<MessageProvider> filteredList;
    private final MainActivityViewModel mainModel;
    private OnReachedBottomCallback onReachedBottomCallback;
    private Handler onReachedBottomCallbackHandler;

    InboxMessagesAdapter(MainActivityViewModel mainModel) {
        this.mainModel = mainModel;
        this.messageList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
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
        if (lastActionThread == null) {
            holder.imgReply.setVisibility(View.GONE);
        } else if (lastActionThread.equals(MessageActions.REPLY_ALL)) {
            holder.imgReply.setImageResource(R.drawable.ic_reply_all_message);
        } else if (lastActionThread.equals(MessageActions.FORWARD)) {
            holder.imgReply.setImageResource(R.drawable.ic_forward_message);
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
        } else {
            holder.imgUnread.setVisibility(View.VISIBLE);
            holder.txtUsername.setTypeface(null, Typeface.BOLD);
        }

        // check for protection
        holder.imgEncrypted.setSelected(message.isProtected());

        // check for status (delivery in, delete in, dead mans in)
        if (!TextUtils.isEmpty(message.getDelayedDelivery())) {
            String leftTime = AppUtils.elapsedTime(message.getDelayedDelivery());
            if (leftTime != null) {
                holder.txtStatus.setText(holder.root.getResources().getString(R.string.txt_left_time_delay_delivery, leftTime));
                holder.txtStatus.setBackgroundColor(holder.root.getResources().getColor(R.color.colorDarkGreen));
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else if (!TextUtils.isEmpty(message.getDestructDate())) {
            String leftTime = AppUtils.elapsedTime(message.getDestructDate());
            if (leftTime != null) {
                holder.txtStatus.setText(holder.root.getResources().getString(R.string.txt_left_time_destruct, leftTime));
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else if (!TextUtils.isEmpty(message.getDeadManDuration())) {
            String leftTime = AppUtils.deadMansTime(Long.valueOf(message.getDeadManDuration()));
            if (leftTime != null) {
                holder.txtStatus.setText(holder.root.getResources().getString(R.string.txt_left_time_dead_mans_timer, leftTime));
                holder.txtStatus.setBackgroundColor(holder.root.getResources().getColor(R.color.colorRed0));
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } else {
            holder.txtStatus.setVisibility(View.GONE);
        }

        // format creation date
        if (!TextUtils.isEmpty(message.getCreatedAt())) {
            String creationDate = AppUtils.messageDate(message.getCreatedAt());
            holder.txtDate.setText(creationDate);
        }

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
        boolean isSubjectEncrypted = message.isSubjectEncrypted();
        String subjectText = message.getSubject();
        if (isSubjectEncrypted) {
            holder.txtSubjectEncrypted.setVisibility(View.VISIBLE);
            holder.txtSubject.setVisibility(View.INVISIBLE);
        } else {
            holder.txtSubject.setText(subjectText);
            holder.txtSubjectEncrypted.setVisibility(View.GONE);
            holder.txtSubject.setVisibility(View.VISIBLE);
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

    public interface OnReachedBottomCallback {
        void onReachedBottom();
    }
}