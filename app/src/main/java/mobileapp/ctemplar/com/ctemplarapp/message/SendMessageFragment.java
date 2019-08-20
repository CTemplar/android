package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.UpgradeToPrimeFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.EncryptionMessage;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import mobileapp.ctemplar.com.ctemplarapp.utils.SpaceTokenizer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;

public class SendMessageFragment extends Fragment implements View.OnClickListener, ActivityInterface {

    private final static String PARENT_ID = SendMessageActivity.PARENT_ID;
    private final static String TAG = SendMessageFragment.class.getSimpleName();
    private final int PICK_FILE_FROM_STORAGE = 1;
    private boolean finished;


    public static SendMessageFragment newInstance(
            @Nullable String subject,
            @Nullable String text,
            @Nullable String[] receivers,
            @Nullable String[] cc,
            @Nullable String[] bcc,
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
    private RecyclerView messageAttachmentsRecycleView;

    private SharedPreferences sharedPreferences;
    private SendMessageActivityViewModel mainModel;
    private ProgressDialog sendingProgress;
    private boolean draftMessage = true;

    // COMPOSE OPTIONS
    private long currentMessageId;
    private Long parentId;
    private Long delayedDeliveryInMillis;
    private Long destructDeliveryInMillis;
    private Long deadDeliveryInHours;
    private EncryptionMessage messageEncryptionResult;
    private boolean userIsPrime;
    private boolean isSubjectEncrypted;

    private DelayedDeliveryDialogFragment delayedDeliveryDialogFragment = new DelayedDeliveryDialogFragment();
    private DestructTimerDialogFragment destructTimerDialogFragment = new DestructTimerDialogFragment();
    private DeadMansDeliveryDialogFragment deadMansDeliveryDialogFragment = new DeadMansDeliveryDialogFragment();
    private EncryptMessageDialogFragment encryptMessageDialogFragment = new EncryptMessageDialogFragment();

    private MessageSendAttachmentAdapter messageSendAttachmentAdapter;

    private DelayedDeliveryDialogFragment.OnScheduleDelayedDelivery onScheduleDelayedDelivery
            = new DelayedDeliveryDialogFragment.OnScheduleDelayedDelivery() {
        @Override
        public void onSchedule(Long timeInMilliseconds) {
            delayedDeliveryInMillis = timeInMilliseconds;
            if (getActivity() == null) {
                return;
            }
            ImageView sendMessageDelayedIco = getActivity().findViewById(R.id.fragment_send_message_delayed_ico);
            if (timeInMilliseconds == null) {
                sendMessageDelayedIco.setSelected(false);
            } else {
                sendMessageDelayedIco.setSelected(true);
            }
        }
    };

    private DestructTimerDialogFragment.OnScheduleDestructTimerDelivery onScheduleDestructTimerDelivery
            = new DestructTimerDialogFragment.OnScheduleDestructTimerDelivery() {
        @Override
        public void onSchedule(Long timeInMilliseconds) {
            destructDeliveryInMillis = timeInMilliseconds;
            if (getActivity() == null) {
                return;
            }
            ImageView sendMessageDestructIco = getActivity().findViewById(R.id.fragment_send_message_destruct_ico);
            if (timeInMilliseconds == null) {
                sendMessageDestructIco.setSelected(false);
            } else {
                sendMessageDestructIco.setSelected(true);
            }
        }
    };

    private DeadMansDeliveryDialogFragment.OnScheduleDeadMansDelivery onScheduleDeadMansDelivery
            = new DeadMansDeliveryDialogFragment.OnScheduleDeadMansDelivery() {
        @Override
        public void onSchedule(Long timeInHours) {
            deadDeliveryInHours = timeInHours;
            if (getActivity() == null) {
                return;
            }
            ImageView sendMessageDeadIco = getActivity().findViewById(R.id.fragment_send_message_dead_ico);
            if (timeInHours == null) {
                sendMessageDeadIco.setSelected(false);
            } else {
                sendMessageDeadIco.setSelected(true);
            }
        }
    };

    private EncryptMessageDialogFragment.OnSetEncryptMessagePassword onSetEncryptMessagePassword
            = new EncryptMessageDialogFragment.OnSetEncryptMessagePassword() {
        @Override
        public void onSet(String password, String passwordHint, Integer expireHours) {
            if (password == null && passwordHint == null && expireHours == null) {
                messageEncryptionResult = null;
                if (getActivity() == null) {
                    return;
                }
                ImageView sendMessageEncryptIco = getActivity().findViewById(R.id.fragment_send_message_encrypt_ico);
                sendMessageEncryptIco.setSelected(false);
                return;
            }

            EncryptionMessage encryptionMessage = new EncryptionMessage();
            encryptionMessage.setPassword(password);
            encryptionMessage.setPasswordHint(passwordHint);
            encryptionMessage.setExpireHours(expireHours);

            MailboxEntity defaultMailbox = MessageProvider.getDefaultMailbox();
            if (defaultMailbox == null) {
                return;
            }
            long mailboxId = defaultMailbox.id;

            SendMessageRequest setEncryptionRequest = new SendMessageRequest();
            setEncryptionRequest.setMailbox(mailboxId);
            setEncryptionRequest.setEncryptionMessage(encryptionMessage);

            mainModel.setEncryptionMessage(currentMessageId, setEncryptionRequest);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            Timber.tag(TAG).wtf("Activity is null");
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
        messageAttachmentsRecycleView = root.findViewById(R.id.fragment_send_message_attachments);


        // OnClicks
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
            parentId = args.getLong(PARENT_ID, -1);
            if (parentId < 0) {
                parentId = null;
            }

            if (bundleEmails != null && bundleEmails.length > 0) {
                String emailListString = TextUtils.join(",", bundleEmails);
                toEmailTextView.setText(emailListString);
            }
            if (bundleCC != null && bundleCC.length > 0) {
                ccLayout.setVisibility(View.VISIBLE);
                String ccListString = TextUtils.join(",", bundleCC);
                ccTextView.setText(ccListString);
            }
            if (bundleBCC != null && bundleBCC.length > 0) {
                bccLayout.setVisibility(View.VISIBLE);
                String bccListString = TextUtils.join(",", bundleBCC);
                bccTextView.setText(bccListString);
            }
            if (bundleSubject != null && !bundleSubject.isEmpty()) {
                subjectEditText.setText(bundleSubject);
            }
            if (bundleText != null && !bundleText.isEmpty()) {
                composeEditText.setText(bundleText);
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean mobileSignatureEnabled = sharedPreferences.getBoolean(getString(R.string.mobile_signature_enabled), false);
        String mobileSignature = sharedPreferences.getString(getString(R.string.mobile_signature), "");
        if (mobileSignatureEnabled) {
            String compose = composeEditText.getText().toString();
            String text = getString(R.string.txt_user_signature, mobileSignature, compose);
            composeEditText.setText(text);
        }

        String toEmail = toEmailTextView.getText().toString();
        if (toEmail.isEmpty()) {
            toEmailTextView.requestFocus();
        } else {
            composeEditText.requestFocus();
            composeEditText.setSelection(0);
        }


        mainModel = ViewModelProviders.of(this).get(SendMessageActivityViewModel.class);
        createMessage();

        int selectedAddress = 0;
        List<MailboxEntity> mailboxEntities = mainModel.getMailboxes();
        List<String> mailboxAddresses = new ArrayList<>();
        for (int position = 0; position < mailboxEntities.size(); position++) {
            MailboxEntity mailboxEntity = mailboxEntities.get(position);
            if (mailboxEntity.isEnabled) {
                mailboxAddresses.add(mailboxEntity.email);
                if (mailboxEntity.isDefault) {
                    selectedAddress = position;
                }
            }
        }

        SpinnerAdapter adapter = new ArrayAdapter<>(
                activity,
                R.layout.fragment_send_message_spinner,
                mailboxAddresses
        );
        spinnerFrom.setAdapter(adapter);
        spinnerFrom.setSelection(selectedAddress);

        messageSendAttachmentAdapter = new MessageSendAttachmentAdapter(getActivity());
        messageAttachmentsRecycleView.setAdapter(messageSendAttachmentAdapter);

        initResponses();
        addListeners();

        return root;
    }

    private void initResponses() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Load contacts to autocomplete
        mainModel.getContactsResponse().observe(this, new Observer<ContactsResponse>() {
            @Override
            public void onChanged(@Nullable ContactsResponse contactsResponse) {
                handleContactsList(contactsResponse);
            }
        });
        mainModel.getContacts(20, 0);

        // Load keys before sending message
        mainModel.getKeyResponse().observe(this, new Observer<KeyResponse>() {
            @Override
            public void onChanged(@Nullable KeyResponse keyResponse) {
                if (keyResponse != null && keyResponse.getKeyResult() != null && keyResponse.getKeyResult().length > 0) {
                    List<String> emails = new ArrayList<>();
                    List<String> publicKeys = new ArrayList<>();

                    for (KeyResult key : keyResponse.getKeyResult()) {
                        emails.add(key.getEmail());
                        publicKeys.add(key.getPublicKey());
                    }
                    sendMessage(emails, publicKeys);
                }
            }
        });

        mainModel.getMessagesResult()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult == null) {
                            Toast.makeText(activity, getString(R.string.toast_message_not_sent), Toast.LENGTH_SHORT).show();
                        } else {
                            String folderName = messagesResult.getFolderName();
                            if (!folderName.equals(MainFolderNames.DRAFT)) {
                                finish();
                            }
                        }
                    }
                });

        mainModel.getCreateMessageStatus()
                .observe(this, new Observer<ResponseStatus>() {
                    @Override
                    public void onChanged(@Nullable ResponseStatus responseStatus) {
                        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                            Toast.makeText(activity, getResources().getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        mainModel.getCreateMessageResponse()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult != null) {
                            currentMessageId = messagesResult.getId();
                        } else {
                            Toast.makeText(activity, getResources().getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        mainModel.uploadAttachmentStatus
                .observe(this, new Observer<ResponseStatus>() {
                    @Override
                    public void onChanged(@Nullable ResponseStatus responseStatus) {
                        if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                            Toast.makeText(activity, getResources().getString(R.string.toast_attachment_upload_complete), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mainModel.uploadAttachmentResponse
                .observe(this, new Observer<MessageAttachment>() {
                    @Override
                    public void onChanged(@Nullable MessageAttachment messageAttachment) {
                        if (messageAttachment != null) {
                            messageSendAttachmentAdapter.addAttachment(messageAttachment);
                        }
                        if (messageSendAttachmentAdapter.getItemCount() > 0) {
                            if (getActivity() == null) {
                                return;
                            }
                            ImageView sendMessageAttachmentIco = getActivity().findViewById(R.id.fragment_send_message_attachment_ico);
                            sendMessageAttachmentIco.setSelected(true);
                        }
                    }
                });

        mainModel.getMessageEncryptionResult()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult != null) {
                            messageEncryptionResult = messagesResult.getEncryption();

                            if (getActivity() == null) {
                                return;
                            }
                            ImageView sendMessageEncryptIco = getActivity().findViewById(R.id.fragment_send_message_encrypt_ico);
                            if (messageEncryptionResult != null) {
                                sendMessageEncryptIco.setSelected(true);
                            }
                        }
                    }
                });

        mainModel.getMySelfResponse()
                .observe(this, new Observer<MyselfResponse>() {
                    @Override
                    public void onChanged(@Nullable MyselfResponse myselfResponse) {
                        if (myselfResponse != null) {
                            MyselfResult myself = myselfResponse.result[0];
                            addSignature(myself.mailboxes[0].getSignature());
                            isSubjectEncrypted = myself.settings.isSubjectEncrypted;
                            String joinedDate = myself.joinedDate;
                            boolean userTrial = AppUtils.twoWeeksTrial(joinedDate);
                            boolean userPrime = myself.isPrime;
                            userIsPrime = userPrime || userTrial;
                        }
                    }
                });
    }

    private void createMessage() {
        MailboxEntity defaultMailbox = MessageProvider.getDefaultMailbox();
        if (defaultMailbox == null) {
            return;
        }
        long mailboxId = defaultMailbox.id;
        String mailboxEmail = defaultMailbox.email;

        SendMessageRequest createMessageRequest = new SendMessageRequest(
                mailboxEmail,
                "content",
                new ArrayList<String>(),
                new ArrayList<String>(),
                new ArrayList<String>(),
                MainFolderNames.DRAFT,
                mailboxId
        );

        mainModel.createMessage(createMessageRequest);
        mainModel.mySelfData();
    }

    private void handleContactsList(@Nullable ContactsResponse contactsResponse) {
        if (contactsResponse == null || contactsResponse.getResults() == null || contactsResponse.getResults().length == 0) {
            return; // empty list
        }

        ContactData[] contacts = contactsResponse.getResults();
        List<ContactData> contactsList = new ArrayList<>(Arrays.asList(contacts));
        RecipientsListAdapter recipientsAdapter = new RecipientsListAdapter(getActivity(), R.layout.recipients_list_view_item, contactsList);
        toEmailTextView.setAdapter(recipientsAdapter);
        ccTextView.setAdapter(recipientsAdapter);
        bccTextView.setAdapter(recipientsAdapter);

        toEmailTextView.setTokenizer(new SpaceTokenizer());
        ccTextView.setTokenizer(new SpaceTokenizer());
        bccTextView.setTokenizer(new SpaceTokenizer());
    }

    private void sendMessage(List<String> emails, final List<String> publicKeys) {
        String fromEmail = spinnerFrom.getSelectedItem().toString();
        MailboxEntity fromMailbox = CTemplarApp.getAppDatabase().mailboxDao().getByEmail(fromEmail);
        final long mailboxId = fromMailbox.id;
        String mailboxEmail = fromMailbox.email;

        String subject = subjectEditText.getText().toString();
        String compose = Html.toHtml(composeEditText.getText());

        final SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setSender(mailboxEmail);
        sendMessageRequest.setSubject(subject);
        sendMessageRequest.setContent(compose);
        sendMessageRequest.setMailbox(mailboxId);
        sendMessageRequest.setParent(parentId);
        sendMessageRequest.setSubjectEncrypted(isSubjectEncrypted);

        if (!publicKeys.contains(null)) {
            sendMessageRequest.setIsEncrypted(true);
        }

        if (destructDeliveryInMillis != null) {
            sendMessageRequest.setDestructDate(AppUtils.datetimeForServer(destructDeliveryInMillis));
        }

        draftMessage = false;
        String messageFolder = SENT;
        boolean messageSent = true;
        if (delayedDeliveryInMillis != null) {
            sendMessageRequest.setDelayedDelivery(AppUtils.datetimeForServer(delayedDeliveryInMillis));
            messageFolder = OUTBOX;
            messageSent = false;
        }
        if (deadDeliveryInHours != null) {
            sendMessageRequest.setDeadManDuration(deadDeliveryInHours);
            messageFolder = OUTBOX;
            messageSent = false;
        }
        sendMessageRequest.setSend(messageSent);
        sendMessageRequest.setFolder(messageFolder);

        String toEmail = toEmailTextView.getText().toString().trim();
        List<String> toEmailList = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            toEmailList = EditTextUtils.getListFromString(toEmail);
        }
        sendMessageRequest.setReceivers(toEmailList);

        String ccEmail = ccTextView.getText().toString().trim();
        List<String> ccEmailList = new ArrayList<>();
        if (!ccEmail.isEmpty()) {
            ccEmailList = EditTextUtils.getListFromString(ccEmail);
        }
        sendMessageRequest.setCc(ccEmailList);

        String bccEmail = bccTextView.getText().toString().trim();
        List<String> bccEmailList = new ArrayList<>();
        if (!bccEmail.isEmpty()) {
            bccEmailList = EditTextUtils.getListFromString(bccEmail);
        }
        sendMessageRequest.setBcc(bccEmailList);

        List<MessageAttachment> attachments = messageSendAttachmentAdapter.getAttachmentsList();
        if (attachments != null && !attachments.isEmpty()) {
            sendMessageRequest.setAttachments(attachments);
        }

        if (messageEncryptionResult != null) {
            sendMessageRequest.setEncryptionMessage(messageEncryptionResult);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mainModel.updateMessage(currentMessageId, sendMessageRequest, publicKeys, mailboxId);
                if (sendingProgress != null) {
                    sendingProgress.dismiss();
                }
            }
        }).start();
    }

    private boolean inputFieldsNotEmpty() {
        String toEmail = toEmailTextView.getText().toString();
        String ccEmail = ccTextView.getText().toString();
        String bccEmail = bccTextView.getText().toString();
        String subject = subjectEditText.getText().toString();
        String compose = composeEditText.getText().toString();

        boolean receiversNotEmpty = !toEmail.isEmpty() || !ccEmail.isEmpty() || !bccEmail.isEmpty();
        boolean contentNotEmpty = !subject.isEmpty() || !compose.isEmpty();
        return receiversNotEmpty && contentNotEmpty;
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
            case R.id.fragment_send_message_delayed_layout:
                if (getFragmentManager() != null && userIsPrime) {
                    delayedDeliveryDialogFragment.show(getFragmentManager(), "DelayedDeliveryDialogFragment");
                    delayedDeliveryDialogFragment.setOnScheduleDelayedDelivery(onScheduleDelayedDelivery);
                } else {
                    upgradeToPrimeDialog();
                }
                break;
            case R.id.fragment_send_message_destruct_layout:
                if (getFragmentManager() != null) {
                    destructTimerDialogFragment.show(getFragmentManager(), "DestructTimerDialogFragment");
                    destructTimerDialogFragment.setOnScheduleDestructTimerDelivery(onScheduleDestructTimerDelivery);
                }
                break;
            case R.id.fragment_send_message_dead_layout:
                if (getFragmentManager() != null && userIsPrime) {
                    deadMansDeliveryDialogFragment.show(getFragmentManager(), "DeadMansDialogFragment");
                    deadMansDeliveryDialogFragment.setOnScheduleDeadMansDelivery(onScheduleDeadMansDelivery);
                } else {
                    upgradeToPrimeDialog();
                }
                break;
            case R.id.fragment_send_message_encrypt_layout:
                if (getFragmentManager() != null) {
                    encryptMessageDialogFragment.show(getFragmentManager(), "EncryptMessageDialogFragment");
                    encryptMessageDialogFragment.setEncryptMessagePassword(onSetEncryptMessagePassword);
                }
        }
    }

    private void upgradeToPrimeDialog() {
        if (getFragmentManager() != null) {
            UpgradeToPrimeFragment upgradeToPrimeFragment = new UpgradeToPrimeFragment();
            upgradeToPrimeFragment.show(getFragmentManager(), "UpgradeToPrimeFragment");
        }
    }

    public void onClickSend() {
        String toEmail = toEmailTextView.getText().toString().trim();
        String ccEmail = ccTextView.getText().toString().trim();
        String bccEmail = bccTextView.getText().toString().trim();

        if (toEmail.isEmpty() || EditTextUtils.isEmailListValid(toEmail)) {
            toEmailTextView.setError(null);
        } else {
            toEmailTextView.setError(getString(R.string.txt_enter_valid_email));
            return;
        }

        if (ccEmail.isEmpty() || EditTextUtils.isEmailListValid(ccEmail)) {
            ccTextView.setError(null);
        } else {
            ccTextView.setError(getString(R.string.txt_enter_valid_email));
            return;
        }

        if (bccEmail.isEmpty() || EditTextUtils.isEmailListValid(bccEmail)) {
            bccTextView.setError(null);
        } else {
            bccTextView.setError(getString(R.string.txt_enter_valid_email));
            return;
        }

        sendingProgress = new ProgressDialog(getActivity());
        sendingProgress.setCanceledOnTouchOutside(false);
        sendingProgress.setMessage(getResources().getString(R.string.txt_sending_mail));
        sendingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sendingProgress.show();

        List<String> receiverList = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            receiverList.addAll(EditTextUtils.getListFromString(toEmail));
        }
        if (!ccEmail.isEmpty()) {
            receiverList.addAll(EditTextUtils.getListFromString(ccEmail));
        }
        if (!bccEmail.isEmpty()) {
            receiverList.addAll(EditTextUtils.getListFromString(bccEmail));
        }

        PublicKeysRequest publicKeysRequest = new PublicKeysRequest(receiverList);
        mainModel.getEmailPublicKeys(publicKeysRequest);
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

    private boolean onHandleBackPressed() {
        final Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        new AlertDialog.Builder(activity)
                .setTitle(getResources().getString(R.string.dialog_discard_mail))
                .setMessage(getResources().getString(R.string.dialog_discard_confirm))
                .setPositiveButton(getResources().getString(R.string.dialog_save_in_drafts), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMessageToDraft();
                                dialog.dismiss();
                                finish();
                            }
                        }
                )
                .setNegativeButton(getResources().getString(R.string.action_discard), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                draftMessage = false;
                                mainModel.deleteMessage(currentMessageId);
                                dialog.dismiss();
                                finish();
                            }
                        }
                )
                .setNeutralButton(getResources().getString(R.string.action_cancel), null)
                .show();

        return false;
    }

    @Override
    public boolean onBackPressed() {
        return finished || onHandleBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_FROM_STORAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri attachmentUri = data.getData();
            if (attachmentUri != null) {
                uploadAttachment(attachmentUri);
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_attachment_unable_read_path), Toast.LENGTH_SHORT).show();
                Timber.e("AttachmentUri is null");
            }
        }
    }

    private void uploadAttachment(Uri attachmentUri) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        String attachmentPath = FileUtils.getPath(activity, attachmentUri);
        File attachmentFile;
        try {
            attachmentFile = new File(attachmentPath);
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.toast_attachment_unable_read_file), Toast.LENGTH_SHORT).show();
            Timber.e(e);
            return;
        }

        MediaType mediaType;
        String type = activity.getContentResolver().getType(attachmentUri);
        if (type == null) {
            Timber.e("Attachment type is null");
            mediaType = null;
        } else {
            mediaType = MediaType.parse(type);
        }
        RequestBody attachmentPart = RequestBody.create(mediaType, attachmentFile);
        MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", attachmentFile.getName(), attachmentPart);

        Toast.makeText(activity, getString(R.string.toast_attachment_upload_start), Toast.LENGTH_SHORT).show();
        mainModel.uploadAttachment(multipartAttachment, currentMessageId);
    }

    private void sendMessageToDraft() {
        String fromEmail = spinnerFrom.getSelectedItem().toString();
        MailboxEntity fromMailbox = CTemplarApp.getAppDatabase().mailboxDao().getByEmail(fromEmail);
        long mailboxId = fromMailbox.id;
        String mailboxEmail = fromMailbox.email;

        String toEmail = toEmailTextView.getText().toString().trim();
        String subject = subjectEditText.getText().toString();
        String compose = Html.toHtml(composeEditText.getText());

        SendMessageRequest messageRequestToDraft = new SendMessageRequest();
        messageRequestToDraft.setSubject(subject);
        messageRequestToDraft.setSender(mailboxEmail);
        messageRequestToDraft.setContent(compose);
        messageRequestToDraft.setFolder(MainFolderNames.DRAFT);
        messageRequestToDraft.setIsEncrypted(true);
        messageRequestToDraft.setSend(false);
        messageRequestToDraft.setMailbox(mailboxId);
        messageRequestToDraft.setSubjectEncrypted(isSubjectEncrypted);

        List<MessageAttachment> attachments = messageSendAttachmentAdapter.getAttachmentsList();
        if (attachments != null && !attachments.isEmpty()) {
            messageRequestToDraft.setAttachments(attachments);
        }

        List<String> toEmailList = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            toEmailList = EditTextUtils.getListFromString(toEmail);
        }
        messageRequestToDraft.setReceivers(toEmailList);

        String ccEmail = ccTextView.getText().toString().trim();
        List<String> ccEmailList = new ArrayList<>();
        if (!ccEmail.isEmpty()) {
            ccEmailList = EditTextUtils.getListFromString(ccEmail);
        }
        messageRequestToDraft.setCc(ccEmailList);

        String bccEmail = bccTextView.getText().toString().trim();
        List<String> bccEmailList = new ArrayList<>();
        if (!bccEmail.isEmpty()) {
            bccEmailList = EditTextUtils.getListFromString(bccEmail);
        }
        messageRequestToDraft.setBcc(bccEmailList);

        Toast.makeText(getActivity(), getString(R.string.toast_message_saved_as_draft), Toast.LENGTH_SHORT).show();
        mainModel.updateMessage(currentMessageId, messageRequestToDraft, new ArrayList<String>(), mailboxId);
    }

    private void addSignature(String signatureText) {
        boolean signatureEnabled = sharedPreferences.getBoolean(getString(R.string.signature_enabled), false);
        if (signatureEnabled) {
            String compose = composeEditText.getText().toString();
            String text = getString(R.string.txt_user_signature, signatureText, compose);
            composeEditText.setText(text);
        }
    }

    private void addListeners() {
        sendMessage.setEnabled(false);
        toEmailTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean state = inputFieldsNotEmpty();
                sendMessage.setEnabled(state);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ccTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean state = inputFieldsNotEmpty();
                sendMessage.setEnabled(state);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bccTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean state = inputFieldsNotEmpty();
                sendMessage.setEnabled(state);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        subjectEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean state = inputFieldsNotEmpty();
                sendMessage.setEnabled(state);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        composeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean state = inputFieldsNotEmpty();
                sendMessage.setEnabled(state);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (draftMessage) {
            sendMessageToDraft();
        }
    }

    private void finish() {
        finished = true;
        Activity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
        }
    }
}
