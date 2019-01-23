package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import timber.log.Timber;

public class ViewMessagesActivity extends BaseActivity {

    public static final String PARENT_ID = "parent_id";
    public static final String FOLDER_NAME = "folder_name";
    private MainActivityViewModel mainModel;
    private MessagesResult parentMessage;
    private MessagesResult lastMessage;
    private MailboxEntity currentMailbox;
    private String decryptedLastMessage;
    private String currentFolder;

    @BindView(R.id.activity_view_messages_messages)
    ListView messagesListView;

    @BindView(R.id.activity_view_messages_subject_text)
    TextView  subjectTextView;

    @BindView(R.id.activity_view_messages_subject_star_image)
    ImageView starImageView;

    @BindView(R.id.activity_view_messages_progress)
    View loadProgress;

    @BindView(R.id.activity_view_messages_bar)
    Toolbar toolbar;

    ViewMessagesViewModel modelViewMessages;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_messages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadProgress.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mainModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        modelViewMessages = ViewModelProviders.of(this).get(ViewMessagesViewModel.class);
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();

        final String password = CTemplarApp.getInstance()
                .getSharedPreferences("pref_user", Context.MODE_PRIVATE)
                .getString("key_password", null);

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
        currentFolder = intent.getStringExtra(FOLDER_NAME);

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

                        PGPManager pgpManager = new PGPManager();
                        String privateKey = currentMailbox.getPrivateKey();
                        String encryptedMessage = ViewMessagesActivity.this.lastMessage.getContent();
                        decryptedLastMessage = pgpManager.decryptMessage(encryptedMessage, privateKey, password);

                        starImageView.setSelected(parentMessage.isStarred());
                    }

                    MessageAttachmentAdapter messageAttachmentAdapter = new MessageAttachmentAdapter();
                    ViewMessagesAdapter adapter = new ViewMessagesAdapter(messagesArrayList, messageAttachmentAdapter);
                    messageAttachmentAdapter.getOnClickAttachmentLink()
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(String documentLink) {
                                    Uri documentUri = Uri.parse(documentLink);
                                    String fileName = AppUtils.getFileNameFromURL(documentLink);

                                    DownloadManager.Request documentRequest = new DownloadManager.Request(documentUri);
                                    documentRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                                    documentRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                    if (PermissionCheck.readAndWriteExternalStorage(ViewMessagesActivity.this)){
                                        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                                        downloadManager.enqueue(documentRequest);
                                        Toast.makeText(ViewMessagesActivity.this, "Download started", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                    messagesListView.setAdapter(adapter);

                    if (!parentMessage.isRead())  {
                        modelViewMessages.markMessageAsRead(parentMessage.getId());
                    }

                    loadProgress.setVisibility(View.GONE);

                    invalidateOptionsMenu();

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

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctx, Intent intent) {
                Toast.makeText(getApplicationContext(), "Download complete", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_messages_menu, menu);
        if (currentFolder.equals("inbox")) {
            menu.findItem(R.id.menu_view_inbox).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals("sent")) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals("outbox")) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals("spam")) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_archive).setVisible(false);
        } else if (currentFolder.equals("archive")) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_archive).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals("draft")) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals("trash")) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_view_archive:
                mainModel.toFolder(parentMessage.getId(), "archive");
                Toast.makeText(getApplicationContext(), "1 archived", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            case R.id.menu_view_inbox:
                mainModel.toFolder(parentMessage.getId(), "inbox");
                Toast.makeText(getApplicationContext(), "Moved to inbox", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            case R.id.menu_view_spam:
                mainModel.toFolder(parentMessage.getId(), "spam");
                Toast.makeText(getApplicationContext(), "1 reported as spam", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            case R.id.menu_view_not_spam:
                mainModel.toFolder(parentMessage.getId(), "inbox");
                Toast.makeText(getApplicationContext(), "1 reported as not spam", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            case R.id.menu_view_trash:
                if (currentFolder.equals("trash")) {
                    mainModel.deleteMessage(parentMessage.getId());
                    Toast.makeText(getApplicationContext(), "1 message permanently deleted", Toast.LENGTH_SHORT).show();
                } else {
                    mainModel.toFolder(parentMessage.getId(), "trash");
                    Toast.makeText(getApplicationContext(), "1 message removed", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        intent.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(decryptedLastMessage));
        intent.putExtra(SendMessageActivity.PARENT_ID, lastMessage.getId());
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_reply_all)
    public void onClickReplyAll() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(decryptedLastMessage));
        intent.putExtra(Intent.EXTRA_CC, lastMessage.getCC());
        intent.putExtra(Intent.EXTRA_BCC, lastMessage.getBCC());
        intent.putExtra(SendMessageActivity.PARENT_ID, lastMessage.getId());
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_forward)
    public void onClickForward() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, forwardHead() + Html.fromHtml(decryptedLastMessage));
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_subject_star_image_layout)
    public void onClickStarred() {
        if (parentMessage != null) {
            boolean isStarred = !parentMessage.isStarred();
            modelViewMessages.markMessageIsStarred(parentMessage.getId(), isStarred);
        }
    }
}
