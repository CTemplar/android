package mobileapp.ctemplar.com.ctemplarapp.main;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.message.MoveDialogFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.STARRED;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesFragment.FOLDER_NAME;

public class InboxFragment extends BaseFragment {

    private InboxMessagesAdapter adapter;
    private MainActivityViewModel mainModel;
    private InboxMessagesTouchListener touchListener;
    private FilterDialogFragment dialogFragment;
    private String currentFolder;
    private boolean messagesNotEmpty;

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
        dialogFragment = new FilterDialogFragment();
        dialogFragment.setOnApplyClickListener(onFilterApplyClickListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        mainModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mainModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus status) {
                handleResponseStatus(status);
            }
        });

        mainModel.getMessagesResponse().observe(this, new Observer<ResponseMessagesData>() {
            @Override
            public void onChanged(@Nullable ResponseMessagesData messagesResponse) {
                if (messagesResponse != null) {
                    handleMessagesList(messagesResponse.messages, false, messagesResponse.folderName);
                }
            }
        });

        mainModel.getStarredMessagesResponse().observe(this, new Observer<List<MessageProvider>>() {
            @Override
            public void onChanged(@Nullable List<MessageProvider> messagesResponse) {
                handleMessagesList(messagesResponse, true, STARRED);
            }
        });

        mainModel.getCurrentFolder().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                getMessages();
                String emptyFolderString = getResources().getString(R.string.title_empty_messages, currentFolder);
                txtEmpty.setText(emptyFolderString);
                restartOptionsMenu();
            }
        });

        mainModel.getDeleteSeveralMessagesStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                mainModel.getMessages(50, 0, currentFolder);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessages();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void restartOptionsMenu() {
        setHasOptionsMenu(false);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        // Function
        MenuItem emptyFolder = menu.findItem(R.id.action_empty_folder);
        if (currentFolder != null) {
            boolean inTrash = currentFolder.equals(TRASH);
            boolean inSpam = currentFolder.equals(SPAM);
            boolean inDraft = currentFolder.equals(DRAFT);
            emptyFolder.setVisible((inTrash || inSpam || inDraft) && messagesNotEmpty);
        }

        if (getActivity() == null) {
            return;
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapter.filter(s);
                    return false;
                }
            });
        }
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
            if (getFragmentManager() != null) {
                dialogFragment.show(getFragmentManager(), null);
            }
            return true;
        } else if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_empty_folder) {
            List<MessageProvider> messages = adapter.getAll();
            final StringBuilder messagesIds = new StringBuilder();
            for (MessageProvider messagesResult : messages) {
                messagesIds.append(messagesResult.getId());
                messagesIds.append(',');
            }
            if (!messages.isEmpty()) {
                messagesIds.deleteCharAt(messagesIds.length() - 1);
            }

            if (getActivity() != null) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.title_clear_folder))
                        .setMessage(getString(R.string.txt_clear_folder))
                        .setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mainModel.deleteSeveralMessages(messagesIds.toString());
                                    }
                                }
                        )
                        .setNeutralButton(getString(R.string.btn_cancel), null)
                        .show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.fragment_inbox_send_layout)
    public void onClickComposeLayout() {
        startSendMessageActivity();
    }
    @OnClick(R.id.fragment_inbox_send)
    public void onClickCompose() {
        startSendMessageActivity();
    }

    @OnClick(R.id.fragment_inbox_fab_compose)
    public void onClickFabCompose() {
        startSendMessageActivity();
    }

    private void startSendMessageActivity() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }

        Intent intent = new Intent(activity, SendMessageActivity.class);
        Fragment fragment = SendMessageFragment.newInstance();
        activity.showActivityOrFragment(intent, fragment);
    }

    private void startViewMessageActivity(Long parentId) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        String folderName = mainModel.currentFolder.getValue();

        Intent intent = new Intent(activity, ViewMessagesActivity.class);
        intent.putExtra(PARENT_ID, parentId);
        intent.putExtra(FOLDER_NAME, folderName);

        Fragment fragment = ViewMessagesFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong(PARENT_ID, parentId);
        bundle.putString(FOLDER_NAME, folderName);
        fragment.setArguments(bundle);

        activity.showActivityOrFragment(intent, fragment);
    }

    public void handleResponseStatus(ResponseStatus status) {
        mainModel.hideProgressDialog();
        if(status != null) {
            switch(status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                    Timber.e("Response error");
                    break;
                case RESPONSE_NEXT_MESSAGES:
                    // adapter = new InboxMessagesAdapter(mainModel.getMessagesResponse().getValue().getMessagesList());
                    // recyclerView.setAdapter(adapter);
                    break;
            }
        }
    }

    private void getMessages() {
        currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder != null) {
            if (currentFolder.equals(STARRED)) {
                mainModel.getStarredMessages(50, 0, 1);
            } else {
                mainModel.getMessages(50, 0, currentFolder);
            }
        }
    }

    private void hideMessagesList() {
        recyclerView.setVisibility(View.GONE);
        fabCompose.hide();
        imgEmpty.setVisibility(View.VISIBLE);
        txtEmpty.setVisibility(View.VISIBLE);
        frameCompose.setVisibility(View.VISIBLE);
    }

    private void showMessagesList() {
        recyclerView.setVisibility(View.VISIBLE);
        fabCompose.show();
        imgEmpty.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        frameCompose.setVisibility(View.GONE);
    }

    public void handleMessagesList(List<MessageProvider> messages, boolean starredMessages, String folderName) {
        restartOptionsMenu();
        currentFolder = mainModel.getCurrentFolder().getValue();
        messagesNotEmpty = messages != null && !messages.isEmpty();

        if (messagesNotEmpty) {
            if (currentFolder != null && !currentFolder.equals(folderName)) {
                return;
            }
        }

        if (messages == null || messages.isEmpty()) {
            hideMessagesList();
            messages = new ArrayList<>();

        } else {
            showMessagesList();
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
                    public void onNext(Long parentId) {
                        startViewMessageActivity(parentId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        recyclerView.setAdapter(adapter);

        if (touchListener != null) {
            recyclerView.removeOnItemTouchListener(touchListener);
        }
        touchListener = new InboxMessagesTouchListener(getActivity(), recyclerView);
        if (currentFolder != null && currentFolder.equals(DRAFT)) {
            touchListener.setSwipeOptionViews(R.id.item_message_view_holder_delete);
        } else if (currentFolder != null && currentFolder.equals(SPAM)) {
            touchListener.setSwipeOptionViews(
                    R.id.item_message_view_holder_inbox,
                    R.id.item_message_view_holder_move,
                    R.id.item_message_view_holder_delete
            );
        } else {
            touchListener.setSwipeOptionViews(
                    R.id.item_message_view_holder_spam,
                    R.id.item_message_view_holder_move,
                    R.id.item_message_view_holder_delete
            );
        }
        touchListener
                .setSwipeable(R.id.item_message_view_holder_foreground, R.id.item_message_view_holder_background_layout, new InboxMessagesTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, final int position) {
                        final String currentFolderFinal = currentFolder;

                        switch (viewID) {
                            case R.id.item_message_view_holder_delete:
                                final MessageProvider deletedMessage = adapter.removeAt(position);
                                final String name = deletedMessage.getSubject();

                                if (!currentFolderFinal.equals(TRASH)
                                        && !currentFolderFinal.equals(SPAM)) {

                                    mainModel.toFolder(deletedMessage.getId(), TRASH);
                                    Snackbar snackbarDelete = Snackbar.make(frameCompose, getResources().getString(R.string.txt_name_removed, name), Snackbar.LENGTH_LONG);
                                    snackbarDelete.setAction(getResources().getString(R.string.action_undo), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mainModel.toFolder(deletedMessage.getId(), currentFolderFinal);
                                            if (currentFolder.equals(currentFolderFinal)) {
                                                adapter.restoreMessage(deletedMessage, position);
                                            } else {
                                                getMessages();
                                            }
                                        }
                                    });
                                    snackbarDelete.setActionTextColor(Color.YELLOW);
                                    snackbarDelete.show();
                                } else {
                                    mainModel.deleteMessage(deletedMessage.getId());
                                }
                                break;

                            case R.id.item_message_view_holder_spam:
                                if (!currentFolder.equals(SPAM)) {
                                    final MessageProvider spamMessage = adapter.removeAt(position);
                                    mainModel.toFolder(spamMessage.getId(), SPAM);
                                    Snackbar snackbarSpam = Snackbar.make(frameCompose, getResources().getString(R.string.action_spam), Snackbar.LENGTH_LONG);
                                    snackbarSpam.setAction(getResources().getString(R.string.action_undo), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mainModel.toFolder(spamMessage.getId(), currentFolderFinal);
                                            if (currentFolder.equals(currentFolderFinal)) {
                                                adapter.restoreMessage(spamMessage, position);
                                            } else {
                                                getMessages();
                                            }
                                        }
                                    });
                                    snackbarSpam.setActionTextColor(Color.YELLOW);
                                    snackbarSpam.show();
                                }
                                break;

                            case R.id.item_message_view_holder_inbox:
                                if (currentFolder.equals(SPAM)) {
                                    final MessageProvider notSpamMessage = adapter.removeAt(position);
                                    mainModel.toFolder(notSpamMessage.getId(), INBOX);
                                    Snackbar snackbarSpam = Snackbar.make(frameCompose, getResources().getString(R.string.action_moved_to_inbox), Snackbar.LENGTH_LONG);
                                    snackbarSpam.setAction(getResources().getString(R.string.action_undo), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mainModel.toFolder(notSpamMessage.getId(), currentFolderFinal);
                                            if (currentFolder.equals(currentFolderFinal)) {
                                                adapter.restoreMessage(notSpamMessage, position);
                                            } else {
                                                getMessages();
                                            }
                                        }
                                    });
                                    snackbarSpam.setActionTextColor(Color.YELLOW);
                                    snackbarSpam.show();
                                }
                                break;

                            case R.id.item_message_view_holder_move:
                                MessageProvider movedMessage = adapter.get(position);
                                MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
                                Bundle moveFragmentBundle = new Bundle();
                                moveFragmentBundle.putLong(PARENT_ID, movedMessage.getId());
                                moveDialogFragment.setArguments(moveFragmentBundle);
                                moveDialogFragment.setOnMoveCallback(new MoveDialogFragment.OnMoveListener() {
                                    @Override
                                    public void onMove(String folderName) {
                                        adapter.removeAt(position);
                                    }
                                });
                                if (getFragmentManager() != null) {
                                    moveDialogFragment.show(getFragmentManager(), "MoveDialogFragment");
                                }
                                break;
                        }
                    }
                });

        recyclerView.addOnItemTouchListener(touchListener);
    }

    public void clearListAdapter() {
        if (recyclerView != null) {
            recyclerView.setAdapter(new InboxMessagesAdapter(Collections.<MessageProvider>emptyList(), mainModel));
        }
    }
}
