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
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

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
        if (subject!= null) {
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
    private AutoCompleteTextView toEmailTextView;
    private AutoCompleteTextView ccTextView;
    private AutoCompleteTextView bccTextView;
    private ListView fromListView;
    private Spinner spinnerFrom;
    private RelativeLayout ccLayout;
    private RelativeLayout bccLayout;
    private ImageView toAddIco;
    private ImageView sendMessage;
    private RecyclerView messageAttachmentsRecycleView;

    private SharedPreferences sharedPreferences;
    private SendMessageActivityViewModel mainModel;
    private ProgressDialog sendingProgress;

    private long currentMessageId;
    private Long parentId;

    private MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    private MessageSendAttachmentAdapter messageSendAttachmentAdapter;

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
        fromListView = root.findViewById(R.id.fragment_send_message_from_list_view);
        spinnerFrom = root.findViewById(R.id.fragment_send_message_from_input_spinner);
        ccLayout = root.findViewById(R.id.fragment_send_message_cc_layout);
        bccLayout = root.findViewById(R.id.fragment_send_message_bcc_layout);
        toAddIco = root.findViewById(R.id.fragment_send_message_to_add_button);
        sendMessage = root.findViewById(R.id.fragment_send_message_send);
        messageAttachmentsRecycleView = root.findViewById(R.id.fragment_send_message_attachments);


        // OnClicks
        root.findViewById(R.id.fragment_send_message_send).setOnClickListener(this);
        root.findViewById(R.id.fragment_send_message_attachment_layout).setOnClickListener(this);
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
                toEmailTextView.setText(bundleEmails[0]);
            }
            if (bundleCC != null && bundleCC.length > 0) {
                ccLayout.setVisibility(View.VISIBLE);
                ccTextView.setText(bundleCC[0]);
            }
            if (bundleBCC != null && bundleBCC.length > 0) {
                bccLayout.setVisibility(View.VISIBLE);
                bccTextView.setText(bundleBCC[0]);
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
        String mobileSignature = sharedPreferences.getString(getString(R.string.mobile_signature), null);
        if(mobileSignatureEnabled) {
            String text = "\n\n--------\n" + mobileSignature + '\n' + composeEditText.getText();
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

        List<MailboxEntity> mailboxEntities = mainModel.getMailboxes();
        String[] emails = new String[mailboxEntities.size()];
        for (int i = 0; i < mailboxEntities.size(); i++) {
            emails[i] = mailboxEntities.get(i).email;
        }

        SpinnerAdapter adapter = new ArrayAdapter<>(
                activity,
                R.layout.fragment_send_message_spinner,
                emails
        );
        spinnerFrom.setAdapter(adapter);

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
                    ArrayList<String> emails = new ArrayList<>();
                    ArrayList<String> publicKeys = new ArrayList<>();

                    for (KeyResult key : keyResponse.getKeyResult()) {
                        emails.add(key.getEmail());
                        publicKeys.add(key.getPublicKey());
                    }
                    sendMessage(emails, publicKeys);
                }
            }
        });

        mainModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {

            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
                    if (sendingProgress != null){
                        sendingProgress.dismiss();
                    }
                    Toast.makeText(activity, getResources().getString(R.string.toast_message_not_sent), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainModel.getMessagesResult()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult == null) {
                            Toast.makeText(activity, getResources().getString(R.string.toast_message_not_sent), Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
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
                            Toast.makeText(activity, getResources().getString(R.string.toast_attachment_upload_complete) , Toast.LENGTH_SHORT).show();
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
                    }
                });

    }

    private void createMessage() {
        MailboxEntity defaultMailbox = mailboxDao.getDefault();
        long mailboxId = defaultMailbox.id;
        String mailboxEmail = defaultMailbox.email;

        SendMessageRequest createMessageRequest = new SendMessageRequest(
                mailboxEmail,
                "content",
                "draft",
                mailboxId
        );

        mainModel.createMessage(createMessageRequest);
    }

    private void handleContactsList(@Nullable ContactsResponse contactsResponse) {
        if (contactsResponse == null || contactsResponse.getResults() == null || contactsResponse.getResults().length == 0) {
            // empty list
            return;
        }

        ContactData[] contacts = contactsResponse.getResults();
        List<ContactData> contactsList = new LinkedList<>();
        contactsList.addAll(Arrays.asList(contacts));
        RecipientsListAdapter recipientsAdapter = new RecipientsListAdapter(getActivity(), R.layout.recipients_list_view_item, contactsList);
        toEmailTextView.setAdapter(recipientsAdapter);
    }

    private void sendMessage(ArrayList<String> emails, ArrayList<String> publicKeys) {
        MailboxEntity defaultMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
        long mailboxId = defaultMailbox.id;
        String mailboxEmail = defaultMailbox.email;

        String subject = subjectEditText.getText().toString();
        String compose = Html.toHtml(composeEditText.getText());

        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setSender(mailboxEmail);
        sendMessageRequest.setSubject(subject);
        sendMessageRequest.setContent(compose);
        sendMessageRequest.setFolder("sent");
        sendMessageRequest.setSend(true);
        sendMessageRequest.setMailbox(mailboxId);
        sendMessageRequest.setParent(parentId);

        if (!publicKeys.contains(null)) {
            sendMessageRequest.setIsEncrypted(true);
        }

        List<MessageAttachment> attachments = messageSendAttachmentAdapter.getAttachmentsList();
        if (attachments != null && !attachments.isEmpty()) {
            sendMessageRequest.setAttachments(attachments);
        }

        if (!emails.isEmpty()) {
            sendMessageRequest.setReceivers(emails);
        }

        mainModel.updateMessage(currentMessageId, sendMessageRequest, publicKeys);
        if (sendingProgress != null){
            sendingProgress.dismiss();
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

    private boolean inputFieldsNotEmpty() {
        String toEmail = toEmailTextView.getText().toString();
        String subject = subjectEditText.getText().toString();
        String compose = composeEditText.getText().toString();

        return (!toEmail.isEmpty() && !subject.isEmpty() && !compose.isEmpty());
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
        }
    }

    public void onClickSend() {
        String toEmail = toEmailTextView.getText().toString();
        String subject = subjectEditText.getText().toString();
        String compose = composeEditText.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(toEmail).matches()) {
            toEmailTextView.setError(null);
        } else {
            toEmailTextView.setError(getResources().getString(R.string.txt_enter_valid_email));
            return;
        }

        if (subject.length() < 1) {
            subjectEditText.setError(getResources().getString(R.string.hint_enter_subject));
            return;
        } else {
            subjectEditText.setError(null);
        }

        if (compose.length() < 1) {
            composeEditText.setError(getResources().getString(R.string.hint_enter_message));
            return;
        } else {
            composeEditText.setError(null);
        }

        sendingProgress = new ProgressDialog(getActivity());
        sendingProgress.setCanceledOnTouchOutside(false);
        sendingProgress.setMessage(getResources().getString(R.string.txt_sending_mail));
        sendingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sendingProgress.show();

        ArrayList<String> emails = new ArrayList<>();
        emails.add(toEmail);
        PublicKeysRequest publicKeysRequest = new PublicKeysRequest(emails);
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
            uploadAttachment(attachmentUri);
        }
    }


    private void uploadAttachment(Uri attachmentUri) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        String attachmentPath = FileUtils.getPath(activity, attachmentUri);

        File attachmentFile = new File(attachmentPath);
        MediaType mediaType;
        String type = activity.getContentResolver().getType(attachmentUri);
        if (type == null) {
            Timber.tag(TAG).wtf("Attachment type is null");
            mediaType = null;
        } else {
            mediaType = MediaType.parse(type);
        }
        RequestBody attachmentPart = RequestBody.create(mediaType, attachmentFile);
        MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", attachmentFile.getName(), attachmentPart);

        Toast.makeText(activity, getResources().getString(R.string.toast_attachment_upload_start) , Toast.LENGTH_SHORT).show();

        mainModel.uploadAttachment(multipartAttachment, currentMessageId);
    }

    private void sendMessageToDraft() {
        MailboxEntity defaultMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
        long mailboxId = defaultMailbox.id;
        String mailboxEmail = defaultMailbox.email;

        String toEmail = toEmailTextView.getText().toString();
        String subject = subjectEditText.getText().toString();
        String compose = Html.toHtml(composeEditText.getText());

        ArrayList<String> emails = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            emails.add(toEmail);
        }

        SendMessageRequest messageRequestToDraft = new SendMessageRequest();
        messageRequestToDraft.setSubject(subject);
        messageRequestToDraft.setSender(mailboxEmail);
        messageRequestToDraft.setContent(compose);
        messageRequestToDraft.setFolder("draft");
        messageRequestToDraft.setIsEncrypted(true);
        messageRequestToDraft.setSend(false);
        messageRequestToDraft.setMailbox(mailboxId);
        messageRequestToDraft.setParent(parentId);

        List<MessageAttachment> attachments = messageSendAttachmentAdapter.getAttachmentsList();
        if (attachments != null && !attachments.isEmpty()) {
            messageRequestToDraft.setAttachments(attachments);
        }

        if (!emails.isEmpty()) {
            messageRequestToDraft.setReceivers(emails);
        }

        mainModel.updateMessage(currentMessageId, messageRequestToDraft, new ArrayList<String>());
    }


    private void finish() {
        finished = true;
        Activity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
        }
    }

}