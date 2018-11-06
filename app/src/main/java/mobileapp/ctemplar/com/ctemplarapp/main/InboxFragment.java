package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MessagesResponse;

public class InboxFragment extends BaseFragment {

    @BindView(R.id.fragment_inbox_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_inbox_icon_empty)
    ImageView imgEmpty;

    @BindView(R.id.fragment_inbox_title_empty)
    TextView txtEmpty;

    @BindView(R.id.fragment_inbox_send_layout)
    FrameLayout frameCompose;

    @BindView(R.id.fragment_inbox_fab_compose)
    FloatingActionButton fabCompose;

    private InboxMessagesAdapter adapter;

    private MainActivityViewModel mainModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_inbox;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mainModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus status) {
                handleResponseStatus(status);
            }
        });

        mainModel.getMessagesResponse().observe(this, new Observer<MessagesResponse>() {
            @Override
            public void onChanged(@Nullable MessagesResponse messagesResponse) {
                handleMessagesList(messagesResponse);
            }
        });

        mainModel.getCurrentFolder().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mainModel.getMessages(20, 0, mainModel.getCurrentFolder().getValue());
            }
        });

        if(mainModel.getMessagesResponse() == null || mainModel.getMessagesResponse().getValue() == null) {
            mainModel.showProgressDialog();
            mainModel.getMessages(20, 0, mainModel.getCurrentFolder().getValue());
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnClick(R.id.fragment_inbox_send_layout)
    public void onClickComposeLayout() {
        Toast.makeText(getActivity(), "In progress", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.fragment_inbox_send)
    public void onClickCompose() {
        Toast.makeText(getActivity(), "In progress", Toast.LENGTH_SHORT).show();
    }

    public void handleResponseStatus(ResponseStatus status) {
        mainModel.hideProgressDialog();
        if(status != null) {
            switch(status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                    break;
                case RESPONSE_NEXT_MESSAGES:
                    adapter = new InboxMessagesAdapter(mainModel.getMessagesResponse().getValue().getMessagesList());
                    recyclerView.setAdapter(adapter);
                    break;
            }
        }
    }

    public void handleMessagesList(MessagesResponse mailboxResponse) {
        if(mailboxResponse == null || mailboxResponse.getMessagesList() == null || mailboxResponse.getMessagesList().size() == 0) {
            recyclerView.setVisibility(View.GONE);
            fabCompose.hide();
            imgEmpty.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.VISIBLE);
            frameCompose.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            fabCompose.show();
            imgEmpty.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.GONE);
            frameCompose.setVisibility(View.GONE);
            // adapter = new InboxMessagesAdapter(mailboxResponse.getMessagesList());
        }
    }

}