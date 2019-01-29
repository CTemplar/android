package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.main.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import timber.log.Timber;

public class ViewMessagesActivity extends BaseActivity {

    public static final String PARENT_ID = "parent_id";
    public static final String FOLDER_NAME = "folder_name";
    private MainActivityViewModel mainModel;
    private MessageProvider parentMessage;
    private MessageProvider lastMessage;
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

    @BindView(R.id.activity_view_messages_actions)
    ConstraintLayout messageActionsLayout;

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

        modelViewMessages.getMessagesResponse().observe(this, new Observer<List<MessageProvider>>() {
            @Override
            public void onChanged(@Nullable List<MessageProvider> messagesList) {
                if (messagesList == null || messagesList.isEmpty()) {
                    Timber.e("Messages doesn't exists");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_messages_doesnt_exist), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    return;
                }

                MessageProvider currentParentMessage = messagesList.get(0);
                parentMessage = currentParentMessage;
                setSubject(currentParentMessage.getSubject());

                lastMessage = messagesList.get(messagesList.size() - 1);
                decryptedLastMessage = lastMessage.getContent();
                starImageView.setSelected(parentMessage.isStarred());

                MessageAttachmentAdapter messageAttachmentAdapter = new MessageAttachmentAdapter();
                ViewMessagesAdapter adapter = new ViewMessagesAdapter(messagesList, messageAttachmentAdapter);
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

                                if (PermissionCheck.readAndWriteExternalStorage(ViewMessagesActivity.this)) {
                                    DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                                    downloadManager.enqueue(documentRequest);
                                    Toast.makeText(ViewMessagesActivity.this, getResources().getString(R.string.toast_download_started), Toast.LENGTH_SHORT).show();
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

                if (!parentMessage.isRead()) {
                    modelViewMessages.markMessageAsRead(parentMessage.getId());
                }

                loadProgress.setVisibility(View.GONE);

                invalidateOptionsMenu();
            }
        });

        modelViewMessages.getChainMessages(parentId);

        modelViewMessages.getStarredResponse().observe(this, new Observer<MessageProvider>() {
            @Override
            public void onChanged(@Nullable MessageProvider messagesResult) {
                if (messagesResult != null && messagesResult.getId() == parentMessage.getId()) {
                    starImageView.setSelected(messagesResult.isStarred());
                    parentMessage = messagesResult;
                }
            }
        });

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctx, Intent intent) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_download_complete), Toast.LENGTH_SHORT).show();
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
        } else {
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
            menu.findItem(R.id.menu_view_spam).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_view_archive:
                showSnackbar("archive", getResources().getString(R.string.action_archived));
                return true;
            case R.id.menu_view_inbox:
                showSnackbar("inbox", getResources().getString(R.string.action_moved_to_inbox));
                return true;
            case R.id.menu_view_spam:
                showSnackbar("spam", getResources().getString(R.string.action_reported_as_spam));
                return true;
            case R.id.menu_view_not_spam:
                showSnackbar("inbox", getResources().getString(R.string.action_reported_as_not_spam));
                return true;
            case R.id.menu_view_trash:
                showSnackbar("trash", getResources().getString(R.string.action_message_removed));
                return true;
            case R.id.menu_view_move:
                MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
                Bundle moveFragmentBundle = new Bundle();
                moveFragmentBundle.putLong(PARENT_ID, parentMessage.getId());
                moveDialogFragment.setArguments(moveFragmentBundle);
                moveDialogFragment.show(getSupportFragmentManager(), "MoveDialogFragment");
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSnackbar(final String folder, String message) {
        Snackbar snackbar = Snackbar.make(messageActionsLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUI();
            }
        });
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    if (!currentFolder.equals("trash")) {
                        mainModel.toFolder(parentMessage.getId(), folder);
                    } else {
                        mainModel.deleteMessage(parentMessage.getId());
                    }
                    onBackPressed();
                }
                unlockUI();
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void blockUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void unlockUI() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        String createdAt = getStringDate(lastMessage.getCreatedAt());
        String sender = lastMessage.getSender();
        return getResources().getString(R.string.txt_user_wrote, createdAt, sender);
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
        String sender = lastMessage.getSender();
        String createdAt = getStringDate(lastMessage.getCreatedAt());
        String subject = lastMessage.getSubject();

        return "\n\n---------- " + getResources().getString(R.string.txt_forwarded_message) + "----------\n" +
            getResources().getString(R.string.txt_from) + " <" + sender + ">\n" +
            getResources().getString(R.string.txt_date) + ": " + createdAt + "\n" +
            getResources().getString(R.string.txt_subject) + ": " + subject + "\n" +
            getResources().getString(R.string.txt_to) + " " + receiversString + "\n\n";
    }

    @OnClick(R.id.activity_view_messages_reply)
    public void onClickReply() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(decryptedLastMessage));
        intent.putExtra(SendMessageActivity.PARENT_ID, parentMessage.getId());
        startActivity(intent);
    }

    @OnClick(R.id.activity_view_messages_reply_all)
    public void onClickReplyAll() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
        intent.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(decryptedLastMessage));
        intent.putExtra(Intent.EXTRA_CC, lastMessage.getCc());
        intent.putExtra(Intent.EXTRA_BCC, lastMessage.getBcc());
        intent.putExtra(SendMessageActivity.PARENT_ID, parentMessage.getId());
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
