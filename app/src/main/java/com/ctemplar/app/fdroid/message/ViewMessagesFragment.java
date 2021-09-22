package com.ctemplar.app.fdroid.message;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.ctemplar.app.fdroid.message.SendMessageActivity.ATTACHMENT_LIST;
import static com.ctemplar.app.fdroid.message.ViewMessagesActivity.PARENT_ID;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.ARCHIVE;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.INBOX;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.OUTBOX;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.SENT;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.SPAM;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.TRASH;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ctemplar.app.fdroid.ActivityInterface;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.main.MainActivity;
import com.ctemplar.app.fdroid.main.MainActivityViewModel;
import com.ctemplar.app.fdroid.message.dialog.MoveDialogFragment;
import com.ctemplar.app.fdroid.message.dialog.PasswordEncryptedMessageDialogFragment;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.entity.AttachmentsEntity;
import com.ctemplar.app.fdroid.repository.constant.MessageActions;
import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.repository.provider.UserDisplayProvider;
import com.ctemplar.app.fdroid.utils.AppUtils;
import com.ctemplar.app.fdroid.utils.DateUtils;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.EncryptUtils;
import com.ctemplar.app.fdroid.utils.FileUtils;
import com.ctemplar.app.fdroid.utils.HtmlUtils;
import com.ctemplar.app.fdroid.utils.PermissionUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import timber.log.Timber;

public class ViewMessagesFragment extends Fragment implements View.OnClickListener, ActivityInterface {
    public static final String FOLDER_NAME = "folder_name";
    public static final String ENCRYPTED_EXT = ".encrypted";

    private MainActivityViewModel mainModel;
    private ViewMessagesViewModel viewModel;
    private MessageProvider parentMessage;
    private MessageProvider lastMessage;
    private String currentFolder;
    private final DownloadCompleteReceiver downloadReceiver = new DownloadCompleteReceiver();
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

    private final ActivityResultLauncher<String[]> downloadAttachmentPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    return;
                }
            });

    private final Map<Long, Pair<AttachmentProvider, MessageProvider>> downloadMap = new HashMap<>();
    private final OnAttachmentDownloading onAttachmentDownloading = (attachment, message) -> {
        Context context = getContext();
        if (context == null) {
            Timber.e("Context is null");
            return;
        }
        String documentUrl = attachment.getDocumentUrl();
        if (documentUrl == null) {
            Toast.makeText(context, R.string.error_attachment_url, Toast.LENGTH_SHORT).show();
            return;
        }

        File externalFilesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        String originalFileName = attachment.getName() == null
                ? AppUtils.getFileNameFromURL(documentUrl) : attachment.getName();
        File generatedFile = FileUtils.generateFileName(originalFileName, externalFilesDir);
        String fileName = generatedFile == null ? originalFileName : generatedFile.getName();
        String downloadFileName = attachment.isEncrypted() ? fileName + ENCRYPTED_EXT : fileName;

        DownloadManager.Request documentRequest = new DownloadManager.Request(Uri.parse(documentUrl));
        documentRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName);
        documentRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        if (!PermissionUtils.readExternalStorage(context) || !PermissionUtils.writeExternalStorage(context)) {
            downloadAttachmentPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
            return;
        }
        DownloadManager downloadManager = (DownloadManager) context.getApplicationContext()
                .getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            Timber.e("downloadManager is null");
            return;
        }
        long downloadId = downloadManager.enqueue(documentRequest);
        attachment.setName(fileName);
        downloadMap.put(downloadId, new Pair<>(attachment, message));
        Toast.makeText(context, R.string.toast_download_started, Toast.LENGTH_SHORT).show();
    };


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
                Toast.makeText(activity.getApplicationContext(),
                        getString(R.string.toast_messages_doesnt_exist), Toast.LENGTH_SHORT).show();
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
            if (parentMessage != null) {
                encryptedImageView.setSelected(parentMessage.isProtected());
                starImageView.setSelected(parentMessage.isStarred());
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
            boolean starred = isStarred == null ? false : isStarred;
            starImageView.setSelected(starred);
            if (parentMessage != null) {
                parentMessage.setStarred(starred);
            }
        });
        viewModel.getAddWhitelistStatus().observe(getViewLifecycleOwner(), responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                Toast.makeText(getActivity(), getString(R.string.added_to_whitelist), Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getActivity(), getString(R.string.adding_whitelist_contact_error), Toast.LENGTH_SHORT).show();
            }
        });
        mainModel.getToFolderStatus().observe(getViewLifecycleOwner(), responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else {
                Activity viewActivity = getActivity();
                viewActivity.onBackPressed();
            }
        });
        mainModel.getDeleteMessagesStatus().observe(getViewLifecycleOwner(), responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
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

        switch (id) {
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
                if (parentMessage == null) {
                    Timber.e("parentMessage is null");
                    return true;
                }
                viewModel.markMessageAsRead(parentMessage.getId(), false);
                Toast.makeText(getActivity(), R.string.toast_message_marked_unread, Toast.LENGTH_SHORT).show();
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
        snackbar.setActionTextColor(Color.YELLOW);
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
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void markNotSpam() {
        if (parentMessage != null) {
            UserDisplayProvider senderDisplay = parentMessage.getSenderDisplay();
            String senderName = senderDisplay.getName();
            String senderEmail = senderDisplay.getEmail();
            viewModel.addWhitelistContact(senderName, senderEmail);
        }
    }

    private void showMoveDialog() {
        if (parentMessage != null) {
            MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
            Bundle moveFragmentBundle = new Bundle();
            moveFragmentBundle.putLong(PARENT_ID, parentMessage.getId());
            moveDialogFragment.setArguments(moveFragmentBundle);
            moveDialogFragment.show(getActivity().getSupportFragmentManager(), "MoveDialogFragment");
        }
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
        boolean noEncryptionPhrase = parentMessage.getEncryptionMessage() == null;
        String replySubject = noEncryptionPhrase ? getString(R.string.subject_reply,
                EditTextUtils.getText(subjectTextView)) : "";
        String replyBody = noEncryptionPhrase ? replyHead()
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


    private class DownloadCompleteReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent intent) {
            Toast.makeText(ctx, getString(R.string.toast_download_complete), Toast.LENGTH_SHORT).show();
            Bundle extras = intent.getExtras();
            if (extras == null) {
                ToastUtils.showToast(ctx, "Download failed. Extras is null");
                return;
            }
            long downloadId = extras.getLong("extra_download_id");
            Pair<AttachmentProvider, MessageProvider> dataPair = downloadMap.remove(downloadId);
            if (dataPair == null) {
                ToastUtils.showToast(ctx, "Download failed. Attachment cannot be parsed");
                return;
            }
            AttachmentProvider attachment = dataPair.first;
            MessageProvider message = dataPair.second;
            try {
                File externalFilesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                String fileName = attachment.getName();

                if (attachment.isEncrypted()) {
                    Toast.makeText(ctx, getString(R.string.attachment_decryption), Toast.LENGTH_SHORT).show();

                    File encryptedFile = new File(externalFilesDir, fileName + ENCRYPTED_EXT);
                    File decryptedFile = new File(externalFilesDir, fileName);
                    boolean attachmentDecrypted;
                    if (message.getEncryptionMessage() == null) {
                        long mailboxId = parentMessage == null
                                ? viewModel.getDefaultMailbox().getId()
                                : parentMessage.getMailboxId();
                        String password = viewModel.getUserPassword();
                        attachmentDecrypted = EncryptUtils.decryptAttachment(
                                encryptedFile, decryptedFile, password, mailboxId
                        );
                    } else {
                        String password = message.getEncryptionMessage().getPassword();
                        if (password == null) {
                            ToastUtils.showLongToast(ctx, getString(R.string.firstly_decrypt_message));
                            return;
                        }
                        attachmentDecrypted = EncryptUtils.decryptAttachmentGPG(
                                encryptedFile, decryptedFile, password
                        );
                    }
                    encryptedFile.delete();

                    Uri decryptedUri = FileProvider.getUriForFile(ctx,
                            FileUtils.AUTHORITY, decryptedFile
                    );
                    if (attachmentDecrypted) {
                        openFile(decryptedUri);
                    }

                } else {
                    File downloadedFile = new File(externalFilesDir, fileName);
                    Uri fileUri = FileProvider.getUriForFile(
                            ctx, FileUtils.AUTHORITY, downloadedFile
                    );
                    openFile(fileUri);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void openFile(Uri fileUri) {
        if (getActivity() == null) {
            return;
        }
        try {
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            String fileType = getActivity().getContentResolver().getType(fileUri);
            openIntent.setDataAndType(fileUri, fileType);
            openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
        }
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
