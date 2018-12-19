package mobileapp.ctemplar.com.ctemplarapp.message;

import android.content.Context;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.kibotu.pgp.Pgp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import timber.log.Timber;

public class ViewMessagesAdapter extends BaseAdapter {

    private MailboxEntity currentMailbox;
    private List<MessagesResult> data;

    ViewMessagesAdapter(List<MessagesResult> data) {
        this.data = data;
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
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

    private View getViewByFlag(LayoutInflater inflater, ViewGroup parent, MessagesResult messageData, boolean isLast) {
        View view = inflater.inflate(R.layout.item_message_view_selector, parent, false);

        final View collapsedView = view.findViewById(R.id.collappsed);
        final View expandedView = view.findViewById(R.id.expanded);

        String password =
                CTemplarApp.getInstance().getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);

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

        String encodedMessage = decodeContent(messageData.getContent(), password);
        String style = "<style type=\"text/css\">*{width:auto;}</style>";
        String messageWithStyle = style + encodedMessage;
        String encodedContent = Base64.encodeToString(messageWithStyle.getBytes(), Base64.NO_PADDING);

        // collapsed
        TextView senderTextView = view.findViewById(R.id.item_message_view_collapsed_sender);
        TextView contentTextView = view.findViewById(R.id.item_message_view_collapsed_content);

        senderTextView.setText(messageData.getSender());
        contentTextView.setText(Html.fromHtml(encodedMessage));


        //expanded

        senderTextView = view.findViewById(R.id.item_message_view_expanded_sender_name);
        TextView receiverTextView = view.findViewById(R.id.item_message_view_expanded_receiver_name);
        TextView dateTextView = view.findViewById(R.id.item_message_view_expanded_date);
        final TextView detailsTextView = view.findViewById(R.id.item_message_view_expanded_details);
        TextView senderNameTextView = view.findViewById(R.id.item_message_view_from_name);
        TextView senderEmailTextView = view.findViewById(R.id.item_message_view_from_email);
        TextView receiverNameTextView = view.findViewById(R.id.item_message_view_to_name);
        TextView receiverEmailTextView = view.findViewById(R.id.item_message_view_to_email);
        View ccLayout = view.findViewById(R.id.item_message_view_CC_layout);
        TextView ccNameTextView = view.findViewById(R.id.item_message_view_CC_name);
        TextView ccEmailTextView = view.findViewById(R.id.item_message_view_CC_email);
        WebView contentWebView = view.findViewById(R.id.item_message_view_expanded_content);
        final ViewGroup expandedCredentialsLayout = view.findViewById(R.id.item_message_view_expanded_credentials);
        final View credentialsDivider = view.findViewById(R.id.item_message_view_expanded_credentials_divider);

        detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int credentialsVisibility = expandedCredentialsLayout.getVisibility();
                if (credentialsVisibility == View.VISIBLE) {
                    expandedCredentialsLayout.setVisibility(View.GONE);
                    credentialsDivider.setVisibility(View.GONE);
                    detailsTextView.setText("View details");
                } else {
                    expandedCredentialsLayout.setVisibility(View.VISIBLE);
                    credentialsDivider.setVisibility(View.VISIBLE);
                    detailsTextView.setText("Hide details");
                }
            }
        });

        senderTextView.setText(messageData.getSender());
        receiverTextView.setText(TextUtils.join(", ", messageData.getReceivers()));
        dateTextView.setText(getStringDate(messageData.getCreatedAt()));

        String[] sender = new String[] { messageData.getSender() };
        senderEmailTextView.setText(addQuotesToNames(sender));
        receiverEmailTextView.setText(addQuotesToNames(messageData.getReceivers()));

        String[] cc = messageData.getCC();
        if (cc != null) {
            ccEmailTextView.setText(addQuotesToNames(messageData.getCC()));
        } else {
            ccLayout.setVisibility(View.GONE);
        }

        contentWebView.getSettings().setLoadWithOverviewMode(true);
        contentWebView.loadData(encodedContent, "text/html", "base64");


        return view;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MessagesResult messageData = data.get(position);
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

    private String decodeContent(String encodedString, String password) {
        Pgp.setPrivateKey(currentMailbox.getPrivateKey());
        Pgp.setPublicKey(currentMailbox.getPublicKey());
        String result = "";

        try {
            result = Pgp.decrypt(encodedString, password);
        } catch (Exception e) {
            Timber.e("Pgp decrypt error: %s", e.getMessage());
        }

        return result;
    }
}
