package mobileapp.ctemplar.com.ctemplarapp.message;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.providers.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.providers.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.providers.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import timber.log.Timber;

public class ViewMessagesAdapter extends BaseAdapter {

    private List<MessageProvider> data;
    private MessageAttachmentAdapter messageAttachmentAdapter;

    ViewMessagesAdapter(List<MessageProvider> data, MessageAttachmentAdapter messageAttachmentAdapter) {
        this.data = data;
        this.messageAttachmentAdapter = messageAttachmentAdapter;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    private View getViewByFlag(LayoutInflater inflater, ViewGroup parent, MessageProvider messageData, boolean isLast) {
        final View view = inflater.inflate(R.layout.item_message_view_selector, parent, false);

        final View collapsedView = view.findViewById(R.id.collappsed);
        final View expandedView = view.findViewById(R.id.expanded);

        collapsedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
            }
        });

        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        if (isLast) {
            collapsedView.setVisibility(View.GONE);
            expandedView.setVisibility(View.VISIBLE);
        }

        String encodedMessage = messageData.getContent();

        String style = "<style type=\"text/css\">*{width:auto;}</style>";
        String messageWithStyle = style + messageData.getContent();
        String encodedContent = Base64.encodeToString(messageWithStyle.getBytes(), Base64.NO_PADDING);

        // VIEW COLLAPSED
        TextView senderTextView = view.findViewById(R.id.item_message_view_collapsed_sender);
        TextView contentTextView = view.findViewById(R.id.item_message_view_collapsed_content);

        UserDisplayProvider senderDisplay = messageData.getSenderDisplay();
        senderTextView.setText(senderDisplay.getName());
        contentTextView.setText(Html.fromHtml(encodedMessage));

        // VIEW EXPANDED
        senderTextView = view.findViewById(R.id.item_message_view_expanded_sender_name);
        TextView receiverTextView = view.findViewById(R.id.item_message_view_expanded_receiver_name);
        TextView dateTextView = view.findViewById(R.id.item_message_view_expanded_date);
        TextView statusTextView = view.findViewById(R.id.item_message_view_expanded_status);
        final TextView detailsTextView = view.findViewById(R.id.item_message_view_expanded_details);
        TextView senderNameTextView = view.findViewById(R.id.item_message_view_from_name);
        TextView senderEmailTextView = view.findViewById(R.id.item_message_view_from_email);
        TextView receiverNameTextView = view.findViewById(R.id.item_message_view_to_name);
        TextView receiverEmailTextView = view.findViewById(R.id.item_message_view_to_email);
        View ccLayout = view.findViewById(R.id.item_message_view_CC_layout);
        TextView ccNameTextView = view.findViewById(R.id.item_message_view_CC_name);
        TextView ccEmailTextView = view.findViewById(R.id.item_message_view_CC_email);
        WebView contentWebView = view.findViewById(R.id.item_message_view_expanded_content);
        RecyclerView attachmentsRecyclerView = view.findViewById(R.id.item_message_view_expanded_attachment);
        final ViewGroup expandedCredentialsLayout = view.findViewById(R.id.item_message_view_expanded_credentials);
        final View credentialsDivider = view.findViewById(R.id.item_message_view_expanded_credentials_divider);

        detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        senderTextView.setText(senderDisplay.getName());
        List<UserDisplayProvider> receiverDisplayList = messageData.getReceiverDisplayList();
        receiverTextView.setText(userDisplayListToNamesString(receiverDisplayList));

        dateTextView.setText(getStringDate(messageData.getCreatedAt()));

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

        String[] cc = messageData.getCc();
        if (cc != null && cc.length > 0) {
            ccEmailTextView.setText(namesToString(messageData.getCc()));
        } else {
            ccLayout.setVisibility(View.GONE);
        }

        senderNameTextView.setText("");
        receiverNameTextView.setText("");
        ccNameTextView.setText("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            contentWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            contentWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        contentWebView.getSettings().setLoadWithOverviewMode(true);
        contentWebView.loadData(encodedContent, "text/html", "base64");

        List<AttachmentProvider> attachmentsList = messageData.getAttachments();

        RecyclerView.LayoutManager mLayoutManager
                = new LinearLayoutManager(attachmentsRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        attachmentsRecyclerView.setLayoutManager(mLayoutManager);

        messageAttachmentAdapter.setAttachmentsList(attachmentsList);
        attachmentsRecyclerView.setAdapter(messageAttachmentAdapter);

        return view;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MessageProvider messageData = data.get(position);
        return getViewByFlag(inflater, parent, messageData, position + 1 == getCount());
    }

    private String namesToString(String[] names) {
        return TextUtils.join(", ", names);
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

    private String getStringDate(String stringDate) {
        if (stringDate == null) {
            return "";
        }
        DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        DateFormat viewFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        try {
            Date date = parseFormat.parse(stringDate);
            stringDate = viewFormat.format(date);

        } catch (ParseException e) {
            Timber.e("DateParse error: %s", e.getMessage());
        }
        return stringDate;
    }

}
