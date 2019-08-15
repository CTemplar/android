package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Activity;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;

public class ViewMessagesFragment extends Fragment implements View.OnClickListener, ActivityInterface {

    public static final String FOLDER_NAME = "folder_name";
    private MainActivityViewModel mainModel;
    private ViewMessagesViewModel modelViewMessages;
    private MessageProvider parentMessage;
    private MessageProvider lastMessage;
    private String decryptedLastMessage;
    private String currentFolder;

    public static ViewMessagesFragment newInstance(
            @Nullable Long parentId
    ) {
        Bundle args = new Bundle();
        if (parentId != null) {
            args.putLong(PARENT_ID, parentId);
        }

        ViewMessagesFragment fragment = new ViewMessagesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static ViewMessagesFragment newInstance() {
        return new ViewMessagesFragment();
    }

    public static ViewMessagesFragment newInstance(Bundle args) {
        ViewMessagesFragment fragment = newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    private ListView messagesListView;
    private TextView subjectTextView;
    private ImageView starImageView;
    private View loadProgress;
    private Toolbar toolbar;
    private ConstraintLayout messageActionsLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View root = inflater.inflate(R.layout.fragment_view_messages, container, false);

        messagesListView = root.findViewById(R.id.activity_view_messages_messages);
        subjectTextView = root.findViewById(R.id.activity_view_messages_subject_text);
        starImageView = root.findViewById(R.id.activity_view_messages_subject_star_image);
        loadProgress = root.findViewById(R.id.activity_view_messages_progress);
        toolbar = root.findViewById(R.id.activity_view_messages_bar);
        messageActionsLayout = root.findViewById(R.id.activity_view_messages_actions);
        loadProgress.setVisibility(View.VISIBLE);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            setHasOptionsMenu(true);
        }

        // OnClicks
        root.findViewById(R.id.activity_view_messages_reply).setOnClickListener(this);
        root.findViewById(R.id.activity_view_messages_reply_all).setOnClickListener(this);
        root.findViewById(R.id.activity_view_messages_forward).setOnClickListener(this);
        root.findViewById(R.id.activity_view_messages_subject_star_image_layout).setOnClickListener(this);

        mainModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        modelViewMessages = ViewModelProviders.of(this).get(ViewMessagesViewModel.class);

        Bundle args = getArguments();
        long parentId = -1;
        if (args != null) {
            parentId = args.getLong(PARENT_ID, -1);
            currentFolder = args.getString(FOLDER_NAME);
        }
        if (parentId < 0) {
            activity.onBackPressed();
        }

        modelViewMessages.getChainMessages(parentId);
        modelViewMessages.getMessagesResponse().observe(this, new Observer<List<MessageProvider>>() {
            @Override
            public void onChanged(@Nullable List<MessageProvider> messagesList) {
                if (messagesList == null || messagesList.isEmpty()) {
                    Timber.e("Messages doesn't exists");
                    Toast.makeText(activity.getApplicationContext(), getResources().getString(R.string.toast_messages_doesnt_exist), Toast.LENGTH_SHORT).show();
                    activity.onBackPressed();
                    return;
                }

                MessageProvider currentParentMessage = messagesList.get(0);
                String subjectText = currentParentMessage.getSubject();
                subjectTextView.setText(subjectText);
                parentMessage = currentParentMessage;

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

                                if (getActivity() != null && PermissionCheck.readAndWriteExternalStorage(getActivity())) {
                                    DownloadManager downloadManager = (DownloadManager) getActivity().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                                    downloadManager.enqueue(documentRequest);
                                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_download_started), Toast.LENGTH_SHORT).show();
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
                messagesListView.setAdapter(adapter);

                if (!parentMessage.isRead()) {
                    long parentMessageId = parentMessage.getId();
                    modelViewMessages.markMessageAsRead(parentMessageId, true);
                }

                loadProgress.setVisibility(View.GONE);
                activity.invalidateOptionsMenu();
            }
        });

        modelViewMessages.getStarredResponse().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isStarred) {
                boolean starred = isStarred == null ? false : isStarred;
                starImageView.setSelected(starred);
                parentMessage.setStarred(starred);
            }
        });

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctx, Intent intent) {
                Toast.makeText(ctx, ctx.getResources().getString(R.string.toast_download_complete), Toast.LENGTH_SHORT).show();
            }
        };
        activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.view_messages_menu, menu);
        if (currentFolder == null) {
            currentFolder = mainModel.getCurrentFolder().getValue();
            if (currentFolder == null) {
                Timber.tag("ViewMessageFragment").wtf("Current folder is null");
                return;
            }
        }

        if (currentFolder.equals(INBOX)) {
            menu.findItem(R.id.menu_view_inbox).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals(SENT)) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals(OUTBOX)) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals(SPAM)) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_archive).setVisible(false);
        } else if (currentFolder.equals(ARCHIVE)) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_archive).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals(DRAFT)) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else if (currentFolder.equals(TRASH)) {
            menu.findItem(R.id.menu_view_spam).setVisible(false);
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
        } else {
            menu.findItem(R.id.menu_view_not_spam).setVisible(false);
            menu.findItem(R.id.menu_view_spam).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_view_archive:
                snackbarMove(ARCHIVE, getResources().getString(R.string.action_archived));
                return true;
            case R.id.menu_view_inbox:
                snackbarMove(INBOX, getResources().getString(R.string.action_moved_to_inbox));
                return true;
            case R.id.menu_view_spam:
                snackbarMove(SPAM, getResources().getString(R.string.action_reported_as_spam));
                return true;
            case R.id.menu_view_not_spam:
                snackbarMove(INBOX, getResources().getString(R.string.action_reported_as_not_spam));
                return true;
            case R.id.menu_view_trash:
                snackbarDelete(TRASH, getResources().getString(R.string.action_message_removed));
                return true;
            case R.id.menu_view_unread:
                modelViewMessages.markMessageAsRead(parentMessage.getId(), false);
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_message_marked_unread), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_view_move:
                MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
                Bundle moveFragmentBundle = new Bundle();
                moveFragmentBundle.putLong(PARENT_ID, parentMessage.getId());
                moveDialogFragment.setArguments(moveFragmentBundle);
                moveDialogFragment.show(getActivity().getSupportFragmentManager(), "MoveDialogFragment");
                return true;
            case android.R.id.home:
                Activity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void snackbarDelete(final String folder, String message) {
        Snackbar snackbar = Snackbar.make(messageActionsLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(getString(R.string.action_undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUI();
            }
        });
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    if (currentFolder.equals(TRASH)) {
                        mainModel.deleteMessage(parentMessage.getId());
                    } else {
                        mainModel.toFolder(parentMessage.getId(), folder);
                    }
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.onBackPressed();
                    }
                }
                unlockUI();
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void snackbarMove(final String folder, String message) {
        Snackbar snackbar = Snackbar.make(messageActionsLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(getString(R.string.action_undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUI();
            }
        });
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    mainModel.toFolder(parentMessage.getId(), folder);

                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.onBackPressed();
                    }
                }
                unlockUI();
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void blockUI() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void unlockUI() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private String getStringDate(String stringDate) {
        if (stringDate == null) {
            return "";
        }
        DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        DateFormat viewFormat = new SimpleDateFormat("EEE',' MMMM d, yyyy 'at' h:mm a", Locale.getDefault());
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
        String sender = "<" + lastMessage.getSender() + ">";
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        FragmentActivity activity = getActivity();

        switch (id) {
            case R.id.activity_view_messages_reply:
                Intent intentReply = new Intent(getActivity(), SendMessageActivity.class);
                intentReply.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
                intentReply.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
                intentReply.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(decryptedLastMessage));
                intentReply.putExtra(SendMessageActivity.PARENT_ID, parentMessage.getId());

                Fragment fragmentReply = SendMessageFragment.newInstance(
                        lastMessage.getSubject(),
                        replyHead() + Html.fromHtml(decryptedLastMessage),
                        new String[] { lastMessage.getSender() },
                        new String[] {},
                        new String[] {},
                        parentMessage.getId()
                );

                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).showActivityOrFragment(intentReply, fragmentReply);
                } else {
                    startActivity(intentReply);
                }
                break;
            case R.id.activity_view_messages_reply_all:
                Intent intentReplyAll = new Intent(getActivity(), SendMessageActivity.class);
                intentReplyAll.putExtra(Intent.EXTRA_EMAIL, new String[] { lastMessage.getSender() });
                intentReplyAll.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
                intentReplyAll.putExtra(Intent.EXTRA_TEXT, replyHead() + Html.fromHtml(decryptedLastMessage));
                intentReplyAll.putExtra(Intent.EXTRA_CC, lastMessage.getCc());
                intentReplyAll.putExtra(Intent.EXTRA_BCC, lastMessage.getBcc());
                intentReplyAll.putExtra(SendMessageActivity.PARENT_ID, parentMessage.getId());

                Fragment fragmentReplyAll = SendMessageFragment.newInstance(
                        lastMessage.getSubject(),
                        replyHead() + Html.fromHtml(decryptedLastMessage),
                        new String[] { lastMessage.getSender() },
                        lastMessage.getCc(),
                        lastMessage.getBcc(),
                        parentMessage.getId()
                );

                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).showActivityOrFragment(intentReplyAll, fragmentReplyAll);
                } else {
                    startActivity(intentReplyAll);
                }
                break;
            case R.id.activity_view_messages_forward:
                Intent intentForward = new Intent(getActivity(), SendMessageActivity.class);
                intentForward.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
                intentForward.putExtra(Intent.EXTRA_TEXT, forwardHead() + Html.fromHtml(decryptedLastMessage));

                Fragment fragmentForward = SendMessageFragment.newInstance(
                        lastMessage.getSubject(),
                        replyHead() + Html.fromHtml(decryptedLastMessage),
                        new String[] {},
                        new String[] {},
                        new String[] {},
                        null
                );

                activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).showActivityOrFragment(intentForward, fragmentForward);
                } else {
                    startActivity(intentForward);
                }
                break;
            case R.id.activity_view_messages_subject_star_image_layout:
                if (parentMessage != null) {
                    boolean isStarred = !parentMessage.isStarred();
                    modelViewMessages.markMessageIsStarred(parentMessage.getId(), isStarred);
                }
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
