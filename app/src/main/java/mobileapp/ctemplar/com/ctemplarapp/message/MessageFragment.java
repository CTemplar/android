package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;

public class MessageFragment extends BaseFragment {
    private MessageActivityViewModel mainModel;

    @BindView(R.id.fragment_send_message_from_input)
    EditText from;

    @BindView(R.id.fragment_send_message_to_input)
    EditText to;

    @BindView(R.id.fragment_send_message_subject_input)
    EditText subject;

    @BindView(R.id.fragment_send_message_compose_email_input)
    EditText content;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_send_message;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainModel = ViewModelProviders.of(getActivity()).get(MessageActivityViewModel.class);
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

    @OnClick(R.id.fragment_send_message_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

}
