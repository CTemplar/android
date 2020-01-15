package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Activity;
import android.app.DownloadManager;
import androidx.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.io.File;
import java.util.Collections;

import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.AttachmentsEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.UserDisplayProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.ATTACHMENT_LIST;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;

public class ViewMessagesFragment extends Fragment implements View.OnClickListener, ActivityInterface {

    public static final String FOLDER_NAME = "folder_name";
    private MainActivityViewModel mainModel;
    private ViewMessagesViewModel viewModel;
    private MessageProvider parentMessage;
    private MessageProvider lastMessage;
    private String decryptedLastMessage;
    private String currentFolder;
    private DownloadCompleteReceiver downloadReceiver = new DownloadCompleteReceiver();

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
    private ImageView encryptedImageView;
    private ImageView starImageView;
    private View loadProgress;
    private Toolbar toolbar;
    private ConstraintLayout messageActionsLayout;
    private AttachmentProvider attachmentProvider;

    private OnAttachmentDownloading onAttachmentDownloading = new OnAttachmentDownloading() {
        @Override
        public void onStart(AttachmentProvider attachment) {
            attachmentProvider = attachment;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View root = inflater.inflate(R.layout.fragment_view_messages, container, false);

        messagesListView = root.findViewById(R.id.activity_view_messages_messages);
        subjectTextView = root.findViewById(R.id.activity_view_messages_subject_text);
        encryptedImageView = root.findViewById(R.id.activity_view_messages_subject_encrypted_image);
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
        viewModel = ViewModelProviders.of(this).get(ViewMessagesViewModel.class);

        Bundle args = getArguments();
        long parentId = -1;
        if (args != null) {
            parentId = args.getLong(PARENT_ID, -1);
            currentFolder = args.getString(FOLDER_NAME);
        }
        if (parentId < 0) {
            activity.onBackPressed();
        }

        attachmentProvider = new AttachmentProvider();
        viewModel.getChainMessages(parentId);
        viewModel.getMessagesResponse().observe(this, messagesList -> {
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
            encryptedImageView.setSelected(parentMessage.isProtected());
            starImageView.setSelected(parentMessage.isStarred());

            ViewMessagesAdapter adapter = new ViewMessagesAdapter(
                    messagesList, onAttachmentDownloading, getActivity()
            );
            messagesListView.setAdapter(adapter);

            if (!parentMessage.isRead()) {
                long parentMessageId = parentMessage.getId();
                viewModel.markMessageAsRead(parentMessageId, true);
            }

            loadProgress.setVisibility(View.GONE);
            activity.invalidateOptionsMenu();
        });

        viewModel.getStarredResponse().observe(this, isStarred -> {
            boolean starred = isStarred == null ? false : isStarred;
            starImageView.setSelected(starred);
            parentMessage.setStarred(starred);
        });
        viewModel.getAddWhitelistStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(getActivity(), getString(R.string.added_to_whitelist), Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getActivity(), getString(R.string.adding_whitelist_contact_error), Toast.LENGTH_SHORT).show();
            }
        });
        mainModel.getToFolderStatus().observe(this, responseStatus -> {
            Activity activity1 = getActivity();
            if (activity1 != null) {
                activity1.onBackPressed();
            }
        });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.unregisterReceiver(downloadReceiver);
        }
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

        switch (currentFolder) {
            case INBOX:
                menu.findItem(R.id.menu_view_inbox).setVisible(false);
                menu.findItem(R.id.menu_view_not_spam).setVisible(false);
                break;
            case SENT:
            case OUTBOX:
                menu.findItem(R.id.menu_view_inbox).setVisible(false);
                menu.findItem(R.id.menu_view_unread).setVisible(false);
                menu.findItem(R.id.menu_view_spam).setVisible(false);
                menu.findItem(R.id.menu_view_not_spam).setVisible(false);
                break;
            case ARCHIVE:
                menu.findItem(R.id.menu_view_spam).setVisible(false);
                menu.findItem(R.id.menu_view_archive).setVisible(false);
                menu.findItem(R.id.menu_view_not_spam).setVisible(false);
                break;
            case SPAM:
                menu.findItem(R.id.menu_view_spam).setVisible(false);
                menu.findItem(R.id.menu_view_archive).setVisible(false);
                break;
            case TRASH:
                menu.findItem(R.id.menu_view_spam).setVisible(false);
                menu.findItem(R.id.menu_view_not_spam).setVisible(false);
                menu.findItem(R.id.menu_view_unread).setVisible(false);
                break;
            default:
                menu.findItem(R.id.menu_view_not_spam).setVisible(false);
                menu.findItem(R.id.menu_view_spam).setVisible(false);
                break;
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
                markNotSpam();
                return true;
            case R.id.menu_view_trash:
                snackbarDelete(TRASH, getResources().getString(R.string.action_message_removed));
                return true;
            case R.id.menu_view_unread:
                viewModel.markMessageAsRead(parentMessage.getId(), false);
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_message_marked_unread), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_view_move:
                showMoveDialog();
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
        snackbar.setAction(getString(R.string.action_undo), view -> blockUI());
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
        snackbar.setAction(getString(R.string.action_undo), view -> blockUI());
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    mainModel.toFolder(parentMessage.getId(), folder);
                }
                unlockUI();
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void markNotSpam() {
        UserDisplayProvider senderDisplay = parentMessage.getSenderDisplay();
        String senderName = senderDisplay.getName();
        String senderEmail = senderDisplay.getEmail();
        viewModel.addWhitelistContact(senderName, senderEmail);
    }

    private void showMoveDialog() {
        MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
        Bundle moveFragmentBundle = new Bundle();
        moveFragmentBundle.putLong(PARENT_ID, parentMessage.getId());
        moveDialogFragment.setArguments(moveFragmentBundle);
        moveDialogFragment.show(getActivity().getSupportFragmentManager(), "MoveDialogFragment");
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

    private String replyHead() {
        String createdAt = AppUtils.getStringDate(lastMessage.getCreatedAt());
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
        String createdAt = AppUtils.getStringDate(lastMessage.getCreatedAt());
        String subject = lastMessage.getSubject();

        return "\n\n---------- " + getResources().getString(R.string.txt_forwarded_message) + "----------\n" +
                getResources().getString(R.string.txt_from) + " <" + sender + ">\n" +
                getResources().getString(R.string.txt_date) + ": " + createdAt + "\n" +
                getResources().getString(R.string.txt_subject) + ": " + subject + "\n" +
                getResources().getString(R.string.txt_to) + " " + receiversString + "\n\n";
    }

    private void forwardMessage(boolean withAttachments) {
        Intent intentForward = new Intent(getActivity(), SendMessageActivity.class);
        intentForward.putExtra(Intent.EXTRA_SUBJECT, lastMessage.getSubject());
        intentForward.putExtra(Intent.EXTRA_TEXT, forwardHead() + Html.fromHtml(decryptedLastMessage));

        Bundle extras = new Bundle();
        AttachmentsEntity attachmentsEntity = withAttachments
                ? new AttachmentsEntity(lastMessage.getAttachments())
                : new AttachmentsEntity(Collections.emptyList());
        extras.putSerializable(ATTACHMENT_LIST, attachmentsEntity);
        intentForward.putExtras(extras);

        Fragment fragmentForward = SendMessageFragment.newInstance(
                lastMessage.getSubject(),
                forwardHead() + Html.fromHtml(decryptedLastMessage),
                new String[] {},
                new String[] {},
                new String[] {},
                attachmentsEntity,
                null
        );

        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).showActivityOrFragment(intentForward, fragmentForward);
        } else {
            startActivity(intentForward);
        }
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
                        new AttachmentsEntity(),
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
                        new AttachmentsEntity(),
                        parentMessage.getId()
                );

                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).showActivityOrFragment(intentReplyAll, fragmentReplyAll);
                } else {
                    startActivity(intentReplyAll);
                }
                break;
            case R.id.activity_view_messages_forward:
                if (lastMessage.getAttachments().isEmpty()) {
                    forwardMessage(false);
                    return;
                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.forward_attachments));
                alertDialog.setMessage(getString(R.string.forward_attachments_description));
                alertDialog.setPositiveButton(getString(R.string.yes), (dialog, which) -> forwardMessage(true)
                );
                alertDialog.setNegativeButton(getString(R.string.no), (dialog, which) -> forwardMessage(false)
                );
                alertDialog.show();
                break;
            case R.id.activity_view_messages_subject_star_image_layout:
                if (parentMessage != null) {
                    boolean isStarred = !parentMessage.isStarred();
                    viewModel.markMessageIsStarred(parentMessage.getId(), isStarred);
                }
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }


    private class DownloadCompleteReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent intent) {
            Toast.makeText(ctx, ctx.getString(R.string.toast_download_complete), Toast.LENGTH_SHORT).show();
            try {
                String documentLink = attachmentProvider.getDocumentLink();
                String fileName = AppUtils.getFileNameFromURL(documentLink);
                boolean isEncrypted = attachmentProvider.isEncrypted();

                File externalStorageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (isEncrypted) {
                    File downloadedFile = new File(externalStorageFile, fileName + "-encrypted");
                    File decryptedFile = new File(externalStorageFile, fileName);

                    long mailboxId = parentMessage.getMailboxId();
                    MailboxEntity mailboxEntity = viewModel.getMailboxById(mailboxId);
                    String password = viewModel.getUserPassword();
                    String privateKey = mailboxEntity.getPrivateKey();
                    boolean attachmentDecrypted = EncryptUtils.decryptAttachment(
                            downloadedFile, decryptedFile, password, privateKey
                    );
                    downloadedFile.delete();

                    Uri decryptedUri = FileProvider.getUriForFile(
                            ctx, BuildConfig.APPLICATION_ID + ".fileprovider", decryptedFile
                    );
                    if (attachmentDecrypted) {
                        openFile(decryptedUri);
                    }
                } else {
                    File downloadedFile = new File(externalStorageFile, fileName);
                    Uri fileUri = FileProvider.getUriForFile(
                            ctx, BuildConfig.APPLICATION_ID + ".fileprovider", downloadedFile
                    );
                    openFile(fileUri);
                }
            } catch (Exception e) {
                Timber.i(e);
            }
        }
    }

    private void openFile(Uri fileUri) {
        try {
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            String fileType = getActivity().getContentResolver().getType(fileUri);
            openIntent.setDataAndType(fileUri, fileType);
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Timber.i(e);
        }
    }
}
