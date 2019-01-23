package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import timber.log.Timber;

public class InboxFragment extends BaseFragment {

    private InboxMessagesAdapter adapter;
    private MainActivityViewModel mainModel;
    private InboxMessagesTouchListener touchListener;
    private String currentFolder;

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

        mainModel.getMessagesResponse().observe(this, new Observer<List<MessageProvider>>() {
            @Override
            public void onChanged(@Nullable List<MessageProvider> messagesResponse) {
                handleMessagesList(messagesResponse, false);
            }
        });

        mainModel.getStarredMessagesResponse().observe(this, new Observer<List<MessageProvider>>() {
            @Override
            public void onChanged(@Nullable List<MessageProvider> messagesResponse) {
                handleMessagesList(messagesResponse, true);
            }
        });

        mainModel.getCurrentFolder().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                currentFolder = mainModel.getCurrentFolder().getValue();
                if (currentFolder.equals("starred")) {
                    mainModel.getStarredMessages(50, 0, 1);
                } else {
                    mainModel.getMessages(50, 0, currentFolder);
                }
                setHasOptionsMenu(false);
                setHasOptionsMenu(true);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();

        currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder != null) {
            if (currentFolder.equals("starred")) {
                mainModel.getStarredMessages(50, 0, 1);
            } else {
                mainModel.getMessages(50, 0, currentFolder);
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

        MenuItem emptyFolder = menu.findItem(R.id.action_empty_folder);
        emptyFolder.setVisible(currentFolder.equals("trash")
                || currentFolder.equals("spam")
                || currentFolder.equals("draft"));

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView searchView = null;
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//        }
//        if (searchView != null) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
        } else if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_empty_folder) {
            List<MessageProvider> messages = adapter.getAll();
            StringBuilder messagesIds = new StringBuilder();
            for (MessageProvider messagesResult : messages) {
                messagesIds.append(messagesResult.getId());
                messagesIds.append(',');
            }
            mainModel.deleteSeveralMessages(messagesIds.toString());
            mainModel.getMessages(50, 0, currentFolder);
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

    public void handleMessagesList(List<MessageProvider> messages, boolean starredMessages) {
        if(messages == null || messages.isEmpty()) {
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

            String messagesFolder = messages.get(0).getFolderName();
            if (currentFolder != null && !currentFolder.equals(messagesFolder) && !starredMessages) {
                return;
            }

            adapter = new InboxMessagesAdapter(messages, mainModel);
            adapter.getOnClickSubject()
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            Intent intent = new Intent(getActivity(), ViewMessagesActivity.class);
                            intent.putExtra(ViewMessagesActivity.PARENT_ID, aLong);
                            intent.putExtra(ViewMessagesActivity.FOLDER_NAME, mainModel.currentFolder.getValue());
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

            if (touchListener != null) {
                recyclerView.removeOnItemTouchListener(touchListener);
            }
            currentFolder = mainModel.getCurrentFolder().getValue();
            touchListener = new InboxMessagesTouchListener(getActivity(), recyclerView);
            if (currentFolder.equals("draft")) {
                touchListener.setSwipeOptionViews(R.id.item_message_view_holder_delete);
            } else {
                touchListener.setSwipeOptionViews(R.id.item_message_view_holder_spam, R.id.item_message_view_holder_move, R.id.item_message_view_holder_delete);
            }
            touchListener
                    .setSwipeable(R.id.item_message_view_holder_foreground, R.id.item_message_view_holder_background_layout, new InboxMessagesTouchListener.OnSwipeOptionsClickListener() {
                        @Override
                        public void onSwipeOptionClicked(int viewID, final int position) {
                            switch (viewID){
                                case R.id.item_message_view_holder_delete:
                                    final MessageProvider deletedMessage = adapter.removeAt(position);
                                    final String name = deletedMessage.getSubject();
                                    final String currentFolderFinal = currentFolder;
                                    Snackbar deleteSnackbar;
                                    if (currentFolderFinal.equals("trash") || currentFolderFinal.equals("draft")) {
                                        deleteSnackbar = Snackbar
                                                .make(frameCompose, name + " permanently deleted", Snackbar.LENGTH_LONG);
                                    } else {
                                        deleteSnackbar = Snackbar
                                                .make(frameCompose, name + " removed", Snackbar.LENGTH_LONG);
                                    }

                                    deleteSnackbar.setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            adapter.restoreMessage(deletedMessage, position);
                                        }
                                    });
                                    deleteSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                        @Override
                                        public void onDismissed(Snackbar transientBottomBar, int event) {
                                            if (event != DISMISS_EVENT_ACTION) {
                                                if (currentFolderFinal.equals("trash") || currentFolderFinal.equals("draft")) {
                                                    mainModel.deleteMessage(deletedMessage.getId());
                                                } else {
                                                    mainModel.toFolder(deletedMessage.getId(), "trash");
                                                }
                                            }
                                        }
                                    });
                                    deleteSnackbar.setActionTextColor(Color.YELLOW);
                                    deleteSnackbar.show();
                                    break;

                                case R.id.item_message_view_holder_spam:
                                    final MessageProvider spamMessage = adapter.removeAt(position);
                                    Snackbar spamSnackbar = Snackbar
                                            .make(frameCompose, "1 reported as spam", Snackbar.LENGTH_LONG);
                                    spamSnackbar.setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            adapter.restoreMessage(spamMessage, position);
                                        }
                                    });
                                    spamSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                        @Override
                                        public void onDismissed(Snackbar transientBottomBar, int event) {
                                            if (event != DISMISS_EVENT_ACTION) {
                                                mainModel.toFolder(spamMessage.getId(), "spam");
                                            }
                                        }
                                    });
                                    spamSnackbar.setActionTextColor(Color.YELLOW);
                                    spamSnackbar.show();
                                    break;

                                case R.id.item_message_view_holder_move:
                                    Toast.makeText(getActivity().getApplicationContext(), "Action move", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });

            recyclerView.addOnItemTouchListener(touchListener);
        }
    }
}