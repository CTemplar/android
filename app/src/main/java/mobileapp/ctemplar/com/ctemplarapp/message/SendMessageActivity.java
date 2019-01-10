package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.text.Html;
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
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class SendMessageActivity extends BaseActivity {

    private SendMessageActivityViewModel mainModel;
    public final static String PARENT_ID = "parent_id";
    private Long parentId;
    private ProgressDialog sendingProgress;
    private boolean needExit = false;
    private SharedPreferences sharedPreferences;

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
                    KeyResult[] keyResult = keyResponse.getKeyResult();

                    ArrayList<String> emails = new ArrayList<>();
                    ArrayList<String> publicKeys = new ArrayList<>();
                    for (KeyResult key : keyResult) {
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
                    Toast.makeText(SendMessageActivity.this, "Message not sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainModel.getMessagesResult()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult == null) {
                            Toast.makeText(SendMessageActivity.this, "Not sent", Toast.LENGTH_SHORT).show();
                        } else {
                            onBackPressed();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (onHandleBackPressed()) {
            super.onBackPressed();
        }
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
        String compose = composeEditText.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(toEmail).matches()) {
            toEmailTextView.setError(null);
        } else {
            toEmailTextView.setError("Enter valid email");
            return;
        }

        if (compose.length() < 1) {
            composeEditText.setError("Enter message");
            return;
        }

        sendingProgress = new ProgressDialog(SendMessageActivity.this);
        sendingProgress.setCanceledOnTouchOutside(false);
        sendingProgress.setMessage("Sending mail...");
        sendingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sendingProgress.show();

        ArrayList<String> emails = new ArrayList<>();
        emails.add(toEmail);
        PublicKeysRequest publicKeysRequest = new PublicKeysRequest(emails);
        mainModel.getEmailPublicKeys(publicKeysRequest);
    }

    private void sendMessageToDraft() {
        String toEmail = toEmailTextView.getText().toString();
        String subject = subjectEditText.getText().toString();
        String compose = Html.toHtml(composeEditText.getText());

        ArrayList<String> emails = new ArrayList<>();
        if (!toEmail.isEmpty()) {
            emails.add(toEmail);
        }

        SendMessageRequest messageRequest = new SendMessageRequest(
                subject,
                compose,
                "draft",
                false,
                true,
                CTemplarApp.getAppDatabase().mailboxDao().getDefault().id, //TODO
                parentId
        );

        if (!emails.isEmpty()) {
            messageRequest.setReceivers(emails);
        }

        mainModel.sendMessage(messageRequest, new ArrayList<String>());
    }

    private void sendMessage(ArrayList<String> emails, ArrayList<String> publicKeys) {
        String subject = subjectEditText.getText().toString();
        String compose = Html.toHtml(composeEditText.getText());

        SendMessageRequest messageRequest = new SendMessageRequest(
                subject,
                compose,
                "sent",
                true,
                true,
                CTemplarApp.getAppDatabase().mailboxDao().getDefault().id, //TODO
                parentId
        );

        if (!emails.isEmpty()) {
            messageRequest.setReceivers(emails);
        }

        needExit = true;
        mainModel.sendMessage(messageRequest, publicKeys);
        if (sendingProgress != null){
            sendingProgress.dismiss();
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

    @OnClick(R.id.fragment_send_message_back)
    public void onClickBack() {
        onBackPressed();
    }

    public boolean onHandleBackPressed() {
        if (needExit) {
            return true;
        }

        new AlertDialog.Builder(SendMessageActivity.this)
                .setTitle("Confirm Discard")
                .setMessage("Are you sure, you want to discard this email?")
                .setPositiveButton("Save in drafts", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                needExit = true;
                                sendMessageToDraft();
                                SendMessageActivity.this.onBackPressed();
                            }
                        }
                )
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                needExit = true;
                                SendMessageActivity.this.onBackPressed();
                            }
                        }
                )
                .setNeutralButton("Cancel", null)
                .show();
        return needExit;
    }
}
