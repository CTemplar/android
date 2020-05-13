package com.ctemplar.app.fdroid.message;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.repository.constant.MessageActions;
import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.repository.provider.UserDisplayProvider;
import com.ctemplar.app.fdroid.utils.AppUtils;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.FileUtils;
import com.ctemplar.app.fdroid.utils.PermissionCheck;
import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.ctemplar.app.fdroid.message.ViewMessagesFragment.ENCRYPTED_EXT;

public class ViewMessagesAdapter extends BaseAdapter {

    private List<MessageProvider> messageProviderList;
    private OnAttachmentDownloading onAttachmentDownloading;
    private Activity activity;

    ViewMessagesAdapter(List<MessageProvider> messageProviderList,
                        OnAttachmentDownloading onAttachmentDownloading,
                        Activity activity) {

        this.messageProviderList = messageProviderList;
        this.onAttachmentDownloading = onAttachmentDownloading;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return messageProviderList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageProviderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messageProviderList.get(position).getId();
    }

    private View getViewByFlag(LayoutInflater inflater, ViewGroup parent, MessageProvider messageData, boolean isLast) {
        final View view = inflater.inflate(R.layout.item_message_view_selector, parent, false);

        final View collapsedView = view.findViewById(R.id.collapsed);
        final View expandedView = view.findViewById(R.id.expanded);

        collapsedView.setOnClickListener(v -> {
            collapsedView.setVisibility(View.GONE);
            expandedView.setVisibility(View.VISIBLE);
        });

        expandedView.setOnClickListener(v -> {
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
        });

        if (isLast) {
            collapsedView.setVisibility(View.GONE);
            expandedView.setVisibility(View.VISIBLE);
        }

        UserDisplayProvider senderDisplay = messageData.getSenderDisplay();

        List<UserDisplayProvider> receiverDisplayList = messageData.getReceiverDisplayList();
        List<UserDisplayProvider> ccDisplayList = messageData.getCcDisplayList();
        List<UserDisplayProvider> bccDisplayList = messageData.getBccDisplayList();

        String lastAction = messageData.getLastAction();
        String folderName = messageData.getFolderName();
        String message = messageData.getContent();
        String messageFullDate = AppUtils.messageFullDate(messageData.getCreatedAt());

        boolean isHtml = messageData.isHtml();
        boolean isHasAttachment = messageData.isHasAttachments()
                || !messageData.getAttachments().isEmpty();

        // VIEW COLLAPSED
        TextView collapsedSenderTextView = view.findViewById(R.id.item_message_view_collapsed_sender);
        TextView collapsedContentTextView = view.findViewById(R.id.item_message_view_collapsed_content);
        TextView collapsedShortDateTextView = view.findViewById(R.id.item_message_view_short_date_text_view);
        TextView collapsedFolderNameTextView = view.findViewById(R.id.item_message_view_collapsed_folder_text_view);
        ImageView collapsedHasAttachmentMessageImageView = view.findViewById(R.id.item_message_view_holder_attachment_image_view);
        ImageView collapsedReplyMessageImageView = view.findViewById(R.id.item_message_view_holder_reply_image_view);

        // VIEW EXPANDED
        TextView senderTextView = view.findViewById(R.id.item_message_view_expanded_sender_name);
        TextView receiverTextView = view.findViewById(R.id.item_message_view_expanded_receiver_name);
        TextView shortDateTextView = view.findViewById(R.id.item_message_view_expanded_short_date_text_view);
        TextView statusTextView = view.findViewById(R.id.item_message_view_expanded_status);
        TextView folderNameTextView = view.findViewById(R.id.item_message_view_expanded_folder_name_text_view);
        TextView detailsTextView = view.findViewById(R.id.item_message_view_expanded_details);
        TextView senderEmailTextView = view.findViewById(R.id.item_message_view_from_email);
        TextView receiverEmailTextView = view.findViewById(R.id.item_message_view_to_email);
        View ccLayout = view.findViewById(R.id.item_message_view_CC_layout);
        TextView ccEmailTextView = view.findViewById(R.id.item_message_view_CC_email);
        View bccLayout = view.findViewById(R.id.item_message_view_BCC_layout);
        ImageView hasAttachmentMessageImageView = view.findViewById(R.id.item_message_view_expanded_attachment_image_view);
        ImageView replyMessageImageView = view.findViewById(R.id.item_message_view_expanded_reply_image_view);
        TextView bccEmailTextView = view.findViewById(R.id.item_message_view_BCC_email);
        TextView fullDateEmailTextView = view.findViewById(R.id.item_message_view_date_text_view);
        WebView contentWebView = view.findViewById(R.id.item_message_view_expanded_content);
        TextView contentText = view.findViewById(R.id.item_message_text_view_expanded_content);
        ProgressBar progressBar = view.findViewById(R.id.item_message_view_expanded_progress_bar);
        RecyclerView attachmentsRecyclerView = view.findViewById(R.id.item_message_view_expanded_attachment);
        ViewGroup expandedCredentialsLayout = view.findViewById(R.id.item_message_view_expanded_credentials);
        View credentialsDivider = view.findViewById(R.id.item_message_view_expanded_credentials_divider);

        detailsTextView.setOnClickListener(v -> {
            int detailsVisibility = expandedCredentialsLayout.getVisibility();
            if (detailsVisibility == View.VISIBLE) {
                expandedCredentialsLayout.setVisibility(View.GONE);
                credentialsDivider.setVisibility(View.GONE);
                detailsTextView.setText(view.getResources().getText(R.string.txt_more_details));
            } else {
                expandedCredentialsLayout.setVisibility(View.VISIBLE);
                credentialsDivider.setVisibility(View.VISIBLE);
                detailsTextView.setText(view.getResources().getText(R.string.txt_less_details));
            }
        });

        collapsedSenderTextView.setText(senderDisplay.getName());
        collapsedContentTextView.setText(EditTextUtils.fromHtml(message));
        collapsedShortDateTextView.setText(AppUtils.messageDate(messageData.getCreatedAt()));

        senderTextView.setText(senderDisplay.getName());
        receiverTextView.setText(userDisplayListToNamesString(receiverDisplayList));
        shortDateTextView.setText(AppUtils.messageDate(messageData.getCreatedAt()));
        fullDateEmailTextView.setText(view.getResources().getString(R.string.txt_date_format, messageFullDate));

        // check for folder
        if (EditTextUtils.isNotEmpty(folderName)) {
            folderNameTextView.setText(folderName);
            collapsedFolderNameTextView.setText(folderName);
        }

        // check for status (time delete, delayed delivery)
        if (EditTextUtils.isNotEmpty(messageData.getDelayedDelivery())) {
            String leftTime = AppUtils.elapsedTime(messageData.getDelayedDelivery());
            if (EditTextUtils.isNotEmpty(leftTime)) {
                statusTextView.setText(view.getResources().getString(R.string.txt_left_time_delay_delivery, leftTime));
                statusTextView.setBackgroundColor(view.getResources().getColor(R.color.colorDarkGreen));
            } else {
                statusTextView.setVisibility(View.GONE);
            }
        } else if (EditTextUtils.isNotEmpty(messageData.getDestructDate())) {
            String leftTime = AppUtils.elapsedTime(messageData.getDestructDate());
            if (EditTextUtils.isNotEmpty(leftTime)) {
                statusTextView.setText(view.getResources().getString(R.string.txt_left_time_destruct, leftTime));
            } else {
                statusTextView.setVisibility(View.GONE);
            }
        } else if (EditTextUtils.isNotEmpty(messageData.getDeadManDuration())) {
            String leftTime = AppUtils.deadMansTime(Long.parseLong(messageData.getDeadManDuration()));
            if (EditTextUtils.isNotEmpty(leftTime)) {
                statusTextView.setText(view.getResources().getString(R.string.txt_left_time_dead_mans_timer, leftTime));
                statusTextView.setBackgroundColor(view.getResources().getColor(R.color.colorRed0));
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

        // display message
        if (isHtml) {
            String messageWithStyle = "<style type=\"text/css\">*{width:auto;max-width:100%;}</style>" + message;
            String encodedContent = Base64.encodeToString(messageWithStyle.getBytes(), Base64.NO_PADDING);
            WebSettings webViewSettings = contentWebView.getSettings();
            webViewSettings.setLoadWithOverviewMode(true);
            webViewSettings.setBuiltInZoomControls(true);
            webViewSettings.setJavaScriptEnabled(false);
            webViewSettings.setAllowFileAccess(false);
            webViewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            contentWebView.clearCache(true);
            contentWebView.loadData(encodedContent, "text/html", "base64");
            contentWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    activity.startActivity(urlIntent);
                    return true;
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            contentText.setText(EditTextUtils.fromHtml(message));
        }

        List<AttachmentProvider> attachmentList = messageData.getAttachments();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                attachmentsRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        attachmentsRecyclerView.setLayoutManager(mLayoutManager);

        MessageAttachmentAdapter messageAttachmentAdapter = new MessageAttachmentAdapter(attachmentList);
        attachmentsRecyclerView.setAdapter(messageAttachmentAdapter);
        messageAttachmentAdapter.getOnClickAttachmentLink().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer position) {
                File externalStorageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                AttachmentProvider attachmentProvider = messageAttachmentAdapter.getAttachment(position);
                String documentLink = attachmentProvider.getDocumentLink();
                if (documentLink == null) {
                    Toast.makeText(activity, activity.getString(R.string.error_attachment_url), Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri documentUri = Uri.parse(documentLink);
                File file = FileUtils.generateFileName(AppUtils.getFileNameFromURL(documentLink), externalStorageFile);
                String fileName = file == null ? "" : file.getName();
                String localFileName = attachmentProvider.isEncrypted() ? fileName + ENCRYPTED_EXT : fileName;

                DownloadManager.Request documentRequest = new DownloadManager.Request(documentUri);
                documentRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, localFileName);
                documentRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                if (activity != null && PermissionCheck.readAndWriteExternalStorage(activity)) {
                    DownloadManager downloadManager = (DownloadManager) activity.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.enqueue(documentRequest);
                    Toast.makeText(activity, activity.getString(R.string.toast_download_started), Toast.LENGTH_SHORT).show();
                    attachmentProvider.setFileName(fileName);
                    onAttachmentDownloading.onStart(attachmentProvider);
                }
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
            }

            @Override
            public void onComplete() {

            }
        });

        return view;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MessageProvider messageData = messageProviderList.get(position);
        return getViewByFlag(inflater, parent, messageData, position + 1 == getCount());
    }

    private String userDisplayListToNamesString(List<UserDisplayProvider> userDisplayProviderList) {
        List<String> userNameList = new ArrayList<>();
        for (UserDisplayProvider userDisplayProvider : userDisplayProviderList) {
            String name = userDisplayProvider.getName();
            if (name != null) {
                userNameList.add(name);
            }
        }
        return TextUtils.join(", ", userNameList);
    }

    private String userDisplayListToString(List<UserDisplayProvider> userDisplayProviderList) {
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