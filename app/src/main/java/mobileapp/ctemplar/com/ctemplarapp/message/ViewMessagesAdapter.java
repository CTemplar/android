package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MessageActions;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;

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

        final View collapsedView = view.findViewById(R.id.collappsed);
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
        boolean isHtml = messageData.isHtml();

        // VIEW COLLAPSED
        TextView senderTextView = view.findViewById(R.id.item_message_view_collapsed_sender);
        TextView contentTextView = view.findViewById(R.id.item_message_view_collapsed_content);
        ImageView collapsedReplyMessageImageView = view.findViewById(R.id.item_message_view_holder_reply_image_view);

        senderTextView.setText(senderDisplay.getName());
        contentTextView.setText(Html.fromHtml(message));

        // VIEW EXPANDED
        senderTextView = view.findViewById(R.id.item_message_view_expanded_sender_name);
        TextView receiverTextView = view.findViewById(R.id.item_message_view_expanded_receiver_name);
        TextView dateTextView = view.findViewById(R.id.item_message_view_expanded_date);
        TextView statusTextView = view.findViewById(R.id.item_message_view_expanded_status);
        TextView folderTextView = view.findViewById(R.id.item_message_view_expanded_folder);
        final TextView detailsTextView = view.findViewById(R.id.item_message_view_expanded_details);
        TextView senderEmailTextView = view.findViewById(R.id.item_message_view_from_email);
        TextView receiverEmailTextView = view.findViewById(R.id.item_message_view_to_email);
        View ccLayout = view.findViewById(R.id.item_message_view_CC_layout);
        TextView ccEmailTextView = view.findViewById(R.id.item_message_view_CC_email);
        View bccLayout = view.findViewById(R.id.item_message_view_BCC_layout);
        ImageView replyMessageImageView = view.findViewById(R.id.item_message_view_expanded_reply_image_view);
        TextView bccEmailTextView = view.findViewById(R.id.item_message_view_BCC_email);
        WebView contentWebView = view.findViewById(R.id.item_message_view_expanded_content);
        TextView contentText = view.findViewById(R.id.item_message_text_view_expanded_content);
        RecyclerView attachmentsRecyclerView = view.findViewById(R.id.item_message_view_expanded_attachment);
        final ViewGroup expandedCredentialsLayout = view.findViewById(R.id.item_message_view_expanded_credentials);
        final View credentialsDivider = view.findViewById(R.id.item_message_view_expanded_credentials_divider);

        detailsTextView.setOnClickListener(v -> {
            int credentialsVisibility = expandedCredentialsLayout.getVisibility();
            if (credentialsVisibility == View.VISIBLE) {
                expandedCredentialsLayout.setVisibility(View.GONE);
                credentialsDivider.setVisibility(View.GONE);
                detailsTextView.setText(view.getContext().getResources().getText(R.string.txt_hide_details));
            } else {
                expandedCredentialsLayout.setVisibility(View.VISIBLE);
                credentialsDivider.setVisibility(View.VISIBLE);
                detailsTextView.setText(view.getContext().getResources().getText(R.string.txt_view_details));
            }
        });

        senderTextView.setText(senderDisplay.getName());
        receiverTextView.setText(userDisplayListToNamesString(receiverDisplayList));
        dateTextView.setText(AppUtils.messageViewDate(messageData.getCreatedAt()));

        // check for folder
        if (folderName != null) {
            folderTextView.setText(folderName);
        }

        // check for status (time delete, delayed delivery)
        if (!TextUtils.isEmpty(messageData.getDelayedDelivery())) {
            String leftTime = AppUtils.elapsedTime(messageData.getDelayedDelivery());
            if (leftTime != null) {
                statusTextView.setText(view.getResources().getString(R.string.txt_left_time_delay_delivery, leftTime));
                statusTextView.setBackgroundColor(view.getResources().getColor(R.color.colorDarkGreen));
            } else {
                statusTextView.setVisibility(View.GONE);
            }
        } else if (!TextUtils.isEmpty(messageData.getDestructDate())) {
            String leftTime = AppUtils.elapsedTime(messageData.getDestructDate());
            if (leftTime != null) {
                statusTextView.setText(view.getResources().getString(R.string.txt_left_time_destruct, leftTime));
            } else {
                statusTextView.setVisibility(View.GONE);
            }
        } else if (!TextUtils.isEmpty(messageData.getDeadManDuration())) {
            String leftTime = AppUtils.deadMansTime(Long.valueOf(messageData.getDeadManDuration()));
            if (leftTime != null) {
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

        // check for last action (reply, reply all, forward)
        if (lastAction == null) {
            replyMessageImageView.setVisibility(View.GONE);
            collapsedReplyMessageImageView.setVisibility(View.GONE);
        } else if (lastAction.equals(MessageActions.REPLY_ALL)) {
            replyMessageImageView.setImageResource(R.drawable.ic_reply_all_message);
            collapsedReplyMessageImageView.setImageResource(R.drawable.ic_reply_all_message);
        } else if (lastAction.equals(MessageActions.FORWARD)) {
            replyMessageImageView.setImageResource(R.drawable.ic_forward_message);
            collapsedReplyMessageImageView.setImageResource(R.drawable.ic_forward_message);
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
            contentWebView.getSettings().setLoadWithOverviewMode(true);
            contentWebView.getSettings().setBuiltInZoomControls(true);
            //contentWebView.getSettings().setDomStorageEnabled(true);
            contentWebView.loadData(encodedContent, "text/html", "base64");
        } else {
            contentText.setText(Html.fromHtml(message));
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
                AttachmentProvider attachmentProvider = messageAttachmentAdapter.getAttachment(position);
                String documentLink = attachmentProvider.getDocumentLink();
                if (documentLink == null) {
                    Toast.makeText(activity, activity.getString(R.string.error_attachment_url), Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri documentUri = Uri.parse(documentLink);
                String fileName = AppUtils.getFileNameFromURL(documentLink);
                if (attachmentProvider.isEncrypted()) {
                    fileName += "-encrypted";
                }

                DownloadManager.Request documentRequest = new DownloadManager.Request(documentUri);
                documentRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                documentRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                if (activity != null && PermissionCheck.readAndWriteExternalStorage(activity)) {
                    DownloadManager downloadManager = (DownloadManager) activity.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.enqueue(documentRequest);
                    Toast.makeText(activity, activity.getString(R.string.toast_download_started), Toast.LENGTH_SHORT).show();
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
