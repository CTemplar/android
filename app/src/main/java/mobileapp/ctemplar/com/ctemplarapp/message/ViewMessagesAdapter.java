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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.main.MessageProvider;
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

        senderTextView.setText(messageData.getSender());
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

        if (messageData.getSender() != null) {
            senderTextView.setText(messageData.getSender());
        }
        if (messageData.getReceivers() != null) {
            receiverTextView.setText(TextUtils.join(", ", messageData.getReceivers()));
        }
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

        String[] sender = new String[] { messageData.getSender() };
        senderEmailTextView.setText(addQuotesToNames(sender));

        if (messageData.getReceivers() != null) {
            receiverEmailTextView.setText(addQuotesToNames(messageData.getReceivers()));
        }

        String[] cc = messageData.getCc();
        if (cc != null && cc.length > 0) {
            ccEmailTextView.setText(addQuotesToNames(messageData.getCc()));
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

    private String addQuotesToNames(String[] names) {
        String[] nameList = new String[names.length];
        for (int i = 0; i < nameList.length; i++) {
            nameList[i] = "<" + names[i] + ">";
        }

        return TextUtils.join(", ", nameList);
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
