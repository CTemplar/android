package mobileapp.ctemplar.com.ctemplarapp.message;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MessageActions;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.EncryptionMessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.HtmlUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import timber.log.Timber;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {
    private final List<MessageProvider> items = new ArrayList<>();
    private final List<MessageProvider> expanded = new ArrayList<>();
    private OnAttachmentDownloading onAttachmentDownloading;
    private MessageViewActionCallback callback;
    private final UserStore userStore;
    private Context context;
    private LayoutInflater inflater;

    MessagesRecyclerViewAdapter() {
        userStore = CTemplarApp.getUserStore();
    }

    public void setOnAttachmentDownloadingCallback(OnAttachmentDownloading onAttachmentDownloading) {
        this.onAttachmentDownloading = onAttachmentDownloading;
    }

    public void setCallback(MessageViewActionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        context = recyclerView.getContext();
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.item_message_view_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<MessageProvider> items) {
        this.items.clear();
        this.items.addAll(items);
        if (!items.isEmpty()) {
            MessageProvider item = items.get(items.size() - 1);
            if (!expanded.contains(item)) {
                expanded.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public void updateItem(MessageProvider item) {
        int index = items.indexOf(item);
        if (index < 0) {
            return;
        }
        notifyItemChanged(index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View collapsedView;
        private final View expandedView;

        private final View expandedShortView;


        // VIEW COLLAPSED
        final TextView collapsedSenderTextView;
        final TextView collapsedContentTextView;
        final TextView collapsedShortDateTextView;
        final TextView collapsedFolderNameTextView;
        final ImageView collapsedVerifiedMarkImageView;
        final ImageView collapsedHasAttachmentMessageImageView;
        final ImageView collapsedReplyMessageImageView;

        // VIEW EXPANDED
        final TextView senderTextView;
        final TextView receiverTextView;
        final TextView shortDateTextView;
        final TextView statusTextView;
        final TextView folderNameTextView;
        final TextView detailsTextView;
        final TextView senderEmailTextView;
        final TextView receiverEmailTextView;
        final View ccLayout;
        final TextView ccEmailTextView;
        final View bccLayout;
        final ImageView verifiedMarkImageView;
        final ImageView hasAttachmentMessageImageView;
        final ImageView replyMessageImageView;
        final TextView bccEmailTextView;
        final TextView fullDateEmailTextView;
        final WebView contentWebView;
        final TextView contentText;
        final ProgressBar progressBar;
        final RecyclerView attachmentsRecyclerView;
        final ViewGroup expandedCredentialsLayout;
        final View credentialsDivider;
        final ViewGroup encryptedMessageLockLayout;
        final Button encryptedMessageDecryptButton;
        final ViewGroup contentLayout;
        private final View encryptedMessageLockView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            collapsedView = itemView.findViewById(R.id.collapsed);
            expandedView = itemView.findViewById(R.id.expanded);
            expandedShortView = expandedView.findViewById(R.id.item_message_view_expanded_short);


            // VIEW COLLAPSED
            collapsedSenderTextView = collapsedView.findViewById(R.id.item_message_view_collapsed_sender);
            collapsedContentTextView = collapsedView.findViewById(R.id.item_message_view_collapsed_content);
            collapsedShortDateTextView = collapsedView.findViewById(R.id.item_message_view_short_date_text_view);
            collapsedFolderNameTextView = collapsedView.findViewById(R.id.item_message_view_collapsed_folder_text_view);
            collapsedVerifiedMarkImageView = collapsedView.findViewById(R.id.item_message_view_collapsed_verified_mark_image_view);
            collapsedHasAttachmentMessageImageView = collapsedView.findViewById(R.id.item_message_view_holder_attachment_image_view);
            collapsedReplyMessageImageView = collapsedView.findViewById(R.id.item_message_view_holder_reply_image_view);
            encryptedMessageLockView = collapsedView.findViewById(R.id.encrypted_message_lock_image_view);

            // VIEW EXPANDED
            senderTextView = expandedView.findViewById(R.id.item_message_view_expanded_sender_name);
            receiverTextView = expandedView.findViewById(R.id.item_message_view_expanded_receiver_name);
            shortDateTextView = expandedView.findViewById(R.id.item_message_view_expanded_short_date_text_view);
            statusTextView = expandedView.findViewById(R.id.item_message_view_expanded_status);
            folderNameTextView = expandedView.findViewById(R.id.item_message_view_expanded_folder_name_text_view);
            detailsTextView = expandedView.findViewById(R.id.item_message_view_expanded_details);
            senderEmailTextView = expandedView.findViewById(R.id.item_message_view_from_email);
            receiverEmailTextView = expandedView.findViewById(R.id.item_message_view_to_email);
            ccLayout = expandedView.findViewById(R.id.item_message_view_CC_layout);
            ccEmailTextView = expandedView.findViewById(R.id.item_message_view_CC_email);
            bccLayout = expandedView.findViewById(R.id.item_message_view_BCC_layout);
            verifiedMarkImageView = expandedView.findViewById(R.id.item_message_view_expanded_verified_mark_image_view);
            hasAttachmentMessageImageView = expandedView.findViewById(R.id.item_message_view_expanded_attachment_image_view);
            replyMessageImageView = expandedView.findViewById(R.id.item_message_view_expanded_reply_image_view);
            bccEmailTextView = expandedView.findViewById(R.id.item_message_view_BCC_email);
            fullDateEmailTextView = expandedView.findViewById(R.id.item_message_view_date_text_view);
            contentWebView = expandedView.findViewById(R.id.item_message_view_expanded_content);
            contentText = expandedView.findViewById(R.id.item_message_text_view_expanded_content);
            progressBar = expandedView.findViewById(R.id.item_message_view_expanded_progress_bar);
            attachmentsRecyclerView = expandedView.findViewById(R.id.item_message_view_expanded_attachment);
            expandedCredentialsLayout = expandedView.findViewById(R.id.item_message_view_expanded_credentials);
            credentialsDivider = expandedView.findViewById(R.id.item_message_view_expanded_credentials_divider);
            encryptedMessageLockLayout = expandedView.findViewById(R.id.encrypted_message_lock_layout);
            encryptedMessageDecryptButton = expandedView.findViewById(R.id.password_encrypted_message_decrypt_button);
            contentLayout = expandedView.findViewById(R.id.content_layout);
        }

        public void update(MessageProvider item) {
            UserDisplayProvider senderDisplay = item.getSenderDisplay();

            List<UserDisplayProvider> receiverDisplayList = item.getReceiverDisplayList();
            List<UserDisplayProvider> ccDisplayList = item.getCcDisplayList();
            List<UserDisplayProvider> bccDisplayList = item.getBccDisplayList();

            String lastAction = item.getLastAction();
            String folderName = item.getFolderName();
            Date messageDate = DateUtils.getDeliveryDate(item);

            boolean isHtml = item.isHtml();
            boolean isVerified = item.isVerified();
            boolean isHasAttachment = item.isHasAttachments() || item.getAttachments().size() > 0;

            Resources resources = context.getResources();
            boolean expand = expanded.contains(item);
            expandedView.setVisibility(expand ? View.VISIBLE : View.GONE);
            collapsedView.setVisibility(expand ? View.GONE : View.VISIBLE);
            expandedShortView.setOnClickListener(v -> switchVisibility(item));
            collapsedView.setOnClickListener(v -> switchVisibility(item));

            detailsTextView.setOnClickListener(v -> {
                int detailsVisibility = expandedCredentialsLayout.getVisibility();
                if (detailsVisibility == View.VISIBLE) {
                    expandedCredentialsLayout.setVisibility(View.GONE);
                    credentialsDivider.setVisibility(View.GONE);
                    detailsTextView.setText(resources.getText(R.string.txt_more_details));
                } else {
                    expandedCredentialsLayout.setVisibility(View.VISIBLE);
                    credentialsDivider.setVisibility(View.VISIBLE);
                    detailsTextView.setText(resources.getText(R.string.txt_less_details));
                }
            });

            collapsedSenderTextView.setText(senderDisplay.getName());
            collapsedShortDateTextView.setText(DateUtils.displayMessageDate(messageDate, resources));

            senderTextView.setText(senderDisplay.getName());
            receiverTextView.setText(userDisplayListToNamesString(receiverDisplayList));
            shortDateTextView.setText(DateUtils.displayMessageDate(messageDate, resources));
            fullDateEmailTextView.setText(resources.getString(R.string.txt_date_format,
                    DateUtils.messageFullDate(messageDate)));

            // check for folder
            if (EditTextUtils.isNotEmpty(folderName)) {
                folderNameTextView.setText(folderName);
                collapsedFolderNameTextView.setText(folderName);
            }

            // check for status (time delete, delayed delivery)
            if (item.getDelayedDelivery() != null) {
                String leftTime = DateUtils.elapsedTime(item.getDelayedDelivery());
                if (leftTime != null) {
                    statusTextView.setText(resources.getString(R.string.txt_left_time_delay_delivery, leftTime));
                    statusTextView.setBackgroundColor(resources.getColor(R.color.colorDarkGreen));
                } else {
                    statusTextView.setVisibility(View.GONE);
                }
            } else if (item.getDestructDate() != null) {
                String leftTime = DateUtils.elapsedTime(item.getDestructDate());
                if (leftTime != null) {
                    statusTextView.setText(resources.getString(R.string.txt_left_time_destruct, leftTime));
                } else {
                    statusTextView.setVisibility(View.GONE);
                }
            } else if (item.getDeadManDuration() != null) {
                String leftTime = DateUtils.deadMansTime(item.getDeadManDuration());
                if (leftTime != null) {
                    statusTextView.setText(resources.getString(R.string.txt_left_time_dead_mans_timer, leftTime));
                    statusTextView.setBackgroundColor(resources.getColor(R.color.colorRed0));
                } else {
                    statusTextView.setVisibility(View.GONE);
                }
            } else {
                statusTextView.setVisibility(View.GONE);
            }

            String senderUserDisplay = userDisplayListToString(Collections.singletonList(senderDisplay));
            senderEmailTextView.setText(senderUserDisplay);

            String receiversDisplayString = userDisplayListToString(receiverDisplayList);
            receiverEmailTextView.setText(receiversDisplayString);

            // check for cc
            if (!ccDisplayList.isEmpty()) {
                String ccDisplayString = userDisplayListToString(ccDisplayList);
                ccEmailTextView.setText(ccDisplayString);
                ccLayout.setVisibility(View.VISIBLE);
            } else {
                ccLayout.setVisibility(View.GONE);
            }

            // check for bcc
            if (!bccDisplayList.isEmpty()) {
                String bccDisplayString = userDisplayListToString(bccDisplayList);
                bccEmailTextView.setText(bccDisplayString);
                bccLayout.setVisibility(View.VISIBLE);
            } else {
                bccLayout.setVisibility(View.GONE);
            }

            // verified mark
            if (isVerified) {
                collapsedVerifiedMarkImageView.setVisibility(View.VISIBLE);
                verifiedMarkImageView.setVisibility(View.VISIBLE);
            } else {
                collapsedVerifiedMarkImageView.setVisibility(View.GONE);
                verifiedMarkImageView.setVisibility(View.GONE);
            }

            // attachment
            if (isHasAttachment) {
                collapsedHasAttachmentMessageImageView.setVisibility(View.VISIBLE);
                hasAttachmentMessageImageView.setVisibility(View.VISIBLE);
            } else {
                collapsedHasAttachmentMessageImageView.setVisibility(View.GONE);
                hasAttachmentMessageImageView.setVisibility(View.GONE);
            }

            // check for last action (reply, reply all, forward)
            if (TextUtils.isEmpty(lastAction)) {
                replyMessageImageView.setVisibility(View.GONE);
                collapsedReplyMessageImageView.setVisibility(View.GONE);
            } else {
                switch (lastAction) {
                    case MessageActions.REPLY:
                        replyMessageImageView.setImageResource(R.drawable.ic_reply_message);
                        collapsedReplyMessageImageView.setImageResource(R.drawable.ic_reply_message);
                        break;
                    case MessageActions.REPLY_ALL:
                        replyMessageImageView.setImageResource(R.drawable.ic_reply_all_message);
                        collapsedReplyMessageImageView.setImageResource(R.drawable.ic_reply_all_message);
                        break;
                    case MessageActions.FORWARD:
                        replyMessageImageView.setImageResource(R.drawable.ic_forward_message);
                        collapsedReplyMessageImageView.setImageResource(R.drawable.ic_forward_message);
                        break;
                }
                replyMessageImageView.setVisibility(View.VISIBLE);
                collapsedReplyMessageImageView.setVisibility(View.VISIBLE);
            }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            contentWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            contentWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
            EncryptionMessageProvider encryptionMessage = item.getEncryptionMessage();
            boolean isPasswordEncrypted = encryptionMessage != null && !encryptionMessage.isMessageDecrypted();
            if (isPasswordEncrypted) {
                contentLayout.setVisibility(View.GONE);
                encryptedMessageLockLayout.setVisibility(View.VISIBLE);
                collapsedContentTextView.setVisibility(View.GONE);
                encryptedMessageLockView.setVisibility(View.VISIBLE);
                encryptedMessageDecryptButton.setOnClickListener(v -> {
                    if (callback != null) {
                        callback.onDecryptPasswordEncryptedMessageClick(item);
                    }
                });
            } else {
                collapsedContentTextView.setVisibility(View.VISIBLE);
                contentLayout.setVisibility(View.VISIBLE);
                encryptedMessageLockLayout.setVisibility(View.GONE);
                encryptedMessageLockView.setVisibility(View.GONE);
                String messageContent;
                if (encryptionMessage == null) {
                    messageContent = item.getContent();
                } else {
                    messageContent = encryptionMessage.getDecryptedMessage();
                }
                Spanned spannedMessageContent = HtmlUtils.fromHtml(messageContent);
                collapsedContentTextView.setText(spannedMessageContent);
                if (isHtml) {
                    String encodedMessageContent = Base64.encodeToString(
                            HtmlUtils.formatHtml(messageContent), Base64.NO_PADDING);
                    WebSettings webViewSettings = contentWebView.getSettings();
                    webViewSettings.setLoadWithOverviewMode(true);
                    webViewSettings.setJavaScriptEnabled(false);
                    webViewSettings.setAllowFileAccess(false);
                    webViewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                    webViewSettings.setLoadsImagesAutomatically(!userStore.isBlockExternalImagesEnabled());
                    contentWebView.clearCache(true);
                    contentWebView.loadData(encodedMessageContent, "text/html", "base64");
                    ThemeUtils.setWebViewDarkTheme(context, contentWebView);
                    contentWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            try {
                                context.startActivity(urlIntent);
                            } catch (Throwable e) {
                                Timber.e(e);
                            }
                            return true;
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    contentText.setText(spannedMessageContent);
                }
            }

            List<AttachmentProvider> attachmentList = item.getAttachments();
            MessageAttachmentAdapter messageAttachmentAdapter = new MessageAttachmentAdapter(attachmentList);
            attachmentsRecyclerView.setAdapter(messageAttachmentAdapter);
            messageAttachmentAdapter.getOnClickAttachmentLink().subscribe(new Observer<Integer>() {
                @Override
                public void onSubscribe(@NotNull Disposable d) {

                }

                @Override
                public void onNext(@NotNull Integer position) {
                    if (encryptionMessage != null && !encryptionMessage.isMessageDecrypted()) {
                        ToastUtils.showToast(context, context.getString(R.string.firstly_decrypt_message));
                        return;
                    }
                    AttachmentProvider attachmentProvider = messageAttachmentAdapter.getAttachment(position);
                    onAttachmentDownloading.onStart(attachmentProvider, item);
                }

                @Override
                public void onError(@NotNull Throwable e) {
                    Timber.e(e, "MessageAttachmentAdapter");
                }

                @Override
                public void onComplete() {

                }
            });

        }

        private void switchVisibility(MessageProvider item) {
            setExpanded(!expanded.contains(item), item);
        }

        private void setExpanded(boolean expand, MessageProvider item) {
            if (expand) {
                if (expanded.contains(item)) {
                    Timber.w("Already expanded %s", item);
                    return;
                }
                expanded.add(item);
            } else {
                if (!expanded.contains(item)) {
                    Timber.w("Already collapsed %s", item);
                    return;
                }
                expanded.remove(item);
            }
            collapsedView.setVisibility(expand ? View.GONE : View.VISIBLE);
            expandedView.setVisibility(expand ? View.VISIBLE : View.GONE);
        }
    }

    private static String userDisplayListToNamesString(List<UserDisplayProvider> userDisplayProviderList) {
        List<String> userNameList = new ArrayList<>();
        for (UserDisplayProvider userDisplayProvider : userDisplayProviderList) {
            String name = userDisplayProvider.getName();
            if (name != null) {
                userNameList.add(name);
            }
        }
        return TextUtils.join(", ", userNameList);
    }

    private static String userDisplayListToString(List<UserDisplayProvider> userDisplayProviderList) {
        List<String> userDisplayList = new ArrayList<>();
        for (UserDisplayProvider userDisplayProvider : userDisplayProviderList) {
            String userDisplay = "";
            String name = userDisplayProvider.getName();
            String email = userDisplayProvider.getEmail();
            if (name != null) {
                userDisplay += name;
            }
            if (email != null) {
                userDisplay += " <" + email + ">";
            }
            userDisplayList.add(userDisplay);
        }
        return TextUtils.join(", ", userDisplayList);
    }
}
