package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;

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

    private FilterDialogFragment.OnApplyClickListener onFilterApplyClickListener
            = new FilterDialogFragment.OnApplyClickListener() {
        @Override
        public void onApply(boolean isStarred, boolean isUnread, boolean withAttachment) {
            adapter.filter(isStarred, isUnread, withAttachment);
        }
    };

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
        setHasOptionsMenu(true);
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
                String currentFolder = mainModel.getCurrentFolder().getValue();
                if (currentFolder.equals("starred")) {
                    mainModel.getStarredMessages(20, 0, 1);
                } else {
                    mainModel.getMessages(20, 0, currentFolder);
                }
            }
        });

//        if(mainModel.getMessagesResponse() == null || mainModel.getMessagesResponse().getValue() == null) {
//            mainModel.showProgressDialog();
//            mainModel.getMessages(20, 0, mainModel.getCurrentFolder().getValue());
//        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        String currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder != null) {
            if (currentFolder.equals("starred")) {
                mainModel.getStarredMessages(20, 0, 1);
            } else {
                mainModel.getMessages(20, 0, currentFolder);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

//        MenuItem searchItem = menu.getItem(R.id.action_search);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = null;
//        if(searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//        }
//        if(searchView != null) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            FilterDialogFragment dialogFragment = new FilterDialogFragment();
            dialogFragment.setOnApplyClickListener(onFilterApplyClickListener);
            dialogFragment.show(getFragmentManager(), null);
            return true;
        }

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.fragment_inbox_send_layout)
    public void onClickComposeLayout() {
        Intent intent = new Intent(getActivity(), SendMessageActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.fragment_inbox_send)
    public void onClickCompose() {
        Intent intent = new Intent(getActivity(), SendMessageActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fragment_inbox_fab_compose)
    public void onClickFabCompose() {
        Intent intent = new Intent(getActivity(), SendMessageActivity.class);
        startActivity(intent);
    }

    public void handleResponseStatus(ResponseStatus status) {
        mainModel.hideProgressDialog();
        if(status != null) {
            switch(status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                    break;
                case RESPONSE_NEXT_MESSAGES:
                    // adapter = new InboxMessagesAdapter(mainModel.getMessagesResponse().getValue().getMessagesList());
                    // recyclerView.setAdapter(adapter);
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

            adapter = new InboxMessagesAdapter(mainModel.getMessagesResponse().getValue().getMessagesList(), mainModel);
            adapter.getOnClickSubject()
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            Intent intent = new Intent(getActivity(), ViewMessageActivity.class);
                            intent.putExtra(ViewMessageActivity.ARG_ID, aLong);
                            getActivity().startActivity(intent);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            recyclerView.setAdapter(adapter);
        }
    }

}