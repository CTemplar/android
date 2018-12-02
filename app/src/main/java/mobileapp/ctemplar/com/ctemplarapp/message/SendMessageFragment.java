package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;

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
    RelativeLayout cc;

    @BindView(R.id.fragment_send_message_bcc_layout)
    RelativeLayout bcc;

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

        mainModel = ViewModelProviders.of(getActivity()).get(SendMessageActivityViewModel.class);
        SpinnerAdapter adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.fragment_send_message_spinner,
                new String[] { mainModel.userRepository.getUsername() + "@ctemplar.com" }
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
        String toEmail = toEmailTextView.getText().toString();
        String compose = composeEditText.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(toEmail).matches()) {
            toEmailTextView.setError(null);
        } else {
            toEmailTextView.setError("Enter valid email");
            return;
        }

        if (compose == null || compose.length() < 1) {
            composeEditText.setError("Enter message");
            return;
        }

        Toast.makeText(getActivity(), "Sending mail...", Toast.LENGTH_SHORT).show();
        SendMessageRequest messageRequest = new SendMessageRequest(
                subjectEditText.getText().toString(),
                composeEditText.getText().toString(),
                "inbox",
                196
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
        if (cc.getVisibility() == View.GONE) {
            toAddIco.setImageResource(R.drawable.ic_remove);
            cc.setVisibility(View.VISIBLE);
            bcc.setVisibility(View.VISIBLE);
        } else {
            toAddIco.setImageResource(R.drawable.ic_add_active);
            cc.setVisibility(View.GONE);
            bcc.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fragment_send_message_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

}
