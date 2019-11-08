package mobileapp.ctemplar.com.ctemplarapp.main;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.message.MoveDialogFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.MESSAGE_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesFragment.FOLDER_NAME;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;

public class InboxFragment extends BaseFragment
        implements InboxMessagesAdapter.OnReachedBottomCallback {
    private static final int SWIPABLE_FOREGROUND_RESOURCE_ID = R.id.item_message_view_holder_foreground;
    private static final int SWIPABLE_BACKGROUND_RESOURCE_ID = R.id.item_message_view_holder_background_layout;
    private static final int REQUEST_MESSAGES_COUNT = 20;
    public static WeakReference<InboxFragment> instanceReference = null;

    private InboxMessagesAdapter adapter;
    private MainActivityViewModel mainModel;
    private InboxMessagesTouchListener touchListener;
    private FilterDialogFragment dialogFragment;
    private String currentFolder;
    private boolean messagesNotEmpty;

    private boolean filterIsStarred;
    private boolean filterIsUnread;
    private boolean filterWithAttachment;
    private String filterText;

    private int currentOffset = 0;
    private boolean isLoadingNewMessages = false;

    @OnClick(R.id.fragment_inbox_send_layout)
    void onClickComposeLayout() {
        startSendMessageActivity();
    }

    @OnClick(R.id.fragment_inbox_send)
    void onClickCompose() {
        startSendMessageActivity();
    }

    @OnClick(R.id.fragment_inbox_fab_compose)
    void onClickFabCompose() {
        startSendMessageActivity();
    }


    @BindView(R.id.fragment_inbox_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_inbox_icon_empty)
    ImageView imgEmpty;

    @BindView(R.id.fragment_inbox_title_empty)
    TextView txtEmpty;

    @BindView(R.id.fragment_inbox_list_empty_layout)
    ConstraintLayout listEmptyLayout;

    @BindView(R.id.fragment_inbox_progress_layout)
    ConstraintLayout progressLayout;

    @BindView(R.id.fragment_inbox_send_layout)
    FrameLayout frameCompose;

    @BindView(R.id.fragment_inbox_fab_compose)
    FloatingActionButton fabCompose;

    @BindView(R.id.fragment_inbox_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private FilterDialogFragment.OnApplyClickListener onFilterApplyClickListener
            = new FilterDialogFragment.OnApplyClickListener() {
        @Override
        public void onApply(boolean isStarred, boolean isUnread, boolean withAttachment) {
            adapter.filter(isStarred, isUnread, withAttachment);
            filterIsStarred = isStarred;
            filterIsUnread = isUnread;
            filterWithAttachment = withAttachment;
            restartOptionsMenu();
            if (adapter.getItemCount() > 0) {
                showMessagesList();
            } else {
                hideMessagesList();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_inbox;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dialogFragment = new FilterDialogFragment();
        dialogFragment.setOnApplyClickListener(onFilterApplyClickListener);

        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        mainModel = ViewModelProviders.of(activity).get(MainActivityViewModel.class);
        currentFolder = mainModel.currentFolder.getValue();
        if (currentFolder == null) {
            currentFolder = MainFolderNames.INBOX;
        }
        adapter = new InboxMessagesAdapter(mainModel);
        adapter.setOnReachedBottomCallback(this);
        adapter.getOnClickSubject()
                .subscribeOn(Schedulers.io())
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
        instanceReference = new WeakReference<>(this);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        mainModel.getResponseStatus().observe(this, status -> {
            handleResponseStatus(status);
            swipeRefreshLayout.setRefreshing(false);
        });
        mainModel.getMessagesResponse().observe(this, messagesResponse -> {
            if (messagesResponse != null) {
                handleMessagesList(messagesResponse.messages,
                        messagesResponse.folderName, messagesResponse.offset);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mainModel.getCurrentFolder().observe(this, folderName -> {
            currentFolder = folderName;
            requestNewMessages();
            restartOptionsMenu();
            String emptyFolderString = getResources().getString(R.string.title_empty_messages, folderName);
            txtEmpty.setText(emptyFolderString);
            loadMessagesList();
            recyclerView.setAdapter(adapter);
            updateTouchListenerSwipeOptions(currentFolder);
            swipeRefreshLayout.setRefreshing(false);
        });
        mainModel.getDeleteSeveralMessagesStatus().observe(this, responseStatus -> requestNewMessages());

        swipeRefreshLayout.setOnRefreshListener(this::requestNewMessages);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        bindTouchListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestNewMessages();
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        MenuItem filterIcon = menu.findItem(R.id.action_filter);
        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
            filterIcon.setIcon(R.drawable.ic_action_filter_on);
        } else {
            filterIcon.setIcon(R.drawable.ic_action_filter_off);
        }

        // Function
        MenuItem emptyFolder = menu.findItem(R.id.action_empty_folder);
        if (currentFolder != null) {
            boolean inTrash = currentFolder.equals(MainFolderNames.TRASH);
            boolean inSpam = currentFolder.equals(MainFolderNames.SPAM);
            boolean inDraft = currentFolder.equals(MainFolderNames.DRAFT);
            emptyFolder.setVisible((inTrash || inSpam || inDraft) && messagesNotEmpty);
        }
        if (getActivity() == null) {
            return;
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String text) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String text) {
                    adapter.filter(text);
                    filterText = text;
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (getFragmentManager() != null) {
                    dialogFragment.show(getFragmentManager(), null);
                }
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_empty_folder:
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
                            .setPositiveButton(getString(R.string.btn_confirm), (dialog, which) -> mainModel.deleteSeveralMessages(messagesIds.toString())
                            )
                            .setNeutralButton(getString(R.string.btn_cancel), null)
                            .show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestNextMessages() {
        if (isLoadingNewMessages) {
            return;
        }
        currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder == null) {
            Timber.e("RequestNextMessages: current folder is null");
            return;
        }
        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
            return;
        }

        currentFolder = mainModel.currentFolder.getValue();
        mainModel.getMessages(REQUEST_MESSAGES_COUNT, currentOffset, currentFolder);
        currentOffset += REQUEST_MESSAGES_COUNT;
        isLoadingNewMessages = true;
    }

    private void requestNewMessages() {
        isLoadingNewMessages = false;
        currentOffset = 0;
        requestNextMessages();
    }

    @Override
    public void onReachedBottom() {
        Timber.i("onReachedBottom");
        requestNextMessages();
    }


    private void restartOptionsMenu() {
        setHasOptionsMenu(false);
        setHasOptionsMenu(true);
    }

    private void updateTouchListenerSwipeOptions(String folder) {
        if (folder != null && folder.equals(DRAFT)) {
            touchListener.setSwipeOptionViews(R.id.item_message_view_holder_delete);
        } else if (folder != null && folder.equals(SPAM)) {
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
    }

    private void bindTouchListener() {
        touchListener = new InboxMessagesTouchListener(getActivity(), recyclerView);
        updateTouchListenerSwipeOptions(currentFolder);
        touchListener.setSwipeable(SWIPABLE_FOREGROUND_RESOURCE_ID, SWIPABLE_BACKGROUND_RESOURCE_ID,
                (viewID, position) -> {
                    final String currentFolderFinal = currentFolder;
                    switch (viewID) {
                        case R.id.item_message_view_holder_delete:
                            final MessageProvider deletedMessage = adapter.removeAt(position);
                            final String name = deletedMessage.getSubject();
                            if (!currentFolderFinal.equals(MainFolderNames.TRASH)
                                    && !currentFolderFinal.equals(SPAM)) {
                                mainModel.toFolder(deletedMessage.getId(), MainFolderNames.TRASH);
                                showRestoreSnackBar(getResources().getString(R.string.txt_name_removed, name), () -> {
                                    mainModel.toFolder(deletedMessage.getId(), currentFolderFinal);
                                    if (currentFolder.equals(currentFolderFinal)) {
                                        adapter.restoreMessage(deletedMessage, position);
                                    }
                                });
                            } else {
                                mainModel.deleteMessage(deletedMessage.getId());
                            }
                            break;

                        case R.id.item_message_view_holder_spam:
                            if (!currentFolder.equals(MainFolderNames.SPAM)) {
                                final MessageProvider spamMessage = adapter.removeAt(position);
                                mainModel.toFolder(spamMessage.getId(), MainFolderNames.SPAM);
                                showRestoreSnackBar(getResources().getString(R.string.action_spam), () -> {
                                    mainModel.toFolder(spamMessage.getId(), currentFolderFinal);
                                    if (currentFolder.equals(currentFolderFinal)) {
                                        adapter.restoreMessage(spamMessage, position);
                                    }
                                });
                            }
                            break;

                        case R.id.item_message_view_holder_inbox:
                            if (currentFolder.equals(MainFolderNames.SPAM)) {
                                final MessageProvider notSpamMessage = adapter.removeAt(position);
                                mainModel.toFolder(notSpamMessage.getId(), MainFolderNames.INBOX);
                                showRestoreSnackBar(getResources().getString(R.string.action_moved_to_inbox), () -> {
                                    mainModel.toFolder(notSpamMessage.getId(), currentFolderFinal);
                                    if (currentFolder.equals(currentFolderFinal)) {
                                        adapter.restoreMessage(notSpamMessage, position);
                                    }
                                });
                            }
                            break;

                        case R.id.item_message_view_holder_move:
                            MessageProvider movedMessage = adapter.get(position);
                            MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
                            Bundle moveFragmentBundle = new Bundle();
                            moveFragmentBundle.putLong(PARENT_ID, movedMessage.getId());
                            moveDialogFragment.setArguments(moveFragmentBundle);
                            moveDialogFragment.setOnMoveCallback(folderName -> adapter.removeAt(position));
                            if (getFragmentManager() != null) {
                                moveDialogFragment.show(getFragmentManager(), "MoveDialogFragment");
                            }
                            break;
                    }
                });
        recyclerView.addOnItemTouchListener(touchListener);
    }

    private void showRestoreSnackBar(String message, final Runnable onUndoClick) {
        Snackbar.make(frameCompose, message, Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.action_undo), view -> onUndoClick.run())
                .setActionTextColor(Color.YELLOW)
                .show();
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

        if (folderName != null && folderName.equals(DRAFT)) {
            Intent draftIntent = new Intent(activity, SendMessageActivity.class);
            draftIntent.putExtra(MESSAGE_ID, parentId);

            Fragment draftFragment = SendMessageFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong(MESSAGE_ID, parentId);
            draftFragment.setArguments(bundle);

            activity.showActivityOrFragment(draftIntent, draftFragment);
        } else {
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
    }

    private void handleResponseStatus(ResponseStatus status) {
        mainModel.hideProgressDialog();
        if(status != null) {
            switch(status) {
                case RESPONSE_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.error_messages), Toast.LENGTH_SHORT).show();
                    Timber.e("Response error");
                    break;
                case RESPONSE_NEXT_MESSAGES:
                    // adapter = new InboxMessagesAdapter(mainModel.getMessagesResponse().getValue().getMessagesList());
                    // recyclerView.setAdapter(adapter);
                    break;
            }
        }
    }

    private void loadMessagesList() {
        recyclerView.setVisibility(View.GONE);
        fabCompose.hide();
        listEmptyLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
    }

    private void hideMessagesList() {
        recyclerView.setVisibility(View.GONE);
        fabCompose.hide();
        listEmptyLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
    }

    private void showMessagesList() {
        recyclerView.setVisibility(View.VISIBLE);
        fabCompose.show();
        listEmptyLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
    }

    private void handleMessagesList(List<MessageProvider> messages, String folderName, int offset) {
        currentFolder = mainModel.getCurrentFolder().getValue();
        messagesNotEmpty = messages != null && !messages.isEmpty();

        if (messagesNotEmpty && currentFolder != null && !currentFolder.equals(folderName)) {
            return;
        }
        if (messages == null || messages.isEmpty()) {
            hideMessagesList();
            messages = new ArrayList<>();
        } else {
            showMessagesList();
        }
        if (offset == 0) {
            adapter.clear();
        }
        adapter.addMessages(messages);
        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
            adapter.filter(filterIsStarred, filterIsUnread, filterWithAttachment);
        }
        if (filterText != null) {
            adapter.filter(filterText);
        }
        if (adapter.getItemCount() > 0) {
            showMessagesList();
        } else {
            hideMessagesList();
        }
        isLoadingNewMessages = false;
    }

    void clearListAdapter() {
        if (recyclerView != null && adapter != null) {
            adapter.clear();
        }
    }

    public void onNewMessage(long messageId) {
        requestNewMessages();
    }
}
