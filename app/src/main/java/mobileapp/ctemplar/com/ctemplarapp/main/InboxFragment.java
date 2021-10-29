package mobileapp.ctemplar.com.ctemplarapp.main;

import static mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity.MESSAGE_ID;
import static mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity.PARENT_ID;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.FOLDER_NAME;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.concurrent.HandlerExecutor;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentInboxBinding;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.ViewMessagesFragment;
import mobileapp.ctemplar.com.ctemplarapp.message.dialog.MoveDialogFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.ResponseMessagesData;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class InboxFragment extends BaseFragment implements InboxMessagesAdapter.OnReachedBottomCallback {
    public static WeakReference<InboxFragment> instanceReference = null;
    private static final int REQUEST_MESSAGES_COUNT = 10;

    private FragmentInboxBinding binding;

    private InboxMessagesAdapter adapter;
    private MainActivityViewModel mainModel;
    private InboxMessagesTouchListener touchListener;
    private FilterDialogFragment dialogFragment;
    private SearchView searchView;
    private String currentFolder;
    private Executor mainThreadExecutor;

    private boolean filterIsStarred;
    private boolean filterIsUnread;
    private boolean filterWithAttachment;
    private String filterText;

    private int currentOffset = 0;
    private boolean isLoadingNewMessages = false;

    private final FilterDialogFragment.OnApplyClickListener onFilterApplyClickListener
            = new FilterDialogFragment.OnApplyClickListener() {
        @Override
        public void onApply(boolean isStarred, boolean isUnread, boolean withAttachment) {
            adapter.filter(isStarred, isUnread, withAttachment);
            filterIsStarred = isStarred;
            filterIsUnread = isUnread;
            filterWithAttachment = withAttachment;
            invalidateOptionsMenu();
            showResultIfNotEmpty(false);
            displayFilteredCategories();
        }
    };

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInboxBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        mainThreadExecutor = new HandlerExecutor(Looper.getMainLooper());
        mainModel = new ViewModelProvider(activity).get(MainActivityViewModel.class);
        currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder == null) {
            currentFolder = INBOX;
        }
        adapter = new InboxMessagesAdapter(mainModel);
        adapter.setOnReachedBottomCallback(this);
        adapter.getOnClickSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Long>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Long parentId) {
                        startViewMessageActivity(parentId);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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
            Timber.e("activity is null");
            return;
        }
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mainModel.getResponseStatus().observe(getViewLifecycleOwner(), this::handleResponseStatus);
        mainModel.getMessagesResponse().observe(getViewLifecycleOwner(), this::handleMessagesList);
        mainModel.getSearchMessagesResponse().observe(getViewLifecycleOwner(), this::handleSearchMessagesList);
        mainModel.getDeleteMessagesStatus().observe(getViewLifecycleOwner(), this::updateMessagesResponse);
        mainModel.getEmptyFolderStatus().observe(getViewLifecycleOwner(), this::updateMessagesResponse);
        mainModel.getCurrentFolder().observe(getViewLifecycleOwner(), folderName -> {
            currentFolder = folderName;
            binding.swipeRefreshLayout.setRefreshing(false);
            requestNewMessages();
            binding.folderEmptySearchTextView.setText(getString(R.string.title_empty_messages, folderName));
            binding.recyclerView.setAdapter(adapter);
            updateTouchListenerSwipeOptions(currentFolder);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // display loader only if another is off
            if (isMainProgressLoaderVisible()) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            requestNewMessages();
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    Timber.d("onReachedBottom");
                    requestNextMessages();
                }
            }
        });

        binding.fabCompose.setOnClickListener(v -> startSendMessageActivity());
        binding.sendButton.setOnClickListener(v -> startSendMessageActivity());
        binding.sendButtonLayout.setOnClickListener(v -> startSendMessageActivity());

        bindTouchListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestNewMessages();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem filterIcon = menu.findItem(R.id.action_filter);
        if (filterIcon == null) {
            return;
        }
        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
            filterIcon.setIcon(R.drawable.ic_action_filter_on);
        } else {
            filterIcon.setIcon(R.drawable.ic_action_filter_off);
        }

        MenuItem emptyFolder = menu.findItem(R.id.action_empty_folder);
        if (currentFolder != null) {
            boolean inTrash = currentFolder.equals(TRASH);
            boolean inSpam = currentFolder.equals(SPAM);
            boolean inDraft = currentFolder.equals(DRAFT);
            emptyFolder.setVisible((inTrash || inSpam || inDraft) && adapterIsNotEmpty());
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String text) {
                    requestNewMessages();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String text) {
                    adapter.filter(text);
                    filterText = text;
                    if (TextUtils.isEmpty(text)) {
                        requestNewMessages();
                    }
                    return false;
                }
            });
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (!dialogFragment.isAdded()) {
                    dialogFragment.show(getParentFragmentManager(), null);
                }
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_empty_folder:
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    Timber.e("onOptionsItemSelected: activity is null");
                    return true;
                }
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_clear_folder)
                        .setMessage(R.string.txt_clear_folder)
                        .setPositiveButton(R.string.title_confirm, (dialog, which) -> {
                            if (currentFolder.equals(DRAFT)) {
                                mainModel.deleteMessages(adapter.getAllMessagesIds());
                                return;
                            }
                            mainModel.emptyFolder(currentFolder);
                        })
                        .setNeutralButton(R.string.btn_cancel, null)
                        .show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestNewMessages() {
        isLoadingNewMessages = false;
        currentOffset = 0;
        // display loader only if another is off
        if (!binding.swipeRefreshLayout.isRefreshing() && !isMainProgressLoaderVisible()) {
            showMessagesListProgressLoader();
        }
        requestNextMessages();
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
//        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
//            return;
//        }

        currentFolder = mainModel.getCurrentFolder().getValue();
        boolean isSearch = EditTextUtils.isNotEmpty(filterText);
        if (isSearch) {
            mainModel.searchMessages(filterText, REQUEST_MESSAGES_COUNT, currentOffset);
        } else {
            Date lastMessageUpdateTime;
            MessageProvider messageProvider = adapter.getLast();
            if (currentOffset == 0 || messageProvider == null) {
                lastMessageUpdateTime = new Date(System.currentTimeMillis() + 10000);
            } else {
                lastMessageUpdateTime = messageProvider.getUpdatedAt();
            }
            mainModel.getMessages(REQUEST_MESSAGES_COUNT, currentOffset, currentFolder, lastMessageUpdateTime);
        }
        currentOffset += REQUEST_MESSAGES_COUNT;
        isLoadingNewMessages = true;
        // display loader only if another is off
        if (!binding.swipeRefreshLayout.isRefreshing() && !isMainProgressLoaderVisible()) {
            showNextMessagesProgressLoader();
        }
    }

    @Override
    public void onReachedBottom() {
//        requestNextMessages();
//        progressLayout.setVisibility(View.VISIBLE);
    }

    private void updateMessagesResponse(ResponseStatus responseStatus) {
        if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_LONG).show();
        }
        requestNewMessages();
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
        touchListener = new InboxMessagesTouchListener(getActivity(), binding.recyclerView);
        updateTouchListenerSwipeOptions(currentFolder);
        touchListener.setSwipeable(R.id.foreground_layout,
                R.id.background_layout,
                (viewID, position) -> {
                    final String currentFolderFinal = currentFolder;
                    switch (viewID) {
                        case R.id.item_message_view_holder_delete:
                            final MessageProvider deletedMessage = adapter.removeAt(position);
                            final String name = deletedMessage.getSubject();
                            if (!currentFolderFinal.equals(TRASH)
                                    && !currentFolderFinal.equals(SPAM)) {
                                mainModel.toFolder(deletedMessage.getId(), TRASH);
                                showRestoreSnackBar(getString(R.string.txt_name_removed, name), () -> {
                                    mainModel.toFolder(deletedMessage.getId(), currentFolderFinal);
                                    if (currentFolder.equals(currentFolderFinal)) {
                                        adapter.restoreMessage(deletedMessage, position);
                                    }
                                });
                            } else {
                                showDeleteSnackBar(getString(R.string.txt_name_removed, name), () -> {
                                    mainModel.deleteMessages(new Long[]{deletedMessage.getId()});
                                });
                            }
                            break;

                        case R.id.item_message_view_holder_spam:
                            if (!currentFolder.equals(SPAM)) {
                                final MessageProvider spamMessage = adapter.removeAt(position);
                                mainModel.toFolder(spamMessage.getId(), SPAM);
                                showRestoreSnackBar(getString(R.string.action_spam), () -> {
                                    mainModel.toFolder(spamMessage.getId(), currentFolderFinal);
                                    if (currentFolder.equals(currentFolderFinal)) {
                                        adapter.restoreMessage(spamMessage, position);
                                    }
                                });
                            }
                            break;

                        case R.id.item_message_view_holder_inbox:
                            if (currentFolder.equals(SPAM)) {
                                final MessageProvider notSpamMessage = adapter.removeAt(position);
                                mainModel.toFolder(notSpamMessage.getId(), INBOX);
                                showRestoreSnackBar(getString(R.string.action_moved_to_inbox), () -> {
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
                            moveDialogFragment.show(getParentFragmentManager(), "MoveDialogFragment");
                            break;
                    }
                });
        binding.recyclerView.addOnItemTouchListener(touchListener);

        mainModel.getMessageResponse().observe(getViewLifecycleOwner(), messageProvider -> {
            adapter.addMessage(messageProvider);
            binding.recyclerView.scrollToPosition(0);
            decryptSubject(messageProvider);
        });
    }

    private void showRestoreSnackBar(String message, Runnable onUndoClick) {
        Snackbar.make(binding.sendButtonLayout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), view -> onUndoClick.run())
                .setActionTextColor(Color.YELLOW)
                .show();
    }

    private void showDeleteSnackBar(String message, Runnable onDismissed) {
        Snackbar.make(binding.sendButtonLayout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), v -> {
                })
                .setActionTextColor(Color.YELLOW)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            onDismissed.run();
                        }
                    }
                })
                .show();
    }

    private void startSendMessageActivity() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        Intent sendMessageIntent = new Intent(activity, SendMessageActivity.class);
        Fragment fragment = SendMessageFragment.newInstance();
        activity.showActivityOrFragment(sendMessageIntent, fragment);
    }

    private void startViewMessageActivity(Long parentId) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        String folderName = mainModel.getCurrentFolder().getValue();
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
        if (status != null) {
            switch (status) {
                case RESPONSE_ERROR:
                    mainModel.checkUserSession();
                    Timber.e("handleResponseStatus: RESPONSE_ERROR");
                    // Toast.makeText(getActivity(), getString(R.string.error_messages), Toast.LENGTH_SHORT).show();
                    break;
                case RESPONSE_NEXT_MESSAGES:
                    Timber.d("handleResponseStatus: RESPONSE_NEXT_MESSAGES");
                    // adapter = new InboxMessagesAdapter(mainModel.getMessagesResponse().getValue().getMessagesList());
                    // recyclerView.setAdapter(adapter);
                    break;
            }
        }
    }

    private void invalidateOptionsMenu() {
        FragmentActivity activity = getActivity();
        if (activity == null || searchView == null || !searchView.isIconified()) {
            return;
        }
        activity.invalidateOptionsMenu();
    }

    private void handleMessagesList(ResponseMessagesData response) {
        List<MessageProvider> messages = response.getMessages();
        int offset = response.getOffset();
        String folderName = response.getFolderName() == null ? "" : response.getFolderName();
        currentFolder = mainModel.getCurrentFolder().getValue();
        boolean messagesIsEmpty = messages == null || messages.isEmpty();

        if (!messagesIsEmpty && currentFolder != null && !currentFolder.equals(folderName)) {
            return;
        }
        if (messagesIsEmpty) {
            messages = new ArrayList<>();
        }
        if (offset == 0) {
            adapter.clear();
        }
        decryptSubjects(messages);
        adapter.addMessages(messages);
        applyFiltersToMessages();
        showResultIfNotEmpty(false);
    }

    private void handleSearchMessagesList(ResponseMessagesData response) {
        List<MessageProvider> messages = response.getMessages();
        int offset = response.getOffset();
        if (offset == 0) {
            adapter.clear();
        }
        decryptSubjects(messages);
        adapter.addMessages(messages);
        applyFiltersToMessages();
        showResultIfNotEmpty(true);
    }

    private void decryptSubjects(List<MessageProvider> messages) {
        mainModel.decryptSubjects(messages, (message)
                -> mainThreadExecutor.execute(() -> adapter.onItemUpdated(message)));
    }

    private void decryptSubject(MessageProvider message) {
        decryptSubjects(Collections.singletonList(message));
    }

    private void applyFiltersToMessages() {
        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
            adapter.filter(filterIsStarred, filterIsUnread, filterWithAttachment);
        }
        if (EditTextUtils.isNotEmpty(filterText)) {
            adapter.filter(filterText);
        }
        isLoadingNewMessages = false;
        invalidateOptionsMenu();
    }

    private boolean adapterIsNotEmpty() {
        if (adapter == null) {
            return false;
        }
        return adapter.getItemCount() > 0;
    }

    public void clearListAdapter() {
        if (adapter == null) {
            Timber.e("clearListAdapter: adapter is null");
            return;
        }
        adapter.clear();
    }

    public void onNewMessage(long messageId, String folder) {
        Activity activity = getActivity();
        if (activity == null || currentFolder == null) {
            return;
        }
        if (currentFolder.equals(folder)) {
            mainModel.getMessage(messageId, folder);
        }
    }

    private void showResultIfNotEmpty(boolean isServerSearchResult) {
        if (adapterIsNotEmpty()) {
            showMessagesList();
            return;
        }
        if (filterIsStarred || filterIsUnread || filterWithAttachment) {
            showFilteredMessagesListEmptyIcon();
            return;
        }
        if (EditTextUtils.isNotEmpty(filterText) || isServerSearchResult) {
            showSearchMessagesListEmptyIcon();
            return;
        }
        showMessagesListEmptyIcon();
    }

    private void displayFilteredCategories() {
        List<String> filteredBy = new ArrayList<>();
        if (filterIsStarred) {
            filteredBy.add(getString(R.string.txt_starred));
        }
        if (filterIsUnread) {
            filteredBy.add(getString(R.string.txt_unread));
        }
        if (filterWithAttachment) {
            filteredBy.add(getString(R.string.txt_with_attachments));
        }
        if (filteredBy.size() > 0) {
            binding.filteredCategoriesTextView.setText(TextUtils.join(", ", filteredBy));
            binding.filteredLayout.setVisibility(View.VISIBLE);
        } else {
            binding.filteredLayout.setVisibility(View.GONE);
        }
    }

    private void showNextMessagesProgressLoader() {
        binding.progressLayout.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void showMessagesListProgressLoader() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.fabCompose.hide();
        binding.listEmptyLayout.setVisibility(View.GONE);
        binding.listEmptySearchLayout.setVisibility(View.GONE);
        binding.listEmptyFilterLayout.setVisibility(View.GONE);
        binding.progressLayout.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void showMessagesListEmptyIcon() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.fabCompose.hide();
        binding.listEmptyLayout.setVisibility(View.VISIBLE);
        binding.listEmptySearchLayout.setVisibility(View.GONE);
        binding.listEmptyFilterLayout.setVisibility(View.GONE);
        binding.progressLayout.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void showSearchMessagesListEmptyIcon() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.fabCompose.hide();
        binding.listEmptyLayout.setVisibility(View.GONE);
        binding.listEmptySearchLayout.setVisibility(View.VISIBLE);
        binding.listEmptyFilterLayout.setVisibility(View.GONE);
        binding.progressLayout.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void showFilteredMessagesListEmptyIcon() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.fabCompose.hide();
        binding.listEmptyLayout.setVisibility(View.GONE);
        binding.listEmptySearchLayout.setVisibility(View.GONE);
        binding.listEmptyFilterLayout.setVisibility(View.VISIBLE);
        binding.progressLayout.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void showMessagesList() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.fabCompose.show();
        binding.listEmptyLayout.setVisibility(View.GONE);
        binding.listEmptySearchLayout.setVisibility(View.GONE);
        binding.listEmptyFilterLayout.setVisibility(View.GONE);
        binding.progressLayout.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private boolean isMainProgressLoaderVisible() {
        return binding.progressLayout.getVisibility() == View.VISIBLE;
    }
}
