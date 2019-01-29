package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
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

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CreateAttachmentResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.FileUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.PermissionCheck;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SendMessageActivity extends BaseActivity {

    private final int PICK_FILE_FROM_STORAGE = 1;
    public final static String PARENT_ID = "parent_id";
    private Long parentId;
    private long currentMessageId;
    private ProgressDialog sendingProgress;
    private SharedPreferences sharedPreferences;
    private SendMessageActivityViewModel mainModel;
    private MessageSendAttachmentAdapter messageSendAttachmentAdapter;

    @BindView(R.id.fragment_send_message_to_input)
    AutoCompleteTextView toEmailTextView;

    @BindView(R.id.fragment_send_message_subject_input)
    EditText subjectEditText;

    @BindView(R.id.fragment_send_message_compose_email_input)
    EditText composeEditText;

    @BindView(R.id.fragment_send_message_from_list_view)
    ListView fromListView;

    @BindView(R.id.fragment_send_message_from_input_spinner)
    Spinner spinnerFrom;

    @BindView(R.id.fragment_send_message_cc_layout)
    RelativeLayout ccLayout;

    @BindView(R.id.fragment_send_message_bcc_layout)
    RelativeLayout bccLayout;

    @BindView(R.id.fragment_send_message_cc_input)
    AutoCompleteTextView ccTextView;

    @BindView(R.id.fragment_send_message_bcc_input)
    AutoCompleteTextView bccTextView;

    @BindView(R.id.fragment_send_message_to_add_button)
    ImageView toAddIco;

    @BindView(R.id.fragment_send_message_send)
    ImageView sendMessage;

    @BindView(R.id.fragment_send_message_attachments)
    RecyclerView messageAttachmentsRecycleView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_send_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            String[] bundleEmails = args.getStringArray(Intent.EXTRA_EMAIL);
            String[] bundleCC = args.getStringArray(Intent.EXTRA_CC);
            String[] bundleBCC = args.getStringArray(Intent.EXTRA_BCC);
            String bundleSubject = args.getString(Intent.EXTRA_SUBJECT);
            String bundleText = args.getString(Intent.EXTRA_TEXT);
            parentId = args.getLong(PARENT_ID, -1);
            if (parentId == -1) {
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

        boolean mobileSignatureEnabled = sharedPreferences.getBoolean(getString(R.string.mobile_signature_enabled), false);
        String mobileSignature = sharedPreferences.getString(getString(R.string.mobile_signature), null);
        if(mobileSignatureEnabled) {
            composeEditText.setText("\n\n--------\n" + mobileSignature + '\n' + composeEditText.getText());
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
                this,
                R.layout.fragment_send_message_spinner,
                emails
        );
        spinnerFrom.setAdapter(adapter);

        messageSendAttachmentAdapter = new MessageSendAttachmentAdapter();
        messageAttachmentsRecycleView.setAdapter(messageSendAttachmentAdapter);

        mainModel.getContactsResponse().observe(this, new Observer<ContactsResponse>() {
            @Override
            public void onChanged(@Nullable ContactsResponse contactsResponse) {
                handleContactsList(contactsResponse);
            }
        });
        mainModel.getContacts(20, 0);

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
                    Toast.makeText(SendMessageActivity.this, getResources().getString(R.string.toast_message_not_sent), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainModel.getMessagesResult()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult == null) {
                            Toast.makeText(SendMessageActivity.this, getResources().getString(R.string.toast_message_not_sent), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_message_not_created), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        mainModel.uploadAttachmentStatus
                .observe(this, new Observer<ResponseStatus>() {
                    @Override
                    public void onChanged(@Nullable ResponseStatus responseStatus) {
                        if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_attachment_upload_complete) , Toast.LENGTH_SHORT).show();
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

        addListeners();
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

    private void handleContactsList(@Nullable ContactsResponse contactsResponse) {
        if (contactsResponse == null || contactsResponse.getResults() == null || contactsResponse.getResults().length == 0) {
            // empty list
            return;
        }

        ContactData[] contacts = contactsResponse.getResults();
        List<ContactData> contactsList = new LinkedList<>();
        contactsList.addAll(Arrays.asList(contacts));
        RecipientsListAdapter recipientsAdapter = new RecipientsListAdapter(SendMessageActivity.this, R.layout.recipients_list_view_item, contactsList);
        toEmailTextView.setAdapter(recipientsAdapter);
    }

    @OnClick(R.id.fragment_send_message_send)
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

        sendingProgress = new ProgressDialog(this);
        sendingProgress.setCanceledOnTouchOutside(false);
        sendingProgress.setMessage(getResources().getString(R.string.txt_sending_mail));
        sendingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sendingProgress.show();

        ArrayList<String> emails = new ArrayList<>();
        emails.add(toEmail);
        PublicKeysRequest publicKeysRequest = new PublicKeysRequest(emails);
        mainModel.getEmailPublicKeys(publicKeysRequest);
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

        if (!emails.isEmpty()) {
            messageRequestToDraft.setReceivers(emails);
        }

        mainModel.updateMessage(currentMessageId, messageRequestToDraft, new ArrayList<String>());
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

    private void createMessage() {
        MailboxEntity defaultMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_FROM_STORAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri attachmentUri = data.getData();
            uploadAttachment(attachmentUri);
        }
    }

    private void uploadAttachment(Uri attachmentUri) {
        String attachmentPath = FileUtils.getPath(this, attachmentUri);

        File attachmentFile = new File(attachmentPath);
        RequestBody attachmentPart = RequestBody.create(MediaType.parse(getContentResolver().getType(attachmentUri)), attachmentFile);
        MultipartBody.Part multipartAttachment = MultipartBody.Part.createFormData("document", attachmentFile.getName(), attachmentPart);

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_attachment_upload_start) , Toast.LENGTH_SHORT).show();

        mainModel.uploadAttachment(multipartAttachment, currentMessageId);
    }

    private boolean onHandleBackPressed() {
        new AlertDialog.Builder(SendMessageActivity.this)
                .setTitle(getResources().getString(R.string.dialog_discard_mail))
                .setMessage(getResources().getString(R.string.dialog_discard_confirm))
                .setPositiveButton(getResources().getString(R.string.dialog_save_in_drafts), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMessageToDraft();
                                finish();
                            }
                        }
                )
                .setNegativeButton(getResources().getString(R.string.action_discard), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainModel.deleteMessage(currentMessageId);
                                finish();
                            }
                        }
                )
                .setNeutralButton(getResources().getString(R.string.action_cancel), null)
                .show();

        return false;
    }

    private boolean inputFieldsNotEmpty() {
        String toEmail = toEmailTextView.getText().toString();
        String subject = subjectEditText.getText().toString();
        String compose = composeEditText.getText().toString();

        return (!toEmail.isEmpty() && !subject.isEmpty() && !compose.isEmpty());
    }

    @OnClick(R.id.fragment_send_message_attachment_layout)
    public void onClickAttachment() {
        if (PermissionCheck.readAndWriteExternalStorage(this)) {
            Intent chooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            chooseIntent.setType("*/*");
            startActivityForResult(chooseIntent, PICK_FILE_FROM_STORAGE);
        }
    }

    @OnClick(R.id.fragment_send_message_to_add_button)
    public void onClick() {
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

    @Override
    public void onBackPressed() {
        if (onHandleBackPressed()) {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.fragment_send_message_back)
    public void onClickBack() {
        onBackPressed();
    }
}
