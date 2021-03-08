package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.BuildConfig;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.contacts.ContactsViewModel;
import mobileapp.ctemplar.com.ctemplarapp.main.UpgradeToPrimeFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.AttachmentsEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.EncryptionMessage;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.EncryptionMessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageAttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.SendMessageRequestProvider;
import mobileapp.ctemplar.com.ctemplarapp.services.SendMailService;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.HtmlUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import mobileapp.ctemplar.com.ctemplarapp.utils.SpaceTokenizer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.ATTACHMENT_LIST;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.LAST_ACTION;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.MESSAGE_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;

public class SendMessageFragment extends Fragment implements View.OnClickListener, ActivityInterface {
    private final static int PICK_FILE_FROM_STORAGE = 1;

    private EditText subjectEditText;
    private EditText composeEditText;

    private AppCompatMultiAutoCompleteTextView toEmailTextView;
    private AppCompatMultiAutoCompleteTextView ccTextView;
    private AppCompatMultiAutoCompleteTextView bccTextView;

    private Spinner spinnerFrom;
    private RelativeLayout ccLayout;
    private RelativeLayout bccLayout;
    private ImageView toAddIco;
    private ImageView sendMessage;
    private TextView messageAttachmentsProcessingTextView;
    private RecyclerView messageAttachmentsRecycleView;

    private ImageView sendMessageDestructIco;
    private ImageView sendMessageDelayedIco;
    private ImageView sendMessageDeadIco;
    private ImageView sendMessageAttachmentIco;
    private ImageView sendMessageEncryptIco;

    private SendMessageActivityViewModel sendModel;
    private ContactsViewModel contactsViewModel;
    private ProgressDialog sendingProgress;
    private ProgressDialog uploadProgress;

    private boolean userIsPrime;
    private long currentMessageId = -1;
    private Long parentId;
    private List<String> publicKeyList = new ArrayList<>();

    // COMPOSE OPTIONS
    private final List<String> mailboxAddresses = new ArrayList<>();
    private Date delayedDeliveryDate;
    private Date destructDeliveryDate;
    private Long deadDeliveryInHours;
    private String lastAction;
    private EncryptionMessage messageEncryptionResult;
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

    private final DelayedDeliveryDialogFragment.OnScheduleDelayedDelivery onScheduleDelayedDelivery
            = new DelayedDeliveryDialogFragment.OnScheduleDelayedDelivery() {
        @Override
        public void onSchedule(Date date) {
            delayedDeliveryDate = date;
            if (getActivity() == null) {
                return;
            }
            sendMessageDelayedIco.setSelected(date != null);
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
            sendMessageDestructIco.setSelected(date != null);
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
            sendMessageDeadIco.setSelected(timeInHours != null);
        }
    };

    private final EncryptMessageDialogFragment.OnSetEncryptMessagePassword onSetEncryptMessagePassword
            = new EncryptMessageDialogFragment.OnSetEncryptMessagePassword() {
        @Override
        public void onSet(String password, String passwordHint, Integer expireHours) {
            if (password == null && passwordHint == null && expireHours == null) {
                if (getActivity() == null) {
                    return;
                }
                messageEncryptionResult = null;
                ImageView sendMessageEncryptIco = getActivity().findViewById(R.id.fragment_send_message_encrypt_ico);
                sendMessageEncryptIco.setSelected(false);
                return;
            }

            EncryptionMessage encryptionMessage = new EncryptionMessage();
            encryptionMessage.setPassword(password);
            encryptionMessage.setPasswordHint(passwordHint);
            encryptionMessage.setExpireHours(expireHours);

            MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
            if (defaultMailbox == null) {
                return;
            }
            long mailboxId = defaultMailbox.getId();

            SendMessageRequest setEncryptionRequest = new SendMessageRequest();
            setEncryptionRequest.setMailbox(mailboxId);
            setEncryptionRequest.setEncryptionMessage(encryptionMessage);

            sendModel.setEncryptionMessage(currentMessageId, setEncryptionRequest);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            Serializable attachmentList = args.getSerializable(ATTACHMENT_LIST);
            if (attachmentList instanceof AttachmentsEntity) {
                AttachmentsEntity attachmentsEntity = (AttachmentsEntity) attachmentList;
                List<AttachmentProvider> attachmentProviderList = attachmentsEntity.getAttachmentProviderList();
                if (attachmentProviderList != null && !attachmentProviderList.isEmpty()) {
                    forwardedAttachments = attachmentProviderList;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.wtf("Activity is null");
            return null;
        }

        View root = inflater.inflate(R.layout.fragment_send_message, container, false);

        subjectEditText = root.findViewById(R.id.fragment_send_message_subject_input);
        composeEditText = root.findViewById(R.id.fragment_send_message_compose_email_input);
        toEmailTextView = root.findViewById(R.id.fragment_send_message_to_input);
        ccTextView = root.findViewById(R.id.fragment_send_message_cc_input);
        bccTextView = root.findViewById(R.id.fragment_send_message_bcc_input);
        spinnerFrom = root.findViewById(R.id.fragment_send_message_from_input_spinner);
        ccLayout = root.findViewById(R.id.fragment_send_message_cc_layout);
        bccLayout = root.findViewById(R.id.fragment_send_message_bcc_layout);
        toAddIco = root.findViewById(R.id.fragment_send_message_to_add_button);
        sendMessage = root.findViewById(R.id.fragment_send_message_send);
        messageAttachmentsProcessingTextView = root.findViewById(R.id.fragment_send_message_attachments_processing_text_view);
        messageAttachmentsRecycleView = root.findViewById(R.id.fragment_send_message_attachments);
        sendMessageDestructIco = root.findViewById(R.id.fragment_send_message_destruct_ico);
        sendMessageDelayedIco = root.findViewById(R.id.fragment_send_message_delayed_ico);
        sendMessageDeadIco = root.findViewById(R.id.fragment_send_message_dead_ico);
        sendMessageAttachmentIco = root.findViewById(R.id.fragment_send_message_attachment_ico);
        sendMessageEncryptIco = root.findViewById(R.id.fragment_send_message_encrypt_ico);

        composeEditText.setPaintFlags(composeEditText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));

        // OnClick Listeners
        root.findViewById(R.id.fragment_send_message_send).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_attachment_layout).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_delayed_layout).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_destruct_layout).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_dead_layout).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_encrypt_layout).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_to_add_button).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_back).setOnClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            String[] bundleEmails = args.getStringArray(Intent.EXTRA_EMAIL);
            String[] bundleCC = args.getStringArray(Intent.EXTRA_CC);
            String[] bundleBCC = args.getStringArray(Intent.EXTRA_BCC);
            String bundleSubject = args.getString(Intent.EXTRA_SUBJECT);
            String bundleText = args.getString(Intent.EXTRA_TEXT);
            lastAction = args.getString(LAST_ACTION);
            currentMessageId = args.getLong(MESSAGE_ID, -1);
            parentId = args.getLong(PARENT_ID, -1);
            if (parentId < 0) {
                parentId = null;
            }
            if (bundleEmails != null && bundleEmails.length > 0) {
                toEmailTextView.setText(EditTextUtils.getStringFromList(bundleEmails));
            }
            if (bundleCC != null && bundleCC.length > 0) {
                ccLayout.setVisibility(View.VISIBLE);
                ccTextView.setText(EditTextUtils.getStringFromList(bundleCC));
            }
            if (bundleBCC != null && bundleBCC.length > 0) {
                bccLayout.setVisibility(View.VISIBLE);
                bccTextView.setText(EditTextUtils.getStringFromList(bundleBCC));
            }
            if (bundleSubject != null && !bundleSubject.isEmpty()) {
                subjectEditText.setText(bundleSubject);
            }
            if (bundleText != null && !bundleText.isEmpty()) {
                composeEditText.setText(bundleText);
            }
        }

        String toEmail = EditTextUtils.getText(toEmailTextView);
        if (toEmail.isEmpty()) {
            toEmailTextView.requestFocus();
        } else {
            composeEditText.requestFocus();
            composeEditText.setSelection(0);
        }

        sendModel = new ViewModelProvider(getActivity()).get(SendMessageActivityViewModel.class);
        contactsViewModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
        if (currentMessageId == -1) {
            createMessage();
        } else {
            sendModel.openMessage(currentMessageId);
        }

        int selectedAddress = 0;
        int mailboxPosition = 0;
        List<MailboxEntity> mailboxEntities = sendModel.getMailboxes();
        for (MailboxEntity mailboxEntity : mailboxEntities) {
            if (mailboxEntity.isEnabled()) {
                mailboxAddresses.add(mailboxEntity.getEmail());
                if (mailboxEntity.isDefault()) {
                    selectedAddress = mailboxPosition;
                }
                ++mailboxPosition;
            }
        }

        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
        boolean isSignatureEnabled = sendModel.isSignatureEnabled();
        if (defaultMailbox != null && isSignatureEnabled) {
            String signatureText = defaultMailbox.getSignature();
            addSignature(signatureText);
        }

        SpinnerAdapter adapter = new ArrayAdapter<>(
                activity,
                R.layout.fragment_send_message_spinner,
                mailboxAddresses
        );
        spinnerFrom.setAdapter(adapter);
        spinnerFrom.setSelection(selectedAddress);

        messageSendAttachmentAdapter = new MessageSendAttachmentAdapter(activity);
        messageAttachmentsRecycleView.setAdapter(messageSendAttachmentAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initResponses();
        addListeners();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_FROM_STORAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri attachmentUri = data.getData();
            if (attachmentUri != null) {
                uploadAttachment(attachmentUri);
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_attachment_unable_read_path), Toast.LENGTH_SHORT).show();
                Timber.e("onActivityResult: attachmentUri is null");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (draftMessage) {
            sendMessage();
        }
    }

    @Override
    public boolean onBackPressed() {
        return !(finished || onHandleBackPressed());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelSendingProgressBar();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fragment_send_message_send:
                onClickSend();
                break;
            case R.id.fragment_send_message_attachment_layout:
                onClickAttachment();
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
        String toEmail = EditTextUtils.getText(toEmailTextView).trim();
        String ccEmail = EditTextUtils.getText(ccTextView).trim();
        String bccEmail = EditTextUtils.getText(bccTextView).trim();

        if (TextUtils.isEmpty(toEmail) || EditTextUtils.isEmailListValid(toEmail)) {
            toEmailTextView.setError(null);
        } else {
            toEmailTextView.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        if (TextUtils.isEmpty(ccEmail) || EditTextUtils.isEmailListValid(ccEmail)) {
            ccTextView.setError(null);
        } else {
            ccTextView.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        if (TextUtils.isEmpty(bccEmail) || EditTextUtils.isEmailListValid(bccEmail)) {
            bccTextView.setError(null);
        } else {
            bccTextView.setError(getString(R.string.txt_enter_valid_email));
            return;
        }
        if (attachmentsProcessingEnabled) {
            Toast.makeText(getActivity(), getString(R.string.txt_attachments_in_processing), Toast.LENGTH_SHORT).show();
            return;
        }

        sendingProgress = new ProgressDialog(getActivity());
        sendingProgress.setCanceledOnTouchOutside(false);
        sendingProgress.setMessage(getString(R.string.txt_sending_mail));
        sendingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sendingProgress.show();

        List<String> receiverList = new ArrayList<>();
        if (EditTextUtils.isNotEmpty(toEmail)) {
            receiverList.addAll(EditTextUtils.getListFromString(toEmail));
        }
        if (EditTextUtils.isNotEmpty(ccEmail)) {
            receiverList.addAll(EditTextUtils.getListFromString(ccEmail));
        }
        if (EditTextUtils.isNotEmpty(bccEmail)) {
            receiverList.addAll(EditTextUtils.getListFromString(bccEmail));
        }

        PublicKeysRequest publicKeysRequest = new PublicKeysRequest(receiverList);
        sendModel.getEmailPublicKeys(publicKeysRequest);
    }

    public void onClickAttachment() {
        if (PermissionCheck.readAndWriteExternalStorage(getActivity())) {
            Intent chooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            chooseIntent.setType("*/*");
            startActivityForResult(chooseIntent, PICK_FILE_FROM_STORAGE);
        }
    }

    public void onClickAddReceiver() {
        if (ccLayout.getVisibility() == View.GONE) {
            toAddIco.setImageResource(R.drawable.ic_remove);
            ccLayout.setVisibility(View.VISIBLE);
            bccLayout.setVisibility(View.VISIBLE);
        } else {
            toAddIco.setImageResource(R.drawable.ic_add_active);
            ccLayout.setVisibility(View.GONE);
            bccLayout.setVisibility(View.GONE);
        }
    }

    private void initResponses() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        // Load contacts for autocomplete
        contactsViewModel.getContactsResponse().observe(getViewLifecycleOwner(),
                this::handleContactsList);
        contactsViewModel.getContacts(200, 0);
        // Load keys before sending message
        sendModel.getKeyResponse().observe(getViewLifecycleOwner(), keyResponse -> {
            if (keyResponse != null && keyResponse.getKeyResult() != null && keyResponse.getKeyResult().length > 0) {
                publicKeyList = new ArrayList<>();
                for (KeyResult key : keyResponse.getKeyResult()) {
                    String emailPublicKey = key.getPublicKey();
                    publicKeyList.add(emailPublicKey);
                }
                draftMessage = false;
                sendMessage();
            }
        });

        sendModel.getGrabAttachmentStatus().observe(getViewLifecycleOwner(), aBoolean -> {
            messageAttachmentsProcessingTextView.setVisibility(View.GONE);
            attachmentsProcessingEnabled = false;
        });

        sendModel.getCreateMessageStatus()
                .observe(getViewLifecycleOwner(), responseStatus -> {
                    if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                        Toast.makeText(activity, getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        sendModel.getCreateMessageResponse()
                .observe(getViewLifecycleOwner(), messagesResult -> {
                    if (messagesResult != null) {
                        currentMessageId = messagesResult.getId();
                        grabForwardedAttachments();
                    } else {
                        Toast.makeText(activity, getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        sendModel.getOpenMessageResponse().observe(getViewLifecycleOwner(), this::loadMessageHandler);

        sendModel.getUploadAttachmentStatus()
                .observe(getViewLifecycleOwner(), responseStatus -> {
                    if (responseStatus == ResponseStatus.RESPONSE_ERROR_TOO_LARGE) {
                        Toast.makeText(activity, getString(R.string.error_upload_attachment_too_large), Toast.LENGTH_SHORT).show();
                    } else if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                        Toast.makeText(activity, getString(R.string.error_upload_attachment), Toast.LENGTH_SHORT).show();
                    }
                    if (uploadProgress != null) {
                        uploadProgress.dismiss();
                    }
                });

        sendModel.getUploadAttachmentResponse()
                .observe(getViewLifecycleOwner(), messageAttachmentProvider -> {
                    messageSendAttachmentAdapter.addAttachment(messageAttachmentProvider);
                    if (messageSendAttachmentAdapter.getItemCount() > 0) {
                        sendMessageAttachmentIco.setSelected(true);
                        sendMessage.setEnabled(true);
                    }
                });

        sendModel.getDeleteAttachmentStatus()
                .observe(getViewLifecycleOwner(), responseStatus -> {
                    if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                        int attachmentCount = messageSendAttachmentAdapter.getItemCount();
                        if (attachmentCount < 1) {
                            sendMessageAttachmentIco.setSelected(false);
                        }
                    }
                });

        sendModel.getMessageEncryptionResult()
                .observe(getViewLifecycleOwner(), messagesResult -> {
                    if (messagesResult != null) {
                        messageEncryptionResult = messagesResult.getEncryption();
                        if (messageEncryptionResult != null) {
                            sendMessageEncryptIco.setSelected(true);
                        }
                    }
                });

        sendModel.getMySelfResponse().observe(getViewLifecycleOwner(), myselfResponse -> {
            if (myselfResponse != null) {
                MyselfResult myself = myselfResponse.getResult()[0];
                userIsPrime = myself.isPrime();
//                        String joinedDate = myself.joinedDate;
//                        boolean userTrial = AppUtils.twoWeeksTrial(joinedDate);
//                        boolean userPrime = myself.isPrime;
//                        userIsPrime = userPrime || userTrial;
            }
        });
        startupBodyContent = EditTextUtils.getText(composeEditText);
    }

    private void createMessage() {
        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
        if (defaultMailbox == null) {
            return;
        }
        long mailboxId = defaultMailbox.getId();
        String mailboxEmail = defaultMailbox.getEmail();

        SendMessageRequest createMessageRequest = new SendMessageRequest(
                mailboxEmail,
                "content",
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
                Toast.makeText(getActivity(), getString(R.string.toast_message_not_loaded), Toast.LENGTH_SHORT).show();
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
            spinnerFrom.setSelection(senderPosition);
        }
        if (messageReceivers != null && messageReceivers.length > 0) {
            toEmailTextView.setText(EditTextUtils.getStringFromList(messageReceivers));
        }
        if (messageCc != null && messageCc.length > 0) {
            ccLayout.setVisibility(View.VISIBLE);
            ccTextView.setText(EditTextUtils.getStringFromList(messageCc));
        }
        if (messageBcc != null && messageBcc.length > 0) {
            bccLayout.setVisibility(View.VISIBLE);
            bccTextView.setText(EditTextUtils.getStringFromList(messageBcc));
        }
        if (messageSubject != null && !messageSubject.isEmpty()) {
            subjectEditText.setText(messageSubject);
        }
        if (messageContent != null && !messageContent.isEmpty()) {
            Spanned messageSpanned = HtmlUtils.fromHtml(messageContent);
            composeEditText.setText(messageSpanned);
        }
        if (messageDestruct != null) {
            sendMessageDestructIco.setSelected(true);
            destructDeliveryDate = messageDestruct;
        }
        if (messageDelayed != null) {
            sendMessageDelayedIco.setSelected(true);
            delayedDeliveryDate = messageDelayed;
        }
        if (messageDeadMan != null) {
            sendMessageDeadIco.setSelected(true);
            deadDeliveryInHours = messageDeadMan;
        }
        if (messageAttachmentList != null) {
            for (AttachmentProvider attachmentProvider : messageAttachmentList) {
                MessageAttachmentProvider messageAttachment = new MessageAttachmentProvider();
                messageAttachment.setId(attachmentProvider.getId());
                messageAttachment.setMessage(attachmentProvider.getMessage());
                messageAttachment.setContentId(attachmentProvider.getContentId());
                messageAttachment.setDocumentLink(attachmentProvider.getDocumentLink());
                messageSendAttachmentAdapter.addAttachment(messageAttachment);
            }
        }
        if (messageSendAttachmentAdapter.getItemCount() > 0) {
            sendMessageAttachmentIco.setSelected(true);
        }
    }

    private void handleContactsList(List<Contact> contactList) {
        if (contactList == null || contactList.isEmpty()) {
            return;
        }
        RecipientsListAdapter recipientsAdapter = new RecipientsListAdapter(
                getActivity(), R.layout.recipients_list_view_item, contactList
        );
        toEmailTextView.setAdapter(recipientsAdapter);
        ccTextView.setAdapter(recipientsAdapter);
        bccTextView.setAdapter(recipientsAdapter);

        toEmailTextView.setTokenizer(new SpaceTokenizer());
        ccTextView.setTokenizer(new SpaceTokenizer());
        bccTextView.setTokenizer(new SpaceTokenizer());
    }

    private void sendMessage() {
        Object fromEmailItem = spinnerFrom.getSelectedItem();
        if (fromEmailItem == null) {
            Timber.e("sendMessage spinnerFrom.getSelectedItem is null");
            return;
        }
        MailboxEntity fromMailboxEntity = sendModel.getMailboxByEmail(fromEmailItem.toString());
        if (fromMailboxEntity == null) {
            Timber.e("sendMessage fromMailboxEntity is null");
            return;
        }

        long mailboxId = fromMailboxEntity.getId();
        String mailboxEmail = fromMailboxEntity.getEmail();
        String subject = EditTextUtils.getText(subjectEditText);
        String compose = EditTextUtils.getText(composeEditText);
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

        String toEmail = EditTextUtils.getText(toEmailTextView).trim();
        List<String> toEmailList = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            toEmailList = EditTextUtils.getListFromString(toEmail);
        }
        sendMessageRequest.setReceivers(toEmailList);

        String ccEmail = EditTextUtils.getText(ccTextView).trim();
        List<String> ccEmailList = new ArrayList<>();
        if (!ccEmail.isEmpty()) {
            ccEmailList = EditTextUtils.getListFromString(ccEmail);
        }
        sendMessageRequest.setCc(ccEmailList);

        String bccEmail = EditTextUtils.getText(bccTextView).trim();
        List<String> bccEmailList = new ArrayList<>();
        if (!bccEmail.isEmpty()) {
            bccEmailList = EditTextUtils.getListFromString(bccEmail);
        }
        sendMessageRequest.setBcc(bccEmailList);

        List<MessageAttachmentProvider> attachments = messageSendAttachmentAdapter.getAttachmentList();
        if (attachments == null) {
            attachments = new ArrayList<>();
        }

        MailboxEntity mailboxEntity = sendModel.getMailboxById(mailboxId);
        String senderPublicKey = mailboxEntity.getPublicKey();
        publicKeyList.add(senderPublicKey);

        SendMailService.sendMessage(
                getContext(),
                currentMessageId,
                sendMessageRequest,
                publicKeyList.toArray(new String[0]),
                attachments.toArray(new MessageAttachmentProvider[0]),
                EncryptionMessageProvider.fromResponse(messageEncryptionResult)
        );
        cancelSendingProgressBar();
        if (!draftMessage) {
            finish();
        }
    }

    private void uploadAttachment(Uri attachmentUri) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (attachmentUri == null) {
            Timber.e("attachmentUri is null");
            return;
        }
        String attachmentPath = FileUtils.getPath(activity, attachmentUri);
        File attachmentFile;
        try {
            attachmentFile = new File(attachmentPath);
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.toast_attachment_unable_read_file), Toast.LENGTH_SHORT).show();
            return;
        }
        String type = activity.getContentResolver().getType(attachmentUri);
        if (type == null) {
            type = "";
        }
        MediaType mediaType = MediaType.parse(type);

        Object fromEmailItem = spinnerFrom.getSelectedItem();
        if (fromEmailItem == null) {
            Timber.e("uploadAttachment: fromEmailItem is null");
            return;
        }
        MailboxEntity fromMailboxEntity = sendModel.getMailboxByEmail(fromEmailItem.toString());
        if (fromMailboxEntity == null) {
            Timber.e("uploadAttachment: fromMailboxEntity is null");
            return;
        }
        String mailboxPublicKey = fromMailboxEntity.getPublicKey();

        RequestBody attachmentPart;
        try {
            File cacheDir = getActivity().getCacheDir();
            File encryptedFile = File.createTempFile("attachment", ".ext", cacheDir);
            EncryptUtils.encryptAttachment(attachmentFile, encryptedFile, Collections.singletonList(mailboxPublicKey));
            attachmentPart = RequestBody.create(mediaType, encryptedFile);
        } catch (IOException e) {
            Timber.e(e);
            return;
        }

        String attachmentName = attachmentFile.getName();
        MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", attachmentName, attachmentPart);

        sendModel.uploadAttachment(multipartAttachment, currentMessageId, attachmentPath, true);

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

    private void addSignature(String signatureText) {
        Spanned signatureSpanned = HtmlUtils.fromHtml(signatureText);
        if (EditTextUtils.isNotEmpty(signatureSpanned)) {
            Editable compose = composeEditText.getText();
            CharSequence signatureWithCompose = TextUtils.concat(compose, "\n\n", signatureSpanned);
            composeEditText.setText(signatureWithCompose);
        }
    }

    private boolean isSentFieldsFilled() {
        String toEmail = EditTextUtils.getText(toEmailTextView);
        String ccEmail = EditTextUtils.getText(ccTextView);
        String bccEmail = EditTextUtils.getText(bccTextView);
        String subject = EditTextUtils.getText(subjectEditText);
        String compose = EditTextUtils.getText(composeEditText);

        boolean receiversEmpty = toEmail.isEmpty() && ccEmail.isEmpty() && bccEmail.isEmpty();
        boolean contentEmpty = subject.isEmpty() && compose.isEmpty();
        return !(receiversEmpty || contentEmpty);
    }

    private boolean isMessageBodyEmpty() {
        String toEmail = EditTextUtils.getText(toEmailTextView);
        String ccEmail = EditTextUtils.getText(ccTextView);
        String bccEmail = EditTextUtils.getText(bccTextView);
        String subject = EditTextUtils.getText(subjectEditText);
        String compose = EditTextUtils.getText(composeEditText);
        return toEmail.isEmpty() && ccEmail.isEmpty() && bccEmail.isEmpty() && subject.isEmpty()
                && compose.equals(startupBodyContent);
    }

    private boolean isCTemplarRecipients() {
        String toEmail = EditTextUtils.getText(toEmailTextView);
        String ccEmail = EditTextUtils.getText(ccTextView);
        String bccEmail = EditTextUtils.getText(bccTextView);
        String domain = BuildConfig.DOMAIN;
        return toEmail.contains(domain) || ccEmail.contains(domain) || bccEmail.contains(domain);
    }

    private void showUpgradeToPrimeDialog() {
        UpgradeToPrimeFragment upgradeToPrimeFragment = new UpgradeToPrimeFragment();
        upgradeToPrimeFragment.show(getParentFragmentManager(), "UpgradeToPrimeFragment");
    }

    private void showOnlyCTemplarRecipientsAlert() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.txt_destruct_timer_hint)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void addListeners() {
        sendMessage.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessage.setEnabled(isSentFieldsFilled());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        toEmailTextView.addTextChangedListener(textWatcher);
        ccTextView.addTextChangedListener(textWatcher);
        bccTextView.addTextChangedListener(textWatcher);
        subjectEditText.addTextChangedListener(textWatcher);
        composeEditText.addTextChangedListener(textWatcher);
    }

    private void grabForwardedAttachments() {
        if (forwardedAttachments != null) {
            sendModel.grabForwardedAttachments(forwardedAttachments, currentMessageId);
            messageAttachmentsProcessingTextView.setVisibility(View.VISIBLE);
            attachmentsProcessingEnabled = true;
        }
    }

    private void cancelSendingProgressBar() {
        if (sendingProgress != null && sendingProgress.isShowing()) {
            sendingProgress.cancel();
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
            @Nullable Long parentId
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
