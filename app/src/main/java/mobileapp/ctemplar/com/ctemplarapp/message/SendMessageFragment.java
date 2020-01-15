package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.UpgradeToPrimeFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.AttachmentsEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.EncryptionMessage;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.AttachmentEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import mobileapp.ctemplar.com.ctemplarapp.utils.SpaceTokenizer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.ATTACHMENT_LIST;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.MESSAGE_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;

public class SendMessageFragment extends Fragment implements View.OnClickListener, ActivityInterface {
    private final static String TAG = SendMessageFragment.class.getSimpleName();
    private final static int PICK_FILE_FROM_STORAGE = 1;

    private boolean finished;

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

    private SharedPreferences sharedPreferences;
    private SendMessageActivityViewModel sendModel;
    private ProgressDialog sendingProgress;
    private ProgressDialog uploadProgress;

    // COMPOSE OPTIONS
    private long currentMessageId = -1;
    private Long parentId;
    private Long delayedDeliveryInMillis;
    private Long destructDeliveryInMillis;
    private Long deadDeliveryInHours;
    private EncryptionMessage messageEncryptionResult;
    private boolean userIsPrime;
    private boolean isSubjectEncrypted;
    private boolean attachmentsProcessingEnabled;

    private SendMessageRequest sendMessageRequest;
    private List<String> publicKeyList;
    private List<String> mailboxAddresses = new ArrayList<>();
    private List<File> cacheFileList = new ArrayList<>();
    private boolean draftMessage = true;
    private int updateAttachmentPosition = 0;

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
            if (getActivity() == null) {
                return;
            }
            destructDeliveryInMillis = timeInMilliseconds;
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
            if (getActivity() == null) {
                return;
            }
            deadDeliveryInHours = timeInHours;
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
            long mailboxId = defaultMailbox.id;

            SendMessageRequest setEncryptionRequest = new SendMessageRequest();
            setEncryptionRequest.setMailbox(mailboxId);
            setEncryptionRequest.setEncryptionMessage(encryptionMessage);

            sendModel.setEncryptionMessage(currentMessageId, setEncryptionRequest);
        }
    };

    private List<AttachmentProvider> forwardedAttachments;


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
        messageAttachmentsProcessingTextView = root.findViewById(R.id.fragment_send_message_attachments_processing_text_view);
        messageAttachmentsRecycleView = root.findViewById(R.id.fragment_send_message_attachments);
        sendMessageDestructIco = root.findViewById(R.id.fragment_send_message_destruct_ico);
        sendMessageDelayedIco = root.findViewById(R.id.fragment_send_message_delayed_ico);
        sendMessageDeadIco = root.findViewById(R.id.fragment_send_message_dead_ico);
        sendMessageAttachmentIco = root.findViewById(R.id.fragment_send_message_attachment_ico);
        sendMessageEncryptIco = root.findViewById(R.id.fragment_send_message_encrypt_ico);

        composeEditText.setPaintFlags(composeEditText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));

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
            currentMessageId = args.getLong(MESSAGE_ID, -1);
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

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


        sendModel = ViewModelProviders.of(this).get(SendMessageActivityViewModel.class);
        if (currentMessageId == -1) {
            createMessage();
        } else {
            sendModel.openMessage(currentMessageId);
        }

        int selectedAddress = 0;
        List<MailboxEntity> mailboxEntities = sendModel.getMailboxes();
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

        messageSendAttachmentAdapter = new MessageSendAttachmentAdapter(activity);
        messageAttachmentsRecycleView.setAdapter(messageSendAttachmentAdapter);

        initResponses();
        addListeners();

        return root;
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

    @Override
    public void onPause() {
        super.onPause();
        if (draftMessage) {
            sendMessageToDraft();
        }
    }

    @Override
    public boolean onBackPressed() {
        return finished || onHandleBackPressed();
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

        if (attachmentsProcessingEnabled) {
            Toast.makeText(getActivity(), getString(R.string.txt_attachments_in_processing), Toast.LENGTH_SHORT).show();
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
        sendModel.getContactsResponse().observe(this, contactList -> {
            if (contactList != null) {
                handleContactsList(contactList);
            }
        });
        sendModel.getContacts(200, 0);

        // Load keys before sending message
        sendModel.getKeyResponse().observe(this, keyResponse -> {
            if (keyResponse != null && keyResponse.getKeyResult() != null && keyResponse.getKeyResult().length > 0) {
                publicKeyList = new ArrayList<>();
                for (KeyResult key : keyResponse.getKeyResult()) {
                    String emailPublicKey = key.getPublicKey();
                    publicKeyList.add(emailPublicKey);
                }
                sendMessage();
            }
        });

        // checking for attachment updates when sending
        sendModel.getUpdateAttachmentStatus().observe(this, responseStatus -> {
            if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                List<MessageAttachment> attachmentList = messageSendAttachmentAdapter.getAttachmentList();
                int attachmentListSize = attachmentList.size();
                if (updateAttachmentPosition < attachmentListSize) {
                    new Thread(() -> {
                        updateAttachments();
                        updateAttachmentPosition++;
                    }).start();
                    return;
                } else {
                    sendModel.updateMessage(currentMessageId, sendMessageRequest, publicKeyList);
                }
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR_TOO_LARGE) {
                Toast.makeText(activity, getString(R.string.error_upload_attachment_too_large), Toast.LENGTH_SHORT).show();
            } else if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                Toast.makeText(activity, getString(R.string.error_upload_attachment), Toast.LENGTH_SHORT).show();
            }

            for (File cacheFile : cacheFileList) {
                cacheFile.delete();
            }
            if (sendingProgress != null) {
                sendingProgress.dismiss();
            }

        });

        sendModel.getGrabAttachmentStatus().observe(this, aBoolean -> {
            messageAttachmentsProcessingTextView.setVisibility(View.GONE);
            attachmentsProcessingEnabled = false;
        });

        sendModel.getMessagesResult()
                .observe(this, messagesResult -> {
                    if (sendingProgress != null && sendingProgress.isShowing()) {
                        sendingProgress.dismiss();
                    }
                    if (messagesResult == null) {
                        Toast.makeText(activity, getString(R.string.toast_message_not_sent), Toast.LENGTH_SHORT).show();
                    } else {
                        String folderName = messagesResult.getFolderName();
                        if (!folderName.equals(MainFolderNames.DRAFT)) {
                            finish();
                        }
                    }
                });

        sendModel.getCreateMessageStatus()
                .observe(this, responseStatus -> {
                    if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
                        Toast.makeText(activity, getResources().getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        sendModel.getCreateMessageResponse()
                .observe(this, messagesResult -> {
                    if (messagesResult != null) {
                        currentMessageId = messagesResult.getId();
                        grabForwardedAttachments();
                    } else {
                        Toast.makeText(activity, getResources().getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        sendModel.getOpenMessageResponse().observe(this, messageEntity -> {
            if (messageEntity != null) {
                loadMessageHandler(messageEntity);
            } else {
                Toast.makeText(activity, getString(R.string.toast_message_not_loaded), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        sendModel.getUploadAttachmentStatus()
                .observe(this, responseStatus -> {
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
                .observe(this, messageAttachment -> {
                    if (messageAttachment != null) {
                        messageSendAttachmentAdapter.addAttachment(messageAttachment);
                        if (messageSendAttachmentAdapter.getItemCount() > 0) {
                            sendMessageAttachmentIco.setSelected(true);
                            sendMessage.setEnabled(true);
                        }
                    }
                    if (!cacheFileList.isEmpty()) {
                        File cacheFile = cacheFileList.get(0);
                        cacheFile.delete();
                    }
                });

        sendModel.getDeleteAttachmentStatus()
                .observe(this, responseStatus -> {
                    if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                        int attachmentCount = messageSendAttachmentAdapter.getItemCount();
                        if (attachmentCount < 1) {
                            sendMessageAttachmentIco.setSelected(false);
                        }
                    }
                });

        sendModel.getMessageEncryptionResult()
                .observe(this, messagesResult -> {
                    if (messagesResult != null) {
                        messageEncryptionResult = messagesResult.getEncryption();
                        if (messageEncryptionResult != null) {
                            sendMessageEncryptIco.setSelected(true);
                        }
                    }
                });

        sendModel.getMySelfResponse()
                .observe(this, myselfResponse -> {
                    if (myselfResponse != null) {
                        MyselfResult myself = myselfResponse.result[0];
                        addSignature(myself.mailboxes[0].getSignature());
                        isSubjectEncrypted = myself.settings.isSubjectEncrypted();
                        userIsPrime = myself.isPrime;
//                        String joinedDate = myself.joinedDate;
//                        boolean userTrial = AppUtils.twoWeeksTrial(joinedDate);
//                        boolean userPrime = myself.isPrime;
//                        userIsPrime = userPrime || userTrial;
                    }
                });
    }

    private void createMessage() {
        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
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

        sendModel.createMessage(createMessageRequest);
        sendModel.mySelfData();
    }

    private void loadMessageHandler(MessageEntity messageEntity) {
        String messageSender = messageEntity.getSender();
        List<String> messageReceivers = messageEntity.getReceivers();
        List<String> messageCc = messageEntity.getCc();
        List<String> messageBcc = messageEntity.getBcc();
        String messageSubject = messageEntity.getSubject();
        String messageContent = messageEntity.getContent();
        String messageDestruct = messageEntity.getDestructDate();
        String messageDelayed = messageEntity.getDelayedDelivery();
        String messageDeadMan = messageEntity.getDeadManDuration();
        List<AttachmentEntity> messageAttachmentList = messageEntity.getAttachments();

        if (messageSender != null && !messageSender.isEmpty()) {
            int senderPosition = mailboxAddresses.indexOf(messageSender);
            spinnerFrom.setSelection(senderPosition);
        }
        if (messageReceivers != null && messageReceivers.size() > 0) {
            toEmailTextView.setText(EditTextUtils.getStringFromList(messageReceivers));
        }
        if (messageCc != null && messageCc.size() > 0) {
            ccLayout.setVisibility(View.VISIBLE);
            ccTextView.setText(EditTextUtils.getStringFromList(messageCc));
        }
        if (messageBcc != null && messageBcc.size() > 0) {
            bccLayout.setVisibility(View.VISIBLE);
            bccTextView.setText(EditTextUtils.getStringFromList(messageBcc));
        }
        if (messageSubject != null && !messageSubject.isEmpty()) {
            subjectEditText.setText(messageSubject);
        }
        if (messageContent != null && !messageContent.isEmpty()) {
            Spanned messageSpanned = Html.fromHtml(messageContent);
            composeEditText.setText(messageSpanned);
        }
        if (messageDestruct != null && !messageDestruct.isEmpty()) {
            sendMessageDestructIco.setSelected(true);
            destructDeliveryInMillis = AppUtils.millisFromServer(messageDestruct);
        }
        if (messageDelayed != null && !messageDelayed.isEmpty()) {
            sendMessageDelayedIco.setSelected(true);
            delayedDeliveryInMillis = AppUtils.millisFromServer(messageDelayed);
        }
        if (messageDeadMan != null && !messageDeadMan.isEmpty()) {
            sendMessageDeadIco.setSelected(true);
            deadDeliveryInHours = Long.parseLong(messageDeadMan);
        }
        if (messageAttachmentList != null) {
            for (AttachmentEntity attachmentEntity : messageAttachmentList) {
                MessageAttachment messageAttachment = new MessageAttachment();
                messageAttachment.setId(attachmentEntity.getId());
                messageAttachment.setMessage(attachmentEntity.getMessage());
                messageAttachment.setContentId(attachmentEntity.getContentId());
                messageAttachment.setDocumentLink(attachmentEntity.getDocumentLink());
                messageSendAttachmentAdapter.addAttachment(messageAttachment);
            }
        }
        if (messageSendAttachmentAdapter.getItemCount() > 0) {
            sendMessageAttachmentIco.setSelected(true);
        }
    }

    private void handleContactsList(List<Contact> contactList) {
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
        String fromEmail = spinnerFrom.getSelectedItem().toString();
        MailboxEntity fromMailbox = CTemplarApp.getAppDatabase().mailboxDao().getByEmail(fromEmail);
        final long mailboxId = fromMailbox.id;
        String mailboxEmail = fromMailbox.email;

        String subject = subjectEditText.getText().toString();
        String compose = composeEditText.getText().toString();
        Spannable composeSpannable = new SpannableString(compose);

        sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setSender(mailboxEmail);
        sendMessageRequest.setSubject(subject);
        sendMessageRequest.setHtml(true);
        sendMessageRequest.setContent(Html.toHtml(composeSpannable));
        sendMessageRequest.setMailbox(mailboxId);
        sendMessageRequest.setParent(parentId);
        sendMessageRequest.setSubjectEncrypted(isSubjectEncrypted);

        draftMessage = false;
        boolean messageSent = true;
        String messageFolder = SENT;

        if (destructDeliveryInMillis != null) {
            sendMessageRequest.setDestructDate(AppUtils.datetimeForServer(destructDeliveryInMillis));
        }
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

        List<MessageAttachment> attachments = messageSendAttachmentAdapter.getAttachmentList();
        if (attachments != null && !attachments.isEmpty()) {
            sendMessageRequest.setAttachments(attachments);
        }

        new Thread(() -> {
            if (messageEncryptionResult != null) {
                String randomSecret = messageEncryptionResult.getRandomSecret();
                String password = messageEncryptionResult.getPassword();

                PGPKeyEntity pgpKeyEntity = PGPManager.generateKeys(randomSecret, password);
                messageEncryptionResult.setPublicKey(pgpKeyEntity.getPublicKey());
                messageEncryptionResult.setPrivateKey(pgpKeyEntity.getPrivateKey());

                publicKeyList.add(pgpKeyEntity.getPublicKey());
                sendMessageRequest.setEncryptionMessage(messageEncryptionResult);
            }

            MailboxEntity mailboxEntity = sendModel.getMailboxById(mailboxId);
            String senderPublicKey = mailboxEntity.getPublicKey();
            publicKeyList.add(senderPublicKey);

            if (publicKeyList.contains(null) && messageEncryptionResult == null) {
                publicKeyList.clear();
            } else if (publicKeyList.contains(null)) {
                publicKeyList.removeAll(Collections.singleton(null));
            }

            int attachmentCount = messageSendAttachmentAdapter.getItemCount();
            if (attachmentCount > 0) {
                boolean needUpdate = updateAttachments();
                if (needUpdate) {
                    return;
                }
            }
            sendModel.updateMessage(currentMessageId, sendMessageRequest, publicKeyList);
        }).start();
    }

    private void sendMessageToDraft() {
        String fromEmail = spinnerFrom.getSelectedItem().toString();
        MailboxEntity fromMailbox = CTemplarApp.getAppDatabase().mailboxDao().getByEmail(fromEmail);
        final long mailboxId = fromMailbox.id;
        String mailboxEmail = fromMailbox.email;

        String toEmail = toEmailTextView.getText().toString().trim();
        String subject = subjectEditText.getText().toString();
        String compose = composeEditText.getText().toString();
        Spannable composeSpannable = new SpannableString(compose);

        updateAttachmentPosition = 0;
        final SendMessageRequest messageRequestToDraft = new SendMessageRequest();
        messageRequestToDraft.setSubject(subject);
        messageRequestToDraft.setSender(mailboxEmail);
        messageRequestToDraft.setContent(Html.toHtml(composeSpannable));
        messageRequestToDraft.setFolder(MainFolderNames.DRAFT);
        messageRequestToDraft.setIsEncrypted(true);
        messageRequestToDraft.setHtml(true);
        messageRequestToDraft.setSend(false);
        messageRequestToDraft.setMailbox(mailboxId);
        messageRequestToDraft.setSubjectEncrypted(isSubjectEncrypted);

        if (destructDeliveryInMillis != null) {
            messageRequestToDraft.setDestructDate(AppUtils.datetimeForServer(destructDeliveryInMillis));
        }
        if (delayedDeliveryInMillis != null) {
            messageRequestToDraft.setDelayedDelivery(AppUtils.datetimeForServer(delayedDeliveryInMillis));
        }
        if (deadDeliveryInHours != null) {
            messageRequestToDraft.setDeadManDuration(deadDeliveryInHours);
        }

        List<MessageAttachment> attachments = messageSendAttachmentAdapter.getAttachmentList();
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

        MailboxEntity mailboxEntity = sendModel.getMailboxById(mailboxId);
        String senderPublicKey = mailboxEntity.getPublicKey();
        List<String> publicKeyList = Collections.singletonList(senderPublicKey);

        sendModel.updateMessage(currentMessageId, messageRequestToDraft, publicKeyList);
        Toast.makeText(getActivity(), getString(R.string.toast_message_saved_as_draft), Toast.LENGTH_SHORT).show();
    }

    private boolean updateAttachments() {
        if (getActivity() == null) {
            return false;
        }
        boolean isEncryptionEnabled = sendModel.getAttachmentsEncryptionEnabled();

        List<MessageAttachment> attachmentList = messageSendAttachmentAdapter.getAttachmentList();
        if (updateAttachmentPosition >= attachmentList.size()) {
            return false;
        }
        MessageAttachment messageAttachment = attachmentList.get(updateAttachmentPosition);

        boolean attachmentIsEncrypted = messageAttachment.isEncrypted();
        if (!(isEncryptionEnabled || attachmentList.get(0).isEncrypted())) {
            return false;
        }

        final long id = messageAttachment.getId();
        String documentLink = messageAttachment.getDocumentLink();
        String fileName = AppUtils.getFileNameFromURL(documentLink);
        String type = AppUtils.getMimeType(documentLink);
        if (type == null) {
            return false;
        }
        MediaType mediaType = MediaType.parse(type);

        try {
            File cacheDir = getActivity().getCacheDir();
            File downloadedFile = File.createTempFile("attachment", ".ext", cacheDir);
            File encryptedFile = File.createTempFile("attachment", ".ext", cacheDir);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new URL(documentLink).openStream());

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(downloadedFile));
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1) {
                bufferedOutputStream.write(dataBuffer, 0, bytesRead);
                bufferedOutputStream.flush();
            }
            bufferedOutputStream.close();

            if (attachmentIsEncrypted) {
                MailboxEntity mailboxEntity = sendModel.getMailboxes().get(0);
                String privateKey = mailboxEntity.getPrivateKey();
                String password = sendModel.getUserPassword();
                EncryptUtils.decryptAttachment(downloadedFile, downloadedFile, password, privateKey);
            }

            RequestBody attachmentPart;
            if (isEncryptionEnabled && !publicKeyList.isEmpty()) {
                EncryptUtils.encryptAttachment(downloadedFile, encryptedFile, publicKeyList);
                downloadedFile.delete();
                cacheFileList.add(encryptedFile);
                attachmentPart = RequestBody.create(mediaType, encryptedFile);
            } else {
                attachmentPart = RequestBody.create(mediaType, downloadedFile);
                cacheFileList.add(downloadedFile);
            }

            final MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", fileName, attachmentPart);
            sendModel.updateAttachment(id, multipartAttachment, currentMessageId, isEncryptionEnabled);

        } catch (IOException e) {
            Timber.e(e);
        }

        return true;
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
            return;
        }

        String type = activity.getContentResolver().getType(attachmentUri);
        if (type == null) {
            return;
        }
        MediaType mediaType = MediaType.parse(type);

        boolean isEncryptionEnabled = sendModel.getAttachmentsEncryptionEnabled();
        MailboxEntity mailboxEntity = sendModel.getMailboxes().get(0);
        String mailboxPublicKey = mailboxEntity.getPublicKey();

        RequestBody attachmentPart;
        if (isEncryptionEnabled) {
            try {
                File cacheDir = getActivity().getCacheDir();
                File encryptedFile = File.createTempFile("attachment", ".ext", cacheDir);
                EncryptUtils.encryptAttachment(attachmentFile, encryptedFile, Collections.singletonList(mailboxPublicKey));
                attachmentPart = RequestBody.create(mediaType, encryptedFile);
                cacheFileList.add(encryptedFile);

            } catch (IOException e) {
                Timber.e(e);
                return;
            }
        } else {
            attachmentPart = RequestBody.create(mediaType, attachmentFile);
        }

        String attachmentName = attachmentFile.getName();
        MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", attachmentName, attachmentPart);

        sendModel.uploadAttachment(multipartAttachment, currentMessageId, isEncryptionEnabled);

        uploadProgress = new ProgressDialog(getActivity());
        uploadProgress.setCanceledOnTouchOutside(false);
        uploadProgress.setMessage(getResources().getString(R.string.txt_uploading));
        uploadProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        uploadProgress.show();
    }

    private boolean onHandleBackPressed() {
        final Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        new AlertDialog.Builder(activity)
                .setTitle(getResources().getString(R.string.dialog_discard_mail))
                .setMessage(getResources().getString(R.string.dialog_discard_confirm))
                .setPositiveButton(getResources().getString(R.string.dialog_save_in_drafts), (dialog, which) -> {
                    sendMessageToDraft();
                    dialog.dismiss();
                    finish();
                }
                )
                .setNegativeButton(getResources().getString(R.string.action_discard), (dialog, which) -> {
                    draftMessage = false;
                    sendModel.deleteMessage(currentMessageId);
                    dialog.dismiss();
                    finish();
                }
                )
                .setNeutralButton(getResources().getString(R.string.action_cancel), null)
                .show();

        return false;
    }

    private void addSignature(String signatureText) {
        boolean signatureEnabled = sharedPreferences.getBoolean(getString(R.string.signature_enabled), false);
        if (signatureEnabled) {
            String compose = composeEditText.getText().toString();
            String text = getString(R.string.txt_user_signature, signatureText, compose);
            composeEditText.setText(text);
        }
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

    private void upgradeToPrimeDialog() {
        if (getFragmentManager() != null) {
            UpgradeToPrimeFragment upgradeToPrimeFragment = new UpgradeToPrimeFragment();
            upgradeToPrimeFragment.show(getFragmentManager(), "UpgradeToPrimeFragment");
        }
    }

    private void addListeners() {
        sendMessage.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
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
        };
        toEmailTextView.addTextChangedListener(textWatcher);
        ccTextView.addTextChangedListener(textWatcher);
        bccTextView.addTextChangedListener(textWatcher);
        subjectEditText.addTextChangedListener(textWatcher);
        composeEditText.addTextChangedListener(textWatcher);
    }

    private void finish() {
        finished = true;
        Activity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
        }
    }

    private void grabForwardedAttachments() {
        if (forwardedAttachments != null) {
            sendModel.grabForwardedAttachments(forwardedAttachments, currentMessageId);
            messageAttachmentsProcessingTextView.setVisibility(View.VISIBLE);
            attachmentsProcessingEnabled = true;
        }
    }

    public static SendMessageFragment newInstance(
            @Nullable String subject,
            @Nullable String text,
            @Nullable String[] receivers,
            @Nullable String[] cc,
            @Nullable String[] bcc,
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
