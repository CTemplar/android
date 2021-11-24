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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemMessageViewHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MessageActions;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class InboxMessagesAdapter extends RecyclerView.Adapter<InboxMessagesAdapter.InboxMessagesViewHolder> {
    private LayoutInflater inflater;

    private final List<MessageProvider> messageList;
    private final List<MessageProvider> filteredList;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();

    private final MainActivityViewModel mainModel;

    private OnReachedBottomCallback onReachedBottomCallback;
    private Handler onReachedBottomCallbackHandler;
    private boolean selectionState = false;
    private final Set<Long> selectedMessages = new HashSet<>();
    private final MutableLiveData<Boolean> selectionStateLiveData = new MutableLiveData<>(false);

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
        if (position == getItemCount() - 1 && onReachedBottomCallback != null) {
            if (onReachedBottomCallbackHandler != null) {
                onReachedBottomCallbackHandler.post(() -> onReachedBottomCallback.onReachedBottom());
            } else {
                onReachedBottomCallback.onReachedBottom();
            }
        }
        holder.update(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public MessageProvider get(int position) {
        return filteredList.get(position);
    }

    public MessageProvider getLast() {
        return messageList.isEmpty() ? null : messageList.get(messageList.size() - 1);
    }

    public MessageProvider removeAt(int position) {
        MessageProvider removedMessage = filteredList.remove(position);
        notifyItemRemoved(position);
        return removedMessage;
    }

    public void removeMessages(Collection<MessageProvider> messages) {
        for (MessageProvider message : messages) {
            int position = filteredList.indexOf(message);
            filteredList.remove(message);
            notifyItemRemoved(position);
        }
    }

    public void restoreMessages(Map<Integer, MessageProvider> messages) {
        for (Map.Entry<Integer, MessageProvider> messagesEntry : messages.entrySet()) {
            int position = messagesEntry.getKey();
            filteredList.add(position, messagesEntry.getValue());
            notifyItemInserted(position);
        }
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

    public void setSelectionState() {
        setSelectionState(true);
    }

    public void setSelectionState(boolean active) {
        if (selectionState == active) {
            return;
        }
        this.selectionState = active;
        if (!active) {
            this.selectedMessages.clear();
        }
        selectionStateLiveData.setValue(active);
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (MessageProvider messageProvider : filteredList) {
            selectedMessages.add(messageProvider.getId());
        }
        notifyDataSetChanged();
    }

    private void changeSelectState(MessageProvider messageProvider) {
        long id = messageProvider.getId();
        if (!selectedMessages.remove(id)) {
            selectedMessages.add(id);
        } else {
            if (selectedMessages.isEmpty()) {
                setSelectionState(false);
                return;
            }
        }
        notifyItemChanged(filteredList.indexOf(messageProvider));
    }

    public LiveData<Boolean> getSelectionState() {
        return selectionStateLiveData;
    }

    public boolean getSelectionStateValue() {
        if (selectionStateLiveData.getValue() == null) {
            return false;
        }
        return selectionStateLiveData.getValue();
    }

    public Long[] getSelectedMessages() {
        return selectedMessages.toArray(new Long[0]);
    }

    public Map<Integer, MessageProvider> getSelectedMessagesMap() {
        Map<Integer, MessageProvider> selectedMessagesMap = new HashMap<>();
        for (Long selectedMessage : selectedMessages) {
            for (int i = 0; i < filteredList.size(); i++) {
                MessageProvider messageProvider = filteredList.get(i);
                if (selectedMessage.equals(messageProvider.getId())) {
                    selectedMessagesMap.put(i, messageProvider);
                }
            }
        }
        return selectedMessagesMap;
    }

    public class InboxMessagesViewHolder extends RecyclerView.ViewHolder {
        final ItemMessageViewHolderBinding binding;

        public InboxMessagesViewHolder(@NonNull ItemMessageViewHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(MessageProvider message) {
            final Resources resources = binding.getRoot().getResources();
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
            if (EditTextUtils.isNotEmpty(userDisplay.getName())) {
                binding.usernameTextView.setText(userDisplay.getName());
            } else {
                binding.usernameTextView.setText(userDisplay.getEmail());
            }

            // check for last action (reply, reply all, forward)
            String lastActionThread = message.getLastActionThread();
            if (TextUtils.isEmpty(lastActionThread)) {
                binding.replyMarkImageView.setVisibility(View.GONE);
            } else {
                switch (lastActionThread) {
                    case MessageActions.REPLY:
                        binding.replyMarkImageView.setImageResource(R.drawable.ic_reply_message);
                        break;
                    case MessageActions.REPLY_ALL:
                        binding.replyMarkImageView.setImageResource(R.drawable.ic_reply_all_message);
                        break;
                    case MessageActions.FORWARD:
                        binding.replyMarkImageView.setImageResource(R.drawable.ic_forward_message);
                        break;
                }
                binding.replyMarkImageView.setVisibility(View.VISIBLE);
            }

            // check for children count
            if (message.isHasChildren()) {
                int chainCount = message.getChildrenCount() + 1;
                binding.childrenCounterTextView.setText(String.valueOf(chainCount));
                binding.childrenCounterTextView.setVisibility(View.VISIBLE);
            } else {
                binding.childrenCounterTextView.setVisibility(View.GONE);
            }

            // check for read/unread
            int backgroundColor;
            if (message.isRead()) {
                binding.unreadMarkImageView.setVisibility(View.GONE);
                binding.usernameTextView.setTypeface(null, Typeface.NORMAL);
                backgroundColor = resources.getColor(R.color.colorPrimaryDark);
            } else {
                binding.unreadMarkImageView.setVisibility(View.VISIBLE);
                binding.usernameTextView.setTypeface(null, Typeface.BOLD);
                backgroundColor = resources.getColor(R.color.colorPrimary);
            }
            binding.foregroundLayout.setBackgroundColor(backgroundColor);
            binding.selectedLayout.setBackgroundColor(backgroundColor);

            // check is verified
            binding.verifiedMarkImageView.setVisibility(message.isVerified() ? View.VISIBLE : View.GONE);

            // check for protection
            binding.encryptedImageView.setSelected(message.isProtected());

            // check for status (delivery in, delete in, dead mans in)
            if (message.getDelayedDelivery() != null) {
                String leftTime = DateUtils.elapsedTime(message.getDelayedDelivery());
                if (leftTime != null) {
                    binding.statusTextView.setText(resources.getString(R.string.txt_left_time_delay_delivery, leftTime));
                    binding.statusTextView.setBackgroundColor(resources.getColor(R.color.colorDarkGreen));
                    binding.statusTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.statusTextView.setVisibility(View.GONE);
                }
            } else if (message.getDestructDate() != null) {
                String leftTime = DateUtils.elapsedTime(message.getDestructDate());
                if (leftTime != null) {
                    binding.statusTextView.setText(resources.getString(R.string.txt_left_time_destruct, leftTime));
                    binding.statusTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.statusTextView.setVisibility(View.GONE);
                }
            } else if (message.getDeadManDuration() != null) {
                String leftTime = DateUtils.deadMansTime(message.getDeadManDuration());
                if (leftTime != null) {
                    binding.statusTextView.setText(resources.getString(R.string.txt_left_time_dead_mans_timer, leftTime));
                    binding.statusTextView.setBackgroundColor(resources.getColor(R.color.colorRed0));
                    binding.statusTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.statusTextView.setVisibility(View.GONE);
                }
            } else {
                binding.statusTextView.setVisibility(View.GONE);
            }

            binding.dateTextView.setText(
                    DateUtils.displayMessageDate(DateUtils.getDeliveryDate(message), resources));

            binding.starredLayout.setOnClickListener(v -> {
                boolean isStarred = !message.isStarred();
                mainModel.markMessageIsStarred(message.getId(), isStarred);
                message.setStarred(isStarred);
                binding.starredImageView.setSelected(isStarred);
            });

            binding.starredImageView.setSelected(message.isStarred());

            // check for attachments
            if (message.isHasAttachments()) {
                binding.attachmentImageView.setVisibility(View.VISIBLE);
            } else {
                binding.attachmentImageView.setVisibility(View.GONE);
            }

            // check for subject
            if (message.getEncryptionMessage() != null) {
                binding.subjectTextView.setVisibility(View.GONE);
                binding.keyImageView.setVisibility(View.VISIBLE);
                binding.subjectEncryptedTextView.setVisibility(View.GONE);
                binding.decryptionProgressBar.setVisibility(View.GONE);
            } else if (!message.isSubjectEncrypted() || message.isSubjectDecrypted()) {
                binding.subjectTextView.setText(message.getSubject());
                binding.subjectTextView.setVisibility(View.VISIBLE);
                binding.keyImageView.setVisibility(View.GONE);
                binding.subjectEncryptedTextView.setVisibility(View.GONE);
                binding.decryptionProgressBar.setVisibility(View.GONE);
            } else if (message.getDecryptedSubject() != null) {
                binding.subjectTextView.setText(message.getDecryptedSubject());
                binding.subjectTextView.setVisibility(View.VISIBLE);
                binding.keyImageView.setVisibility(View.GONE);
                binding.subjectEncryptedTextView.setVisibility(View.GONE);
                binding.decryptionProgressBar.setVisibility(View.GONE);
            } else {
                binding.subjectTextView.setVisibility(View.GONE);
                binding.keyImageView.setVisibility(View.GONE);
                binding.subjectEncryptedTextView.setVisibility(View.VISIBLE);
                binding.decryptionProgressBar.setVisibility(View.VISIBLE);
            }

            boolean isMessageSelected = selectedMessages.contains(message.getId());
            binding.foregroundLayout.setOnLongClickListener(v -> {
                if (selectionState) {
                    return false;
                }
                selectedMessages.add(message.getId());
                setSelectionState();
                return false;
            });
            binding.foregroundLayout.setOnClickListener(v -> {
                if (selectionState) {
                    changeSelectState(message);
                } else {
                    onClickSubject.onNext(message.getId());
                }
            });
            if (selectionState) {
                binding.selectedLayout.setVisibility(View.VISIBLE);
                binding.foregroundSelectedView.setSelected(isMessageSelected);
                binding.checkbox.setChecked(isMessageSelected);
            } else {
                binding.selectedLayout.setVisibility(View.GONE);
            }
            binding.checkbox.setOnClickListener(v -> changeSelectState(message));
        }
    }
}
