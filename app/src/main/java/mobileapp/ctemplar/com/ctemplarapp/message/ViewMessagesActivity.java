package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.kibotu.pgp.Pgp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import timber.log.Timber;

public class ViewMessagesActivity extends BaseActivity {
    public static final String PARENT_ID = "parent_id";
    private MessagesResult parentMessage;
    private MessagesResult lastMessage;
    private MailboxEntity currentMailbox;
    private String encodedLastMessage;

    @BindView(R.id.activity_view_messages_messages)
    ListView messagesListView;

    @BindView(R.id.activity_view_messages_subject_text)
    TextView  subjectTextView;

    @BindView(R.id.activity_view_messages_subject_star_image)
    ImageView starImageView;

    @BindView(R.id.activity_view_messages_progress)
    View loadProgress;

    ViewMessagesViewModel modelViewMessages;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_messages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadProgress.setVisibility(View.VISIBLE);

        modelViewMessages = ViewModelProviders.of(this).get(ViewMessagesViewModel.class);
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();

        final String password =
                CTemplarApp.getInstance().getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);

        Intent intent = getIntent();
        if (intent == null) {
            onBackPressed();
            return;
        }

        long parentId = intent.getLongExtra(PARENT_ID, -1);
        if (parentId == -1) {
            onBackPressed();
            return;
        }

        modelViewMessages.getMessagesResponse().observe(this, new Observer<MessagesResponse>() {
            @Override
            public void onChanged(@Nullable MessagesResponse messagesResponse) {
                if (messagesResponse != null) {
                    List<MessagesResult> messagesList = messagesResponse.getMessagesList();
                    if (messagesList == null) {
                        messagesList = new ArrayList<>();
                    }

                    List <MessagesResult> messagesArrayList = new ArrayList<>();
                    if (messagesList.size() > 0) {
                        MessagesResult currentParentMessage = messagesList.get(0);
                        messagesArrayList.add(currentParentMessage);
                        parentMessage = currentParentMessage;
                        setSubject(currentParentMessage.getSubject());

                        MessagesResult[] children = currentParentMessage.getChildren();
                        if (children != null && children.length != 0) {
                            messagesArrayList.addAll(Arrays.asList(children));
                        }

                        lastMessage = messagesArrayList.get(messagesArrayList.size() - 1);
                        encodedLastMessage = decodeContent(ViewMessagesActivity.this.lastMessage.getContent(), password);
                    }

                    ViewMessagesAdapter adapter = new ViewMessagesAdapter(messagesArrayList);
                    messagesListView.setAdapter(adapter);

                    if (!parentMessage.isRead())  {
                        modelViewMessages.markMessageAsRead(parentMessage.getId());
                    }

                    loadProgress.setVisibility(View.GONE);

                } else {
                    Timber.e("Messages doesn't exists");
                    Toast.makeText(getApplicationContext(), "Messages doesn't exists", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

        modelViewMessages.getChainMessages(parentId);

        modelViewMessages.getStarredResponse().observe(this, new Observer<MessagesResult>() {
            @Override
            public void onChanged(@Nullable MessagesResult messagesResult) {
                if (messagesResult != null && messagesResult.getId() == parentMessage.getId()) {
                    starImageView.setSelected(messagesResult.isStarred());
                    parentMessage = messagesResult;
                }
            }
        });
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

    private void setSubject(String subject) {
        subjectTextView.setText(subject);
    }

    private String getStringDate(String stringDate) {
        if (stringDate == null) {
            return "";
        }
        DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        DateFormat viewFormat = new SimpleDateFormat("h:mm a',' MMMM d yyyy", Locale.getDefault());
        try {
            Date date = parseFormat.parse(stringDate);
            stringDate = viewFormat.format(date);

        } catch (ParseException e) {
            Timber.e("DateParse error: %s", e.getMessage());
        }
        return stringDate;
    }

    private String replyHead() {
        return "\n\nOn " +
                getStringDate(lastMessage.getCreatedAt()) + " " +
                lastMessage.getSender() +  " wrote:\n\n";
    }

    private String addQuotesToNames(String[] names) {
        String[] nameList = new String[names.length];
        for (int i = 0; i < nameList.length; i++) {
            nameList[i] = "<" + names[i] + ">";
        }

        return TextUtils.join(", ", nameList);
    }

    private String forwardHead() {
        String receiversString = addQuotesToNames(lastMessage.getReceivers());

        return "\n\n---------- Forwarded message ----------\n" +
                "From: <" + lastMessage.getSender() + ">\n" +
                "Date: " + getStringDate(lastMessage.getCreatedAt()) + "\n" +
                "Subject: " + lastMessage.getSubject() + "\n" +
                "To: " + receiversString + "\n\n";
    }

    @OnClick(R.id.activity_view_messages_reply)
    public void onClickReply() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(encodedLastMessage));
        intent.putExtra(SendMessageFragment.PARENT_ID, lastMessage.getId());
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_reply_all)
    public void onClickReplyAll() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(encodedLastMessage));
        intent.putExtra(Intent.EXTRA_CC, lastMessage.getCC());
        intent.putExtra(Intent.EXTRA_BCC, lastMessage.getBCC());
        intent.putExtra(SendMessageFragment.PARENT_ID, lastMessage.getId());
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_forward)
    public void onClickForward() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, forwardHead() + Html.fromHtml(encodedLastMessage));
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_subject_star_image_layout)
    public void onClickStarred() {
        if (parentMessage != null) {
            boolean isStarred = !parentMessage.isStarred();
            modelViewMessages.markMessageIsStarred(parentMessage.getId(), isStarred);
        }
    }

    @OnClick(R.id.activity_view_messages_back)
    public void onClickBack() {
        onBackPressed();
    }
}
