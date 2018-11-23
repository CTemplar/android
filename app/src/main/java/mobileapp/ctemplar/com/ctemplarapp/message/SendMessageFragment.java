package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;

public class SendMessageFragment extends BaseFragment {
    private SendMessageActivityViewModel mainModel;

//    @BindView(R.id.fragment_send_message_from_input)
//    EditText from;

    @BindView(R.id.fragment_send_message_to_input)
    AutoCompleteTextView to;

    @BindView(R.id.fragment_send_message_subject_input)
    EditText subject;

    @BindView(R.id.fragment_send_message_compose_email_input)
    EditText content;

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

        List<User> recipients = new LinkedList<>();
        recipients.add(new User("Andrew Smith", "andrewsmith@ctemplar.com"));
        recipients.add(new User("Andrew Black", "andrewblack@ctemplar.com"));
        recipients.add(new User("Anna Brown", "annabrown@ctemplar.com"));
        RecipientsListAdapter recipientsAdapter = new RecipientsListAdapter(getActivity(), R.layout.recipients_list_view_item, recipients);
        to.setAdapter(recipientsAdapter);
    }

    @OnClick(R.id.fragment_send_message_send)
    public void onClickSend() {
        Toast.makeText(getActivity(), "Sending mail...", Toast.LENGTH_SHORT).show();
        SendMessageRequest messageRequest = new SendMessageRequest(
                subject.getText().toString(),
                content.getText().toString(),
                "inbox",
                196
        );
        String receiver = to.getText().toString();
        if (!receiver.isEmpty()) {
            List<String> receivers = new LinkedList<>();
            receivers.add(receiver);
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
