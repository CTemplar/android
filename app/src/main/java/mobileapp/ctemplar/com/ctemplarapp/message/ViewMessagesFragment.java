package mobileapp.ctemplar.com.ctemplarapp.message;

import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.ATTACHMENT_LIST;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.dialog.MoveDialogFragment.MESSAGE_IDS;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.FOLDER_NAME;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.message.dialog.MoveDialogFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.dialog.PasswordEncryptedMessageDialogFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.AttachmentsEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MessageActions;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.services.download.DownloadAttachmentInfo;
import mobileapp.ctemplar.com.ctemplarapp.services.download.DownloadAttachmentService;
import mobileapp.ctemplar.com.ctemplarapp.services.download.DownloadAttachmentTask;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.HtmlUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import timber.log.Timber;

public class ViewMessagesFragment extends Fragment implements View.OnClickListener, ActivityInterface {
    private MainActivityViewModel mainModel;
    private ViewMessagesViewModel viewModel;
    private MessageProvider parentMessage;
    private MessageProvider lastMessage;
    private String currentFolder;
    private final MessagesRecyclerViewAdapter messagesRecyclerViewAdapter
            = new MessagesRecyclerViewAdapter();
    private final PasswordEncryptedMessageDialogFragment decryptDialogFragment
            = new PasswordEncryptedMessageDialogFragment();

    private RecyclerView messagesRecyclerView;
    private TextView subjectTextView;
    private ImageView encryptedImageView;
    private ImageView starImageView;
    private View loadProgress;
    private ViewGroup messageActionsLayout;
    private Pair<MessageProvider, AttachmentProvider[]> latestDownloadAttempt;

    private final ActivityResultLauncher<String[]> downloadAttachmentPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    Timber.e("Not all permissions granted");
                    return;
                }
                if (latestDownloadAttempt == null) {
                    Timber.e("latestDownloadAttempt == null");
                    return;
                }
                ViewMessagesFragment.this.downloadAttachments(getContext(),
                        latestDownloadAttempt.first, latestDownloadAttempt.second);
            });

    private final AttachmentDownloader onAttachmentDownloading = new AttachmentDownloader() {
        @Override
        public void downloadAttachment(MessageProvider message, AttachmentProvider attachment) {
            Context context = getContext();
            if (context == null) {
                Timber.e("context == null");
                return;
            }
            String documentUrl = attachment.getDocumentUrl();
            if (documentUrl == null) {
                Timber.e("documentUrl == null");
                ToastUtils.showToast(context, R.string.error_attachment_url);
                return;
            }
            if (!PermissionUtils.readExternalStorage(context) || !PermissionUtils.writeExternalStorage(context)) {
                Timber.w("Storage permissions required");
                latestDownloadAttempt = new Pair<>(message, new AttachmentProvider[]{attachment});
                downloadAttachmentPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
                return;
            }
            ViewMessagesFragment.this.downloadAttachments(context, message, attachment);
        }

        @Override
        public void downloadAttachments(MessageProvider message, AttachmentProvider[] attachments) {
            Context context = getContext();
            if (context == null) {
                Timber.e("context == null");
                return;
            }
            for (AttachmentProvider attachment : attachments) {
                String documentUrl = attachment.getDocumentUrl();
                if (documentUrl == null) {
                    Timber.e("documentUrl == null");
                    ToastUtils.showToast(context, R.string.error_attachment_url);
                    return;
                }
            }
            if (!PermissionUtils.readExternalStorage(context) || !PermissionUtils.writeExternalStorage(context)) {
                Timber.w("Storage permissions required");
                latestDownloadAttempt = new Pair<>(message, attachments);
                downloadAttachmentPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
                return;
            }
            ViewMessagesFragment.this.downloadAttachments(context, message, attachments);
        }
    };

    private void downloadAttachments(Context context, MessageProvider message, AttachmentProvider... attachments) {
        DownloadAttachmentTask task = new DownloadAttachmentTask();
        task.title = message.getSubject();
        List<DownloadAttachmentInfo> attachmentsList = new ArrayList<>(attachments.length);
        for (AttachmentProvider attachment : attachments) {
            DownloadAttachmentInfo attachmentInfo = new DownloadAttachmentInfo(attachment.getDocumentUrl(), attachment.getName());
            if (attachment.isEncrypted()) {
                if (message.getEncryptionMessage() == null) {
                    attachmentInfo.pgpEncryption = new DownloadAttachmentInfo.PgpEncryption();
                    attachmentInfo.pgpEncryption.mailboxId = parentMessage != null
                            ? parentMessage.getMailboxId()
                            : viewModel.getDefaultMailbox().getId();
                    attachmentInfo.pgpEncryption.password = viewModel.getUserPassword();
                } else {
                    attachmentInfo.gpgEncryption = new DownloadAttachmentInfo.GpgEncryption();
                    attachmentInfo.gpgEncryption.password = message.getEncryptionMessage().getPassword();
                    if (attachmentInfo.gpgEncryption.password == null) {
                        ToastUtils.showLongToast(context, getString(R.string.firstly_decrypt_message));
                        return;
                    }
                }
            }
            attachmentsList.add(attachmentInfo);
        }
        task.attachments = attachmentsList.toArray(new DownloadAttachmentInfo[0]);
        DownloadAttachmentService.start(context, task);
        ToastUtils.showToast(getActivity(), R.string.toast_download_started);
    }


    public ViewMessagesFragment() {
        decryptDialogFragment.setCallback((messageProvider, decryptedMessage, decryptedSubject) -> {
            messageProvider.getEncryptionMessage().setDecryptedMessage(decryptedMessage);
            messagesRecyclerViewAdapter.updateItem(messageProvider);
            if (decryptedSubject != null) {
                messageProvider.setDecryptedSubject(decryptedSubject);
                if (parentMessage == messageProvider) {
                    subjectTextView.setText(decryptedSubject);
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        View root = inflater.inflate(R.layout.fragment_view_messages, container, false);

        messagesRecyclerView = root.findViewById(R.id.activity_view_messages_messages_recycler_view);
        subjectTextView = root.findViewById(R.id.activity_view_messages_subject_text);
        encryptedImageView = root.findViewById(R.id.activity_view_messages_subject_encrypted_image);
        starImageView = root.findViewById(R.id.activity_view_messages_subject_star_image);
        loadProgress = root.findViewById(R.id.activity_view_messages_progress);
        Toolbar toolbar = root.findViewById(R.id.activity_view_messages_bar);
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

        mainModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(ViewMessagesViewModel.class);

        Bundle args = getArguments();
        long parentId = -1;
        if (args != null) {
            parentId = args.getLong(PARENT_ID, -1);
            currentFolder = args.getString(FOLDER_NAME);
        }
        if (parentId < 0) {
            activity.onBackPressed();
        }
        viewModel.getChainMessages(parentId);
        viewModel.getMessagesResponse().observe(getViewLifecycleOwner(), messagesList -> {
            if (messagesList == null || messagesList.isEmpty()) {
                Timber.e("Messages doesn't exists");
                ToastUtils.showToast(activity.getApplicationContext(), R.string.toast_messages_doesnt_exist);
                activity.onBackPressed();
                return;
            }

            MessageProvider currentParentMessage = messagesList.get(0);
            if (currentParentMessage.getEncryptionMessage() == null) {
                subjectTextView.setText(currentParentMessage.getSubject());
            } else {
                if (currentParentMessage.getEncryptionMessage().isMessageDecrypted()) {
                    subjectTextView.setText(currentParentMessage.getSubject());
                } else {
                    subjectTextView.setText(null);
                }
            }
            parentMessage = currentParentMessage;

            lastMessage = messagesList.get(messagesList.size() - 1);
            encryptedImageView.setSelected(parentMessage.isProtected());
            starImageView.setSelected(parentMessage.isStarred());
            if (viewModel.isAutoReadEmailEnabled()) {
                parentMessage.setRead(true);
            }

            messagesRecyclerView.setAdapter(messagesRecyclerViewAdapter);
            messagesRecyclerViewAdapter.setItems(messagesList);
            messagesRecyclerViewAdapter.setOnAttachmentDownloadingCallback(onAttachmentDownloading);
            messagesRecyclerViewAdapter.setCallback(item ->
                    decryptDialogFragment.show(getParentFragmentManager(), item));

            loadProgress.setVisibility(View.GONE);
            activity.invalidateOptionsMenu();
        });

        viewModel.getStarredResponse().observe(getViewLifecycleOwner(), isStarred -> {
            starImageView.setSelected(isStarred);
            if (parentMessage != null) {
                parentMessage.setStarred(isStarred);
            }
        });
        viewModel.getReadResponse().observe(getViewLifecycleOwner(), isRead -> {
            if (parentMessage != null) {
                parentMessage.setRead(isRead);
            }
            activity.invalidateOptionsMenu();
            ToastUtils.showToast(getActivity(), isRead ? R.string.toast_message_marked_read
                    : R.string.toast_message_marked_unread);
        });
        viewModel.getAddWhitelistStatus().observe(getViewLifecycleOwner(), responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                ToastUtils.showToast(getActivity(), R.string.added_to_whitelist);
                getActivity().onBackPressed();
            } else {
                ToastUtils.showToast(getActivity(), R.string.adding_whitelist_contact_error);
            }
        });
        mainModel.getToFolderStatus().observe(getViewLifecycleOwner(), responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                ToastUtils.showToast(getActivity(), R.string.error_connection);
            } else {
                Activity viewActivity = getActivity();
                viewActivity.onBackPressed();
            }
        });
        mainModel.getDeleteMessagesStatus().observe(getViewLifecycleOwner(), responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                ToastUtils.showToast(getActivity(), R.string.error_connection);
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.view_messages_menu, menu);
        if (currentFolder == null) {
            currentFolder = mainModel.getCurrentFolder().getValue();
            if (currentFolder == null) {
                Timber.e("currentFolder == null");
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
                break;
            default:
                menu.findItem(R.id.menu_view_not_spam).setVisible(false);
                menu.findItem(R.id.menu_view_spam).setVisible(false);
                break;
        }
        if (parentMessage != null && parentMessage.isRead()) {
            menu.findItem(R.id.menu_view_unread).setVisible(true);
            menu.findItem(R.id.menu_view_read).setVisible(false);
        } else {
            menu.findItem(R.id.menu_view_unread).setVisible(false);
            menu.findItem(R.id.menu_view_read).setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Activity activity = getActivity();
        if (parentMessage == null || activity == null) {
            Timber.e("parentMessage == null || activity == null");
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_view_archive:
                snackbarMove(ARCHIVE, getString(R.string.action_archived));
                return true;
            case R.id.menu_view_inbox:
                snackbarMove(INBOX, getString(R.string.action_moved_to_inbox));
                return true;
            case R.id.menu_view_spam:
                snackbarMove(SPAM, getString(R.string.action_reported_as_spam));
                return true;
            case R.id.menu_view_not_spam:
                snackbarMove(INBOX, getString(R.string.action_reported_as_not_spam));
                return true;
            case R.id.menu_view_trash:
                snackbarDelete(TRASH, getString(R.string.action_message_removed));
                return true;
            case R.id.menu_view_unread:
                viewModel.markMessageAsRead(parentMessage.getId(), false);
                return true;
            case R.id.menu_view_read:
                viewModel.markMessageAsRead(parentMessage.getId(), true);
                return true;
            case R.id.menu_view_move:
                showMoveDialog();
                return true;
            case android.R.id.home:
                activity.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void snackbarDelete(String folder, String message) {
        Snackbar snackbar = Snackbar.make(messageActionsLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(getString(R.string.action_undo), view -> blockUI());
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION && parentMessage != null) {
                    if (currentFolder.equals(TRASH)) {
                        mainModel.deleteMessages(new Long[]{parentMessage.getId()});
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
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    private void snackbarMove(final String folder, String message) {
        Snackbar snackbar = Snackbar.make(messageActionsLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(getString(R.string.action_undo), view -> blockUI());
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION && parentMessage != null) {
                    mainModel.toFolder(parentMessage.getId(), folder);
                }
                unlockUI();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    private void showMoveDialog() {
        if (parentMessage == null) {
            Timber.e("showMoveDialog: parentMessage is null");
            return;
        }
        MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
        Bundle moveFragmentBundle = new Bundle();
        moveFragmentBundle.putLongArray(MESSAGE_IDS, new long[]{parentMessage.getId()});
        moveDialogFragment.setArguments(moveFragmentBundle);
        moveDialogFragment.show(getActivity().getSupportFragmentManager(), "MoveDialogFragment");
    }

    private void blockUI() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("blockUI: activity is null");
            return;
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void unlockUI() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private String replyHead() {
        Date messageDate = DateUtils.getDeliveryDate(lastMessage);
        return getString(R.string.txt_user_wrote,
                DateUtils.getStringDate(messageDate), "<" + lastMessage.getSender() + ">");
    }

    private String addQuotesToNames(String[] names) {
        String[] nameList = new String[names.length];
        for (int i = 0; i < nameList.length; i++) {
            nameList[i] = "<" + names[i] + ">";
        }

        return TextUtils.join(", ", nameList);
    }

    private String forwardHead() {
        Date messageDate = DateUtils.getDeliveryDate(lastMessage);
        return "\n\n---------- " + getString(R.string.txt_forwarded_message) + "----------\n" +
                getString(R.string.txt_from) + " <" + lastMessage.getSender() + ">\n" +
                getString(R.string.txt_date) + ": " + DateUtils.getStringDate(messageDate) + "\n" +
                getString(R.string.txt_subject) + ": " + EditTextUtils.getText(subjectTextView) + "\n" +
                getString(R.string.txt_to) + " " + addQuotesToNames(lastMessage.getReceivers()) + "\n\n";
    }

    private void forwardMessage(boolean withAttachments) {
        boolean noEncryptionPhrase = parentMessage.getEncryptionMessage() == null;
        String forwardSubject = noEncryptionPhrase ? getString(R.string.subject_forward,
                EditTextUtils.getText(subjectTextView)) : "";
        String forwardBody = noEncryptionPhrase ? forwardHead()
                + HtmlUtils.fromHtml(lastMessage.getContent()) : "";

        Intent intentForward = new Intent(getActivity(), SendMessageActivity.class);
        intentForward.putExtra(Intent.EXTRA_SUBJECT, forwardSubject);
        intentForward.putExtra(Intent.EXTRA_TEXT, forwardBody);
        intentForward.putExtra(SendMessageActivity.LAST_ACTION, MessageActions.FORWARD);

        Bundle extras = new Bundle();
        AttachmentsEntity attachmentsEntity = withAttachments && noEncryptionPhrase
                ? new AttachmentsEntity(lastMessage.getAttachments())
                : new AttachmentsEntity(Collections.emptyList());
        extras.putSerializable(ATTACHMENT_LIST, attachmentsEntity);
        intentForward.putExtras(extras);

        Fragment fragmentForward = SendMessageFragment.newInstance(
                forwardSubject,
                forwardBody,
                new String[]{},
                new String[]{},
                new String[]{},
                MessageActions.FORWARD,
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
    public void onClick(View view) {
        int id = view.getId();
        FragmentActivity activity = getActivity();
        if (activity == null || parentMessage == null) {
            return;
        }
        boolean includeOriginalMessage = mainModel.isIncludeOriginalMessage();
        boolean noEncryptionPhrase = parentMessage.getEncryptionMessage() == null;
        String replySubject = noEncryptionPhrase ? getString(R.string.subject_reply,
                EditTextUtils.getText(subjectTextView)) : "";
        String replyBody = noEncryptionPhrase && includeOriginalMessage ? replyHead()
                + HtmlUtils.fromHtml(lastMessage.getContent()) : "";
        switch (id) {
            case R.id.activity_view_messages_reply:
                Intent intentReply = new Intent(getActivity(), SendMessageActivity.class);
                intentReply.putExtra(Intent.EXTRA_EMAIL, new String[]{lastMessage.getSender()});
                intentReply.putExtra(Intent.EXTRA_SUBJECT, replySubject);
                intentReply.putExtra(Intent.EXTRA_TEXT, replyBody);
                intentReply.putExtra(SendMessageActivity.LAST_ACTION, MessageActions.REPLY);
                intentReply.putExtra(SendMessageActivity.PARENT_ID, parentMessage.getId());

                Fragment fragmentReply = SendMessageFragment.newInstance(
                        replySubject,
                        replyBody,
                        new String[]{lastMessage.getSender()},
                        new String[]{},
                        new String[]{},
                        MessageActions.REPLY,
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
                intentReplyAll.putExtra(Intent.EXTRA_EMAIL, new String[]{lastMessage.getSender()});
                intentReplyAll.putExtra(Intent.EXTRA_SUBJECT, replySubject);
                intentReplyAll.putExtra(Intent.EXTRA_TEXT, replyBody);
                intentReplyAll.putExtra(Intent.EXTRA_CC, lastMessage.getCc());
                intentReplyAll.putExtra(Intent.EXTRA_BCC, lastMessage.getBcc());
                intentReplyAll.putExtra(SendMessageActivity.LAST_ACTION, MessageActions.REPLY);
                intentReplyAll.putExtra(SendMessageActivity.PARENT_ID, parentMessage.getId());

                Fragment fragmentReplyAll = SendMessageFragment.newInstance(
                        replySubject,
                        replyBody,
                        new String[]{lastMessage.getSender()},
                        lastMessage.getCc(),
                        lastMessage.getBcc(),
                        MessageActions.REPLY_ALL,
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
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.forward_attachments)
                        .setMessage(R.string.forward_attachments_description)
                        .setPositiveButton(R.string.yes, (dialog, which) -> forwardMessage(true))
                        .setNegativeButton(R.string.no, (dialog, which) -> forwardMessage(false))
                        .show();
                break;
            case R.id.activity_view_messages_subject_star_image_layout:
                boolean isStarred = !parentMessage.isStarred();
                viewModel.markMessageIsStarred(parentMessage.getId(), isStarred);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


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
}
