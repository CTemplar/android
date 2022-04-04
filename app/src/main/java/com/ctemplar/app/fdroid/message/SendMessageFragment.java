package com.ctemplar.app.fdroid.message;

import static com.ctemplar.app.fdroid.message.SendMessageActivity.ATTACHMENT_LIST;
import static com.ctemplar.app.fdroid.message.SendMessageActivity.LAST_ACTION;
import static com.ctemplar.app.fdroid.message.SendMessageActivity.MAILBOX_ID;
import static com.ctemplar.app.fdroid.message.SendMessageActivity.MESSAGE_ID;
import static com.ctemplar.app.fdroid.message.SendMessageActivity.PARENT_ID;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.DRAFT;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.OUTBOX;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.SENT;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ctemplar.app.fdroid.ActivityInterface;
import com.ctemplar.app.fdroid.BuildConfig;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.contacts.ContactsViewModel;
import com.ctemplar.app.fdroid.databinding.FragmentSendMessageBinding;
import com.ctemplar.app.fdroid.main.UpgradeToPrimeWebFragment;
import com.ctemplar.app.fdroid.message.dialog.DeadMansDeliveryDialogFragment;
import com.ctemplar.app.fdroid.message.dialog.DelayedDeliveryDialogFragment;
import com.ctemplar.app.fdroid.message.dialog.DestructTimerDialogFragment;
import com.ctemplar.app.fdroid.message.dialog.EncryptMessageDialogFragment;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.entity.AttachmentsEntity;
import com.ctemplar.app.fdroid.net.request.messages.SendMessageRequest;
import com.ctemplar.app.fdroid.net.response.myself.MyselfResult;
import com.ctemplar.app.fdroid.repository.entity.Contact;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;
import com.ctemplar.app.fdroid.repository.provider.EncryptionMessageProvider;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.repository.provider.SendMessageRequestProvider;
import com.ctemplar.app.fdroid.services.SendMailService;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.EncryptUtils;
import com.ctemplar.app.fdroid.utils.FileUtils;
import com.ctemplar.app.fdroid.utils.HtmlUtils;
import com.ctemplar.app.fdroid.utils.PermissionUtils;
import com.ctemplar.app.fdroid.utils.SpaceTokenizer;
import com.ctemplar.app.fdroid.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

public class SendMessageFragment extends Fragment implements View.OnClickListener, ActivityInterface {
    private FragmentSendMessageBinding binding;
    private SendMessageActivityViewModel sendModel;
    private ContactsViewModel contactsViewModel;

    private ProgressDialog uploadProgress;
    private boolean userIsPrime;
    private long currentMessageId = -1;
    private Long parentId;

    // COMPOSE OPTIONS
    private List<String> mailboxAddresses;
    private Date delayedDeliveryDate;
    private Date destructDeliveryDate;
    private Long deadDeliveryInHours;
    private String lastAction;
    private EncryptionMessageProvider encryptionMessage;
    private List<AttachmentProvider> forwardedAttachments;

    private String startupBodyContent;
    private boolean finished;
    private boolean draftMessage = true;
    private boolean attachmentsProcessingEnabled;

    private final DelayedDeliveryDialogFragment delayedDeliveryDialogFragment = new DelayedDeliveryDialogFragment();
    private final DestructTimerDialogFragment destructTimerDialogFragment = new DestructTimerDialogFragment();
    private final DeadMansDeliveryDialogFragment deadMansDeliveryDialogFragment = new DeadMansDeliveryDialogFragment();
    private final EncryptMessageDialogFragment encryptMessageDialogFragment = new EncryptMessageDialogFragment();

    private MessageSendAttachmentAdapter messageSendAttachmentAdapter;

    private final ActivityResultLauncher<String[]> pickAttachmentPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    return;
                }
                onClickPickAttachment();
            });
    private final ActivityResultLauncher<String> pickAttachmentResultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), attachmentUri -> {
                if (attachmentUri == null) {
                    ToastUtils.showToast(getActivity(), R.string.toast_attachment_unable_read_path);
                    Timber.e("onActivityResult attachmentUri is null");
                    return;
                }
                uploadAttachment(attachmentUri);
            });
    private final DelayedDeliveryDialogFragment.OnScheduleDelayedDelivery onScheduleDelayedDelivery
            = new DelayedDeliveryDialogFragment.OnScheduleDelayedDelivery() {
        @Override
        public void onSchedule(Date date) {
            delayedDeliveryDate = date;
            if (getActivity() == null) {
                return;
            }
            binding.fragmentSendMessageDelayedIco.setSelected(date != null);
        }
    };
    private final DestructTimerDialogFragment.OnScheduleDestructTimerDelivery onScheduleDestructTimerDelivery
            = new DestructTimerDialogFragment.OnScheduleDestructTimerDelivery() {
        @Override
        public void onSchedule(Date date) {
            if (getActivity() == null) {
                return;
            }
            destructDeliveryDate = date;
            binding.fragmentSendMessageDestructIco.setSelected(date != null);
        }
    };
    private final DeadMansDeliveryDialogFragment.OnScheduleDeadMansDelivery onScheduleDeadMansDelivery
            = new DeadMansDeliveryDialogFragment.OnScheduleDeadMansDelivery() {
        @Override
        public void onSchedule(Long timeInHours) {
            if (getActivity() == null) {
                return;
            }
            deadDeliveryInHours = timeInHours;
            binding.fragmentSendMessageDeadIco.setSelected(timeInHours != null);
        }
    };
    private final EncryptMessageDialogFragment.OnSetEncryptMessagePassword onSetEncryptMessagePassword
            = new EncryptMessageDialogFragment.OnSetEncryptMessagePassword() {
        @Override
        public void onSet(String password, String passwordHint, Integer expiryHours) {
            if (password == null && passwordHint == null && expiryHours == null) {
                if (getActivity() == null) {
                    return;
                }
                encryptionMessage = null;
                binding.fragmentSendMessageEncryptIco.setSelected(false);
                return;
            }
            EncryptionMessageProvider encryptionMessage = new EncryptionMessageProvider();
            encryptionMessage.setPassword(password);
            encryptionMessage.setPasswordHint(passwordHint);
            encryptionMessage.setExpiryHours(expiryHours);
            MailboxEntity fromMailboxEntity = getCurrentSelectedMailbox();
            if (fromMailboxEntity == null) {
                return;
            }
            long mailboxId = fromMailboxEntity.getId();
            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.setMailbox(mailboxId);
            sendMessageRequest.setEncryptionMessage(encryptionMessage.toRequest());
            sendModel.setEncryptionMessage(currentMessageId, sendMessageRequest);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            Timber.e("args is null");
            return;
        }
        Serializable attachmentList = args.getSerializable(ATTACHMENT_LIST);
        if (attachmentList instanceof AttachmentsEntity) {
            AttachmentsEntity attachmentsEntity = (AttachmentsEntity) attachmentList;
            List<AttachmentProvider> attachmentProviderList = attachmentsEntity.getAttachmentProviderList();
            if (attachmentProviderList != null && !attachmentProviderList.isEmpty()) {
                forwardedAttachments = attachmentProviderList;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        binding = FragmentSendMessageBinding.inflate(inflater, container, false);
        binding.fragmentSendMessageComposeEmailInput.setPaintFlags(
                binding.fragmentSendMessageComposeEmailInput.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        messageSendAttachmentAdapter = new MessageSendAttachmentAdapter(activity);
        binding.fragmentSendMessageAttachments.setAdapter(messageSendAttachmentAdapter);

        long bundleMailboxId = -1;
        Bundle args = getArguments();
        if (args != null) {
            String[] bundleEmails = args.getStringArray(Intent.EXTRA_EMAIL);
            String[] bundleCC = args.getStringArray(Intent.EXTRA_CC);
            String[] bundleBCC = args.getStringArray(Intent.EXTRA_BCC);
            String bundleSubject = args.getString(Intent.EXTRA_SUBJECT);
            String bundleText = args.getString(Intent.EXTRA_TEXT);
            bundleMailboxId = args.getLong(MAILBOX_ID, -1);
            lastAction = args.getString(LAST_ACTION);
            currentMessageId = args.getLong(MESSAGE_ID, -1);
            parentId = args.getLong(PARENT_ID, -1);
            if (parentId < 0) {
                parentId = null;
            }
            if (bundleEmails != null && bundleEmails.length > 0) {
                binding.fragmentSendMessageToInput.setText(EditTextUtils.getStringFromList(bundleEmails));
            }
            if (bundleCC != null && bundleCC.length > 0) {
                binding.fragmentSendMessageCcLayout.setVisibility(View.VISIBLE);
                binding.fragmentSendMessageCcInput.setText(EditTextUtils.getStringFromList(bundleCC));
            }
            if (bundleBCC != null && bundleBCC.length > 0) {
                binding.fragmentSendMessageBccLayout.setVisibility(View.VISIBLE);
                binding.fragmentSendMessageBccInput.setText(EditTextUtils.getStringFromList(bundleBCC));
            }
            if (bundleSubject != null && !bundleSubject.isEmpty()) {
                binding.fragmentSendMessageSubjectInput.setText(bundleSubject);
            }
            if (bundleText != null && !bundleText.isEmpty()) {
                binding.fragmentSendMessageComposeEmailInput.setText(bundleText);
            }
        }
        sendModel = new ViewModelProvider(getActivity()).get(SendMessageActivityViewModel.class);
        contactsViewModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
        if (currentMessageId == -1) {
            createMessage();
        } else {
            sendModel.openMessage(currentMessageId);
        }
        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
        MailboxEntity bundleMailbox = sendModel.getMailboxById(bundleMailboxId);
        mailboxAddresses = sendModel.getEnabledMailboxAddresses();
        SpinnerAdapter adapter = new ArrayAdapter<>(
                activity,
                R.layout.fragment_send_message_spinner,
                mailboxAddresses
        );
        binding.fragmentSendMessageFromInputSpinner.setAdapter(adapter);
        if (defaultMailbox != null) {
            binding.fragmentSendMessageFromInputSpinner.setSelection(mailboxAddresses.indexOf(defaultMailbox.getEmail()));
        }
        if (bundleMailbox != null) {
            binding.fragmentSendMessageFromInputSpinner.setSelection(mailboxAddresses.indexOf(bundleMailbox.getEmail()));
        }
        boolean isSignatureEnabled = sendModel.isSignatureEnabled();
        if (defaultMailbox != null && isSignatureEnabled) {
            String signatureText = defaultMailbox.getSignature();
            addSignature(signatureText);
        }
        if (EditTextUtils.getText(binding.fragmentSendMessageToInput).isEmpty()) {
            binding.fragmentSendMessageToInput.requestFocus();
        } else {
            binding.fragmentSendMessageComposeEmailInput.requestFocus();
            new Handler().post(() -> binding.fragmentSendMessageComposeEmailInput.setSelection(0));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleRequests();
        addListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        // prevent double sending
        if (draftMessage) {
            sendMessage();
        }
    }

    @Override
    public boolean onBackPressed() {
        return !(finished || onHandleBackPressed());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fragment_send_message_send:
                onClickSend();
                break;
            case R.id.fragment_send_message_attachment_layout:
                onClickPickAttachment();
                break;
            case R.id.fragment_send_message_to_add_button:
                onClickAddReceiver();
                break;
            case R.id.fragment_send_message_back:
                Activity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
                break;
            case R.id.fragment_send_message_encrypt_layout:
                if (encryptMessageDialogFragment.isAdded()) {
                    break;
                }
                encryptMessageDialogFragment.show(getParentFragmentManager(), "EncryptMessageDialogFragment");
                encryptMessageDialogFragment.setEncryptMessagePassword(onSetEncryptMessagePassword);
                break;
            case R.id.fragment_send_message_destruct_layout:
                if (destructTimerDialogFragment.isAdded()) {
                    break;
                }
                if (!userIsPrime) {
                    showUpgradeToPrimeDialog();
                    break;
                }
                if (!isCTemplarRecipients()) {
                    showOnlyCTemplarRecipientsAlert();
                    break;
                }
                destructTimerDialogFragment.show(getParentFragmentManager(), "DestructTimerDialogFragment");
                destructTimerDialogFragment.setOnScheduleDestructTimerDelivery(onScheduleDestructTimerDelivery);
                break;
            case R.id.fragment_send_message_delayed_layout:
                if (delayedDeliveryDialogFragment.isAdded()) {
                    break;
                }
                if (!userIsPrime) {
                    showUpgradeToPrimeDialog();
                    break;
                }
                delayedDeliveryDialogFragment.show(getParentFragmentManager(), "DelayedDeliveryDialogFragment");
                delayedDeliveryDialogFragment.setOnScheduleDelayedDelivery(onScheduleDelayedDelivery);
                break;
            case R.id.fragment_send_message_dead_layout:
                if (deadMansDeliveryDialogFragment.isAdded()) {
                    break;
                }
                if (!userIsPrime) {
                    showUpgradeToPrimeDialog();
                    break;
                }
                deadMansDeliveryDialogFragment.show(getParentFragmentManager(), "DeadMansDialogFragment");
                deadMansDeliveryDialogFragment.setOnScheduleDeadMansDelivery(onScheduleDeadMansDelivery);
                break;
        }
    }

    public void onClickSend() {
        String toEmail = EditTextUtils.getText(binding.fragmentSendMessageToInput).trim();
        if (TextUtils.isEmpty(toEmail) || EditTextUtils.isEmailListValid(toEmail)) {
            binding.fragmentSendMessageToInput.setError(null);
        } else {
            binding.fragmentSendMessageToInput.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        String ccEmail = EditTextUtils.getText(binding.fragmentSendMessageCcInput).trim();
        if (TextUtils.isEmpty(ccEmail) || EditTextUtils.isEmailListValid(ccEmail)) {
            binding.fragmentSendMessageCcInput.setError(null);
        } else {
            binding.fragmentSendMessageCcInput.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        String bccEmail = EditTextUtils.getText(binding.fragmentSendMessageBccInput).trim();
        if (TextUtils.isEmpty(bccEmail) || EditTextUtils.isEmailListValid(bccEmail)) {
            binding.fragmentSendMessageBccInput.setError(null);
        } else {
            binding.fragmentSendMessageBccInput.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        if (attachmentsProcessingEnabled) {
            ToastUtils.showToast(getActivity(), R.string.txt_attachments_in_processing);
            return;
        }
        draftMessage = false;
        sendMessage();
    }

    public void onClickPickAttachment() {
        if (!PermissionUtils.readExternalStorage(getActivity())) {
            pickAttachmentPermissionLauncher.launch(PermissionUtils.externalStoragePermissions());
            return;
        }
        pickAttachmentResultLauncher.launch("*/*");
    }

    public void onClickAddReceiver() {
        if (binding.fragmentSendMessageCcLayout.getVisibility() == View.GONE) {
            binding.fragmentSendMessageToAddButton.setImageResource(R.drawable.ic_remove);
            binding.fragmentSendMessageCcLayout.setVisibility(View.VISIBLE);
            binding.fragmentSendMessageBccLayout.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentSendMessageToAddButton.setImageResource(R.drawable.ic_add_active);
            binding.fragmentSendMessageCcLayout.setVisibility(View.GONE);
            binding.fragmentSendMessageBccLayout.setVisibility(View.GONE);
        }
    }

    private void handleRequests() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        // Load contacts for autocomplete
        contactsViewModel.getContactsResponse().observe(getViewLifecycleOwner(),
                this::handleContactsList);
        contactsViewModel.getContacts(200, 0);
        sendModel.getGrabAttachmentStatus().observe(getViewLifecycleOwner(), aBoolean -> {
            binding.fragmentSendMessageAttachmentsProcessingTextView.setVisibility(View.GONE);
            attachmentsProcessingEnabled = false;
        });
        sendModel.getCreateMessageStatus()
                .observe(getViewLifecycleOwner(), responseStatus -> {
                    if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                        ToastUtils.showToast(activity, R.string.toast_message_not_created);
                        finish();
                    }
                });
        sendModel.getCreateMessageResponse()
                .observe(getViewLifecycleOwner(), messagesResult -> {
                    if (messagesResult != null) {
                        currentMessageId = messagesResult.getId();
                        grabForwardedAttachments();
                    } else {
                        ToastUtils.showToast(activity, R.string.toast_message_not_created);
                        finish();
                    }
                });
        sendModel.getOpenMessageResponse().observe(getViewLifecycleOwner(), this::loadMessageHandler);
        sendModel.getGrabAttachmentResponse()
                .observe(getViewLifecycleOwner(), messageAttachmentProvider -> {
                    messageSendAttachmentAdapter.addAttachment(messageAttachmentProvider);
                    if (messageSendAttachmentAdapter.getItemCount() > 0) {
                        binding.fragmentSendMessageAttachmentIco.setSelected(true);
                        binding.fragmentSendMessageSend.setEnabled(true);
                    }
                });
        sendModel.getDeleteAttachmentStatus()
                .observe(getViewLifecycleOwner(), responseStatus -> {
                    if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                        int attachmentCount = messageSendAttachmentAdapter.getItemCount();
                        if (attachmentCount < 1) {
                            binding.fragmentSendMessageAttachmentIco.setSelected(false);
                        }
                    }
                });
        sendModel.getMessageEncryptionResult()
                .observe(getViewLifecycleOwner(), messagesResult -> {
                    if (messagesResult == null) {
                        ToastUtils.showToast(getActivity(), R.string.error_connection);
                    } else {
                        encryptionMessage = EncryptionMessageProvider.fromResponseToProvider(
                                messagesResult.getEncryptionMessage());
                        binding.fragmentSendMessageEncryptIco.setSelected(encryptionMessage != null);
                    }
                });
        sendModel.getMySelfResponse().observe(getViewLifecycleOwner(), myselfResponse -> {
            if (myselfResponse != null) {
                MyselfResult myself = myselfResponse.getResult()[0];
                userIsPrime = myself.isPrime();
            }
        });
        startupBodyContent = EditTextUtils.getText(binding.fragmentSendMessageComposeEmailInput);
    }

    private void createMessage() {
        MailboxEntity fromMailboxEntity = getCurrentSelectedMailbox();
        if (fromMailboxEntity == null) {
            return;
        }
        long mailboxId = fromMailboxEntity.getId();
        String mailboxEmail = fromMailboxEntity.getEmail();
        SendMessageRequest createMessageRequest = new SendMessageRequest(
                mailboxEmail,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                DRAFT,
                mailboxId
        );
        sendModel.createMessage(createMessageRequest);
        sendModel.mySelfData();
    }

    private void loadMessageHandler(@Nullable MessageProvider messageProvider) {
        if (messageProvider == null) {
            if (getActivity() != null) {
                ToastUtils.showToast(getActivity(), R.string.toast_message_not_loaded);
                finish();
            }
            return;
        }
        String messageSender = messageProvider.getSender();
        String[] messageReceivers = messageProvider.getReceivers();
        String[] messageCc = messageProvider.getCc();
        String[] messageBcc = messageProvider.getBcc();
        String messageSubject = messageProvider.getSubject();
        String messageContent = messageProvider.getContent();
        Date messageDestruct = messageProvider.getDestructDate();
        Date messageDelayed = messageProvider.getDelayedDelivery();
        Long messageDeadMan = messageProvider.getDeadManDuration();
        List<AttachmentProvider> messageAttachmentList = messageProvider.getAttachments();

        if (messageSender != null && !messageSender.isEmpty()) {
            int senderPosition = mailboxAddresses.indexOf(messageSender);
            binding.fragmentSendMessageFromInputSpinner.setSelection(senderPosition);
        }
        if (messageReceivers != null && messageReceivers.length > 0) {
            binding.fragmentSendMessageToInput.setText(EditTextUtils.getStringFromList(messageReceivers));
        }
        if (messageCc != null && messageCc.length > 0) {
            binding.fragmentSendMessageCcLayout.setVisibility(View.VISIBLE);
            binding.fragmentSendMessageCcInput.setText(EditTextUtils.getStringFromList(messageCc));
        }
        if (messageBcc != null && messageBcc.length > 0) {
            binding.fragmentSendMessageBccLayout.setVisibility(View.VISIBLE);
            binding.fragmentSendMessageBccInput.setText(EditTextUtils.getStringFromList(messageBcc));
        }
        if (messageSubject != null && !messageSubject.isEmpty()) {
            binding.fragmentSendMessageSubjectInput.setText(messageSubject);
        }
        if (messageContent != null && !messageContent.isEmpty()) {
            Spanned messageSpanned = HtmlUtils.fromHtml(messageContent);
            binding.fragmentSendMessageComposeEmailInput.setText(messageSpanned);
        }
        if (messageDestruct != null) {
            binding.fragmentSendMessageDestructIco.setSelected(true);
            destructDeliveryDate = messageDestruct;
        }
        if (messageDelayed != null) {
            binding.fragmentSendMessageDelayedIco.setSelected(true);
            delayedDeliveryDate = messageDelayed;
        }
        if (messageDeadMan != null) {
            binding.fragmentSendMessageDeadIco.setSelected(true);
            deadDeliveryInHours = messageDeadMan;
        }
        if (messageAttachmentList != null) {
            messageSendAttachmentAdapter.setAttachments(messageAttachmentList);
        }
        if (messageSendAttachmentAdapter.getItemCount() > 0) {
            binding.fragmentSendMessageAttachmentIco.setSelected(true);
        }
    }

    private void handleContactsList(List<Contact> contactList) {
        if (contactList == null || contactList.isEmpty()) {
            return;
        }
        RecipientsListAdapter recipientsAdapter = new RecipientsListAdapter(
                getActivity(), R.layout.recipients_list_view_item, contactList
        );
        binding.fragmentSendMessageToInput.setAdapter(recipientsAdapter);
        binding.fragmentSendMessageCcInput.setAdapter(recipientsAdapter);
        binding.fragmentSendMessageBccInput.setAdapter(recipientsAdapter);

        binding.fragmentSendMessageToInput.setTokenizer(new SpaceTokenizer());
        binding.fragmentSendMessageCcInput.setTokenizer(new SpaceTokenizer());
        binding.fragmentSendMessageBccInput.setTokenizer(new SpaceTokenizer());
    }

    private void sendMessage() {
        MailboxEntity fromMailboxEntity = getCurrentSelectedMailbox();
        if (fromMailboxEntity == null) {
            return;
        }
        long mailboxId = fromMailboxEntity.getId();
        String mailboxEmail = fromMailboxEntity.getEmail();
        String subject = EditTextUtils.getText(binding.fragmentSendMessageSubjectInput);
        String compose = EditTextUtils.getText(binding.fragmentSendMessageComposeEmailInput);
        Spannable composeSpannable = new SpannableString(compose);

        SendMessageRequestProvider sendMessageRequest = new SendMessageRequestProvider();
        sendMessageRequest.setSender(mailboxEmail);
        sendMessageRequest.setSubject(subject);
        sendMessageRequest.setContent(HtmlUtils.toHtml(composeSpannable));
        sendMessageRequest.setMailbox(mailboxId);
        sendMessageRequest.setParent(parentId);
        sendMessageRequest.setLastAction(lastAction);
        sendMessageRequest.setHtml(true);

        sendMessageRequest.setSend(true);
        sendMessageRequest.setFolder(SENT);

        if (destructDeliveryDate != null) {
            sendMessageRequest.setDestructDate(destructDeliveryDate);
        }
        if (delayedDeliveryDate != null) {
            sendMessageRequest.setDelayedDelivery(delayedDeliveryDate);
            sendMessageRequest.setSend(false);
            sendMessageRequest.setFolder(OUTBOX);
        }
        if (deadDeliveryInHours != null) {
            sendMessageRequest.setDeadManDuration(deadDeliveryInHours);
            sendMessageRequest.setSend(false);
            sendMessageRequest.setFolder(OUTBOX);
        }
        if (draftMessage) {
            sendMessageRequest.setSend(false);
            sendMessageRequest.setFolder(DRAFT);
        }
        String toEmail = EditTextUtils.getText(binding.fragmentSendMessageToInput).trim();
        List<String> toEmailList = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            toEmailList = EditTextUtils.getListFromString(toEmail);
        }
        sendMessageRequest.setReceivers(toEmailList);

        String ccEmail = EditTextUtils.getText(binding.fragmentSendMessageCcInput).trim();
        List<String> ccEmailList = new ArrayList<>();
        if (!ccEmail.isEmpty()) {
            ccEmailList = EditTextUtils.getListFromString(ccEmail);
        }
        sendMessageRequest.setCc(ccEmailList);

        String bccEmail = EditTextUtils.getText(binding.fragmentSendMessageBccInput).trim();
        List<String> bccEmailList = new ArrayList<>();
        if (!bccEmail.isEmpty()) {
            bccEmailList = EditTextUtils.getListFromString(bccEmail);
        }
        sendMessageRequest.setBcc(bccEmailList);

        List<AttachmentProvider> attachments = messageSendAttachmentAdapter.getAttachmentList();
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        SendMailService.sendMessage(
                getContext(),
                currentMessageId,
                sendMessageRequest,
                fromMailboxEntity.getPublicKey(),
                attachments.toArray(new AttachmentProvider[0]),
                encryptionMessage,
                draftMessage
        );
        if (!draftMessage) {
            finish();
        }
    }

    private void uploadAttachment(Uri attachmentUri) {
        Activity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }
        if (attachmentUri == null) {
            Timber.e("attachmentUri is null");
            return;
        }
        InputStream attachmentInputStream;
        try {
            attachmentInputStream = activity.getContentResolver().openInputStream(attachmentUri);
        } catch (FileNotFoundException e) {
            ToastUtils.showToast(getActivity(), R.string.toast_attachment_unable_read_file);
            Timber.e(e, "attachment content resolver");
            return;
        }
        File attachmentFile = FileUtils.getFileFromInputStream(getActivity(), attachmentInputStream);
        if (attachmentFile == null) {
            Timber.e("attachment temp creation fail");
            return;
        }
        String type = activity.getContentResolver().getType(attachmentUri);
        if (type == null) {
            Timber.w("attachment type is null");
            type = "";
        }
        MediaType mediaType = MediaType.parse(type);
        MailboxEntity fromMailboxEntity = getCurrentSelectedMailbox();
        if (fromMailboxEntity == null) {
            return;
        }
        RequestBody attachmentPart;
        File encryptedFile;
        try {
            encryptedFile = File.createTempFile(UUID.randomUUID().toString(), null,
                    getActivity().getCacheDir());
            EncryptUtils.encryptAttachment(attachmentFile, encryptedFile,
                    Collections.singletonList(fromMailboxEntity.getPublicKey()));
            attachmentPart = RequestBody.create(encryptedFile, mediaType);
        } catch (IOException e) {
            Timber.e(e);
            return;
        }
        String attachmentName = FileUtils.getFileName(activity, attachmentUri);
        MultipartBody.Part document = MultipartBody.Part.createFormData("document",
                attachmentName, attachmentPart);

        sendModel.uploadAttachment(
                document, currentMessageId, false, true, type, attachmentName,
                encryptedFile.length()
        ).observe(getViewLifecycleOwner(), resource -> {
            if (uploadProgress != null) {
                uploadProgress.dismiss();
            }
            if (!resource.isSuccess()) {
                ToastUtils.showToast(activity, resource.getError());
                return;
            }
            AttachmentProvider attachmentProvider = AttachmentProvider.fromResponse(resource.getDto());
            attachmentProvider.setFilePath(attachmentFile.getAbsolutePath());
            attachmentProvider.setActualSize(attachmentFile.length());
            messageSendAttachmentAdapter.addAttachment(attachmentProvider);
            if (messageSendAttachmentAdapter.getItemCount() > 0) {
                binding.fragmentSendMessageAttachmentIco.setSelected(true);
                binding.fragmentSendMessageSend.setEnabled(true);
            }
            if (!encryptedFile.delete()) {
                Timber.w("Uploaded encrypted file not deleted");
            }
        });
        uploadProgress = new ProgressDialog(getActivity());
        uploadProgress.setCanceledOnTouchOutside(false);
        uploadProgress.setMessage(getString(R.string.txt_uploading));
        uploadProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        uploadProgress.show();
    }

    private boolean onHandleBackPressed() {
        final Activity activity = getActivity();
        if (activity == null) {
            return false;
        }
        if (isMessageBodyEmpty()) {
            draftMessage = false;
            sendModel.deleteMessage(currentMessageId);
            finish();
            return false;
        }
        if (sendModel.isDraftsAutoSaveEnabled()) {
            finish();
            return false;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(getString(R.string.dialog_discard_mail));
        alertDialog.setMessage(getString(R.string.dialog_discard_confirm));
        alertDialog.setPositiveButton(getString(R.string.dialog_save_in_drafts), (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        alertDialog.setNegativeButton(getString(R.string.action_discard), (dialog, which) -> {
            draftMessage = false;
            sendModel.deleteMessage(currentMessageId);
            dialog.dismiss();
            finish();
        });
        alertDialog.setNeutralButton(getString(R.string.action_cancel), null);
        alertDialog.show();

        return false;
    }

    @Nullable
    private MailboxEntity getCurrentSelectedMailbox() {
        Object fromEmailItem = binding.fragmentSendMessageFromInputSpinner.getSelectedItem();
        if (fromEmailItem != null) {
            MailboxEntity fromMailboxEntity = sendModel.getMailboxByEmail(fromEmailItem.toString());
            if (fromMailboxEntity != null) {
                return fromMailboxEntity;
            }
            Timber.e("fromMailboxEntity is null");
        } else {
            Timber.e("fromEmailItem is null");
        }
        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
        if (defaultMailbox != null) {
            return defaultMailbox;
        }
        Timber.e("defaultMailbox is null");
        return null;
    }

    private void addSignature(String signatureText) {
        Spanned signatureSpanned = HtmlUtils.fromHtml(signatureText);
        if (EditTextUtils.isNotEmpty(signatureSpanned)) {
            Editable compose = binding.fragmentSendMessageComposeEmailInput.getText();
            CharSequence signatureWithCompose = TextUtils.concat(compose, "\n\n", signatureSpanned);
            binding.fragmentSendMessageComposeEmailInput.setText(signatureWithCompose);
        }
    }

    private boolean isSentFieldsFilled() {
        String toEmail = EditTextUtils.getText(binding.fragmentSendMessageToInput);
        String ccEmail = EditTextUtils.getText(binding.fragmentSendMessageCcInput);
        String bccEmail = EditTextUtils.getText(binding.fragmentSendMessageBccInput);
        String subject = EditTextUtils.getText(binding.fragmentSendMessageSubjectInput);
        String compose = EditTextUtils.getText(binding.fragmentSendMessageComposeEmailInput);

        boolean receiversEmpty = toEmail.isEmpty() && ccEmail.isEmpty() && bccEmail.isEmpty();
        boolean contentEmpty = subject.isEmpty() && compose.isEmpty();
        return !(receiversEmpty || contentEmpty);
    }

    private boolean isMessageBodyEmpty() {
        String toEmail = EditTextUtils.getText(binding.fragmentSendMessageToInput);
        String ccEmail = EditTextUtils.getText(binding.fragmentSendMessageCcInput);
        String bccEmail = EditTextUtils.getText(binding.fragmentSendMessageBccInput);
        String subject = EditTextUtils.getText(binding.fragmentSendMessageSubjectInput);
        String compose = EditTextUtils.getText(binding.fragmentSendMessageComposeEmailInput);
        return toEmail.isEmpty() && ccEmail.isEmpty() && bccEmail.isEmpty() && subject.isEmpty()
                && compose.equals(startupBodyContent);
    }

    private boolean isCTemplarRecipients() {
        String toEmail = EditTextUtils.getText(binding.fragmentSendMessageToInput);
        String ccEmail = EditTextUtils.getText(binding.fragmentSendMessageCcInput);
        String bccEmail = EditTextUtils.getText(binding.fragmentSendMessageBccInput);
        String domain = BuildConfig.DOMAIN;
        return toEmail.contains(domain) || ccEmail.contains(domain) || bccEmail.contains(domain);
    }

    private void showUpgradeToPrimeDialog() {
        UpgradeToPrimeWebFragment upgradeToPrimeWebFragment = new UpgradeToPrimeWebFragment();
        upgradeToPrimeWebFragment.show(getParentFragmentManager(), "UpgradeToPrimeFragment");
    }

    private void showOnlyCTemplarRecipientsAlert() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.txt_destruct_timer_hint)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void addListeners() {
        binding.fragmentSendMessageSend.setOnClickListener(this);
        binding.fragmentSendMessageAttachmentLayout.setOnClickListener(this);
        binding.fragmentSendMessageDelayedLayout.setOnClickListener(this);
        binding.fragmentSendMessageDestructLayout.setOnClickListener(this);
        binding.fragmentSendMessageDeadLayout.setOnClickListener(this);
        binding.fragmentSendMessageEncryptLayout.setOnClickListener(this);
        binding.fragmentSendMessageToAddButton.setOnClickListener(this);
        binding.fragmentSendMessageBack.setOnClickListener(this);

        binding.fragmentSendMessageSend.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.fragmentSendMessageSend.setEnabled(isSentFieldsFilled());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.fragmentSendMessageToInput.addTextChangedListener(textWatcher);
        binding.fragmentSendMessageCcInput.addTextChangedListener(textWatcher);
        binding.fragmentSendMessageBccInput.addTextChangedListener(textWatcher);
        binding.fragmentSendMessageSubjectInput.addTextChangedListener(textWatcher);
        binding.fragmentSendMessageComposeEmailInput.addTextChangedListener(textWatcher);
    }

    private void grabForwardedAttachments() {
        if (forwardedAttachments != null) {
            sendModel.grabForwardedAttachments(forwardedAttachments, currentMessageId);
            binding.fragmentSendMessageAttachmentsProcessingTextView.setVisibility(View.VISIBLE);
            attachmentsProcessingEnabled = true;
        }
    }

    private void finish() {
        finished = true;
        Activity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
        }
    }

    public static SendMessageFragment newInstance(
            @Nullable String subject,
            @Nullable String text,
            @Nullable String[] receivers,
            @Nullable String[] cc,
            @Nullable String[] bcc,
            @Nullable String lastAction,
            @Nullable AttachmentsEntity attachmentsEntity,
            @Nullable Long parentId,
            long mailboxId
    ) {
        Bundle args = new Bundle();
        if (subject != null) {
            args.putString(Intent.EXTRA_SUBJECT, subject);
        }
        if (text != null) {
            args.putString(Intent.EXTRA_TEXT, text);
        }
        if (receivers != null && receivers.length > 0) {
            args.putStringArray(Intent.EXTRA_EMAIL, receivers);
        }
        if (cc != null && cc.length > 0) {
            args.putStringArray(Intent.EXTRA_CC, cc);
        }
        if (bcc != null && bcc.length > 0) {
            args.putStringArray(Intent.EXTRA_BCC, bcc);
        }
        if (EditTextUtils.isNotEmpty(lastAction)) {
            args.putString(LAST_ACTION, lastAction);
        }
        if (attachmentsEntity != null
                && attachmentsEntity.getAttachmentProviderList() != null
                && !attachmentsEntity.getAttachmentProviderList().isEmpty()
        ) {
            args.putSerializable(ATTACHMENT_LIST, attachmentsEntity);
        }
        if (parentId != null) {
            args.putLong(PARENT_ID, parentId);
        }
        args.putLong(MAILBOX_ID, mailboxId);

        SendMessageFragment fragment = new SendMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static SendMessageFragment newInstance() {
        return new SendMessageFragment();
    }

    public static SendMessageFragment newInstance(Bundle args) {
        SendMessageFragment fragment = newInstance();
        fragment.setArguments(args);
        return fragment;
    }
}
