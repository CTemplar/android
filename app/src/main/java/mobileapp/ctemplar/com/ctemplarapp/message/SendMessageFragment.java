package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.MotionEvent;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class SendMessageFragment extends BaseFragment {
    private SendMessageActivityViewModel mainModel;

//    @BindView(R.id.fragment_send_message_from_input)
//    EditText from;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String[] bundleEmails = args.getStringArray(Intent.EXTRA_EMAIL);
            String[] bundleCC = args.getStringArray(Intent.EXTRA_CC);
            String[] bundleBCC = args.getStringArray(Intent.EXTRA_BCC);
            String bundleSubject = args.getString(Intent.EXTRA_SUBJECT);
            String bundleText = args.getString(Intent.EXTRA_TEXT);

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

        String toEmail = toEmailTextView.getText().toString();
        if (toEmail.isEmpty()) {
            toEmailTextView.requestFocus();
        } else {
            composeEditText.requestFocus();
            composeEditText.setSelection(0);
        }

        mainModel = ViewModelProviders.of(getActivity()).get(SendMessageActivityViewModel.class);

        List<MailboxEntity> mailboxEntities = mainModel.getMailboxes();
        String[] emails = new String[mailboxEntities.size()];
        for (int i = 0; i < mailboxEntities.size(); i++) {
            emails[i] = mailboxEntities.get(i).email;
        }

        SpinnerAdapter adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.fragment_send_message_spinner,
                emails
        );
        spinnerFrom.setAdapter(adapter);

        mainModel = ViewModelProviders.of(getActivity()).get(SendMessageActivityViewModel.class);
        mainModel.getContactsResponse().observe(this, new Observer<ContactsResponse>() {
            @Override
            public void onChanged(@Nullable ContactsResponse contactsResponse) {
                handleContactsList(contactsResponse);
            }
        });
        mainModel.getContacts(20, 0);
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

    @OnClick(R.id.fragment_send_message_send)
    public void onClickSend() {
        //String from = spinnerFrom
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

        Toast.makeText(getActivity(), "Sending mail...", Toast.LENGTH_SHORT).show();
        SendMessageRequest messageRequest = new SendMessageRequest(
                subjectEditText.getText().toString(),
                composeEditText.getText().toString(),
                "inbox",
                CTemplarApp.getAppDatabase().mailboxDao().getDefault().id // ToDo
        );

        if (!toEmail.isEmpty()) {
            List<String> receivers = new LinkedList<>();
            receivers.add(toEmail);
            messageRequest.setReceivers(receivers);
        }
        mainModel.sendMessage(messageRequest);
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
        getActivity().onBackPressed();
    }

}
