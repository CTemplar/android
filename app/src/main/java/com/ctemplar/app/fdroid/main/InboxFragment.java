package com.ctemplar.app.fdroid.main;

import static com.ctemplar.app.fdroid.message.SendMessageActivity.MESSAGE_ID;
import static com.ctemplar.app.fdroid.message.ViewMessagesActivity.PARENT_ID;
import static com.ctemplar.app.fdroid.message.dialog.MoveDialogFragment.MESSAGE_IDS;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.DRAFT;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.FOLDER_NAME;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.INBOX;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.SPAM;
import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.TRASH;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.FragmentInboxBinding;
import com.ctemplar.app.fdroid.executor.HandlerExecutor;
import com.ctemplar.app.fdroid.message.SendMessageActivity;
import com.ctemplar.app.fdroid.message.SendMessageFragment;
import com.ctemplar.app.fdroid.message.ViewMessagesActivity;
import com.ctemplar.app.fdroid.message.ViewMessagesFragment;
import com.ctemplar.app.fdroid.message.dialog.MoveDialogFragment;
import com.ctemplar.app.fdroid.net.ResponseStatus;
import com.ctemplar.app.fdroid.net.response.ResponseMessagesData;
import com.ctemplar.app.fdroid.repository.dto.SearchMessagesDTO;
import com.ctemplar.app.fdroid.repository.enums.MainFolders;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class InboxFragment extends BaseFragment implements InboxMessagesAdapter.OnReachedBottomCallback {
    private static final int REQUEST_MESSAGES_COUNT = 10;

    private FragmentInboxBinding binding;

    private InboxMessagesAdapter adapter;
    private MainActivityViewModel mainModel;
    private InboxMessagesTouchListener touchListener;
    private SearchDialogFragment searchDialogFragment;
    private SearchView searchView;
    private String currentFolder;
    private Executor mainThreadExecutor;

    private SearchMessagesDTO searchMessages;

    private int currentOffset = 0;
    private boolean isLoadingNewMessages = false;

    private final SearchDialogFragment.SearchClickListener searchClickListener = searchMessages -> {
        InboxFragment.this.searchMessages = searchMessages;
        searchView.setQuery(searchMessages == null ? "" : searchMessages.getQuery(), false);
        invalidateOptionsMenu();
        requestNewMessages();
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
        searchDialogFragment = new SearchDialogFragment();
        searchDialogFragment.setSearchClickListener(searchClickListener);

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
        mainModel.getEmptyFolderStatus().observe(getViewLifecycleOwner(), this::updateMessagesResponse);
        mainModel.getDeleteMessagesStatus().observe(getViewLifecycleOwner(), this::handleDeleteMessagesStatus);
        mainModel.getToFolderStatus().observe(getViewLifecycleOwner(), this::handleToFolderStatus);
        mainModel.getCurrentFolder().observe(getViewLifecycleOwner(), folderName -> {
            currentFolder = folderName;
            binding.swipeRefreshLayout.setRefreshing(false);
            requestNewMessages();
            binding.fragmentInboxTitleEmpty.setText(getString(R.string.title_empty_messages,
                    getString(MainFolders.get(folderName).getDisplayNameResourceId())));
            binding.recyclerView.setAdapter(adapter);
            updateTouchListenerSwipeOptions(currentFolder);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // display loader only if another is off or selection state active
            if (isMainProgressLoaderVisible() || adapter.getSelectionStateValue()) {
                binding.swipeRefreshLayout.setRefreshing(false);
                return;
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
        binding.bannerLayout.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.url_top_banner)))));

        bindTouchListener();
        adapter.getSelectionState().observe(getViewLifecycleOwner(), this::handleSelectableStateChange);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestNewMessages();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (adapter.getSelectionStateValue()) {
            super.onPrepareOptionsMenu(menu);
            return;
        }
        MenuItem filterIcon = menu.findItem(R.id.action_filter);
        if (filterIcon == null) {
            Timber.e("filterIcon is null");
            super.onPrepareOptionsMenu(menu);
            return;
        }
        if (searchMessages == null) {
            filterIcon.setIcon(R.drawable.ic_action_filter_off);
        } else {
            filterIcon.setIcon(R.drawable.ic_action_filter_on);
        }
        MenuItem emptyFolder = menu.findItem(R.id.action_empty_folder);
        if (currentFolder != null) {
            boolean inTrash = currentFolder.equals(TRASH);
            boolean inSpam = currentFolder.equals(SPAM);
            boolean inDraft = currentFolder.equals(DRAFT);
            emptyFolder.setVisible((inTrash || inSpam || inDraft) && adapterIsNotEmpty());
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NonNull MenuInflater inflater) {
        if (adapter.getSelectionStateValue()) {
            inflater.inflate(R.menu.main_selectable, menu);
            return;
        }
        inflater.inflate(R.menu.main, menu);
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
                    if (searchMessages != null) {
                        searchMessages.setQuery(text);
                    }
                    if (searchDialogFragment != null) {
                        searchDialogFragment.setSearchText(text);
                    }
                    adapter.filter(text);
                    if (TextUtils.isEmpty(text)) {
                        requestNewMessages();
                    }
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
                if (!searchDialogFragment.isAdded()) {
                    searchDialogFragment.setSearchText(getSearchViewText());
                    searchDialogFragment.show(getParentFragmentManager(), null);
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
            case R.id.action_move:
                moveMessages(adapter.getSelectedMessagesMap());
                return true;
            case R.id.action_delete:
                removeMessages(adapter.getSelectedMessagesMap());
                return true;
            case R.id.action_select_all:
                adapter.selectAll();
                return true;
            case R.id.action_mark_as_read:
                mainModel.markMessagesAsRead(adapter.getSelectedMessages(), true);
                adapter.markAsReadMessages(adapter.getSelectedMessagesMap().values());
                adapter.setSelectionState(false);
                return true;
            case android.R.id.home:
                if (adapter.getSelectionStateValue()) {
                    adapter.setSelectionState(false);
                    return true;
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleSelectableStateChange(boolean selectableActive) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar == null) {
            Timber.e("actionBar is null");
            return;
        }
        if (selectableActive) {
            int menuColor = ContextCompat.getColor(activity, R.color.secondaryTextColor);
            Drawable menuDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_close);
            if (menuDrawable != null) {
                DrawableCompat.setTint(menuDrawable, menuColor);
            }
            actionBar.setHomeAsUpIndicator(menuDrawable);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            int menuColor = ContextCompat.getColor(activity, R.color.secondaryTextColor);
            Drawable menuDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_drawer_menu);
            if (menuDrawable != null) {
                DrawableCompat.setTint(menuDrawable, menuColor);
            }
            actionBar.setHomeAsUpIndicator(menuDrawable);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        invalidateOptionsMenu();
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
        if (isLoadingNewMessages || adapter.getSelectionStateValue()) {
            // return if loading new messages or selection state active
            return;
        }
        currentFolder = mainModel.getCurrentFolder().getValue();
        if (currentFolder == null) {
            Timber.e("RequestNextMessages: current folder is null");
            return;
        }

        if (searchMessages != null) {
            mainModel.searchMessages(searchMessages, REQUEST_MESSAGES_COUNT, currentOffset);
        } else if (EditTextUtils.isNotEmpty(getSearchViewText())) {
            mainModel.searchMessages(getSearchViewText(), REQUEST_MESSAGES_COUNT, currentOffset);
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
            ToastUtils.showToast(getActivity(), R.string.error_connection);
        }
        requestNewMessages();
    }

    private void handleDeleteMessagesStatus(ResponseStatus responseStatus) {
        if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
            ToastUtils.showToast(getActivity(), R.string.operation_failed);
        }
    }

    private void handleToFolderStatus(ResponseStatus responseStatus) {
        if (responseStatus == ResponseStatus.RESPONSE_ERROR) {
            ToastUtils.showToast(getActivity(), R.string.operation_failed);
        }
    }

    private void updateTouchListenerSwipeOptions(String folder) {
        if (DRAFT.equals(folder)) {
            touchListener.setSwipeOptionViews(R.id.item_message_view_holder_delete);
        } else if (SPAM.equals(folder)) {
            touchListener.setSwipeOptionViews(R.id.item_message_view_holder_inbox,
                    R.id.item_message_view_holder_move, R.id.item_message_view_holder_delete);
        } else {
            touchListener.setSwipeOptionViews(R.id.item_message_view_holder_spam,
                    R.id.item_message_view_holder_move, R.id.item_message_view_holder_delete);
        }
        touchListener.invalidateSwipeOptions();
    }

    private void bindTouchListener() {
        touchListener = new InboxMessagesTouchListener(getActivity(), binding.recyclerView);
        updateTouchListenerSwipeOptions(currentFolder);
        touchListener.setSwipeable(R.id.foreground_layout, R.id.background_layout, (viewID, position) -> {
            final String currentFolderFinal = currentFolder;
            switch (viewID) {
                case R.id.item_message_view_holder_delete:
                    MessageProvider deletedMessage = adapter.get(position);
                    removeMessages(Collections.singletonMap(position, deletedMessage));
                    break;
                case R.id.item_message_view_holder_spam:
                    MessageProvider spamMessage = adapter.removeAt(position);
                    mainModel.toFolder(spamMessage.getId(), SPAM);
                    showRestoreSnackBar(getString(R.string.action_spam), () -> {
                        mainModel.toFolder(spamMessage.getId(), currentFolderFinal);
                        if (currentFolder.equals(currentFolderFinal)) {
                            adapter.restoreMessages(Collections.singletonMap(position,
                                    spamMessage));
                        }
                    });
                    break;
                case R.id.item_message_view_holder_inbox:
                    MessageProvider inboxMessage = adapter.removeAt(position);
                    mainModel.toFolder(inboxMessage.getId(), INBOX);
                    showRestoreSnackBar(getString(R.string.action_moved_to_inbox), () -> {
                        mainModel.toFolder(inboxMessage.getId(), currentFolderFinal);
                        if (currentFolder.equals(currentFolderFinal)) {
                            adapter.restoreMessages(Collections.singletonMap(position,
                                    inboxMessage));
                        }
                    });
                    break;
                case R.id.item_message_view_holder_move:
                    MessageProvider movedMessage = adapter.get(position);
                    moveMessages(Collections.singletonMap(position, movedMessage));
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

    private void removeMessages(Map<Integer, MessageProvider> selectedMessages) {
        String currentFolderFinal = currentFolder;
        Long[] selectedMessageIds = new Long[selectedMessages.size()];
        int i = 0;
        for (Iterator<MessageProvider> it = selectedMessages.values().iterator(); it.hasNext(); ++i) {
            selectedMessageIds[i] = it.next().getId();
        }
        String messagesCount = String.valueOf(selectedMessageIds.length);
        adapter.removeMessages(selectedMessages.values());
        if (currentFolderFinal.equals(TRASH) || currentFolderFinal.equals(SPAM)) {
            showDeleteSnackBar(getString(R.string.txt_name_removed, messagesCount), () -> {
                if (currentFolder.equals(currentFolderFinal)) {
                    adapter.restoreMessages(selectedMessages);
                }
            }, () -> mainModel.deleteMessages(selectedMessageIds));
        } else {
            mainModel.toFolder(selectedMessageIds, TRASH);
            showRestoreSnackBar(getString(R.string.txt_name_removed, messagesCount), () -> {
                if (currentFolder.equals(currentFolderFinal)) {
                    adapter.restoreMessages(selectedMessages);
                }
                mainModel.toFolder(selectedMessageIds, currentFolderFinal);
            });
        }
        mainThreadExecutor.execute(() -> adapter.setSelectionState(false));
    }

    private void moveMessages(Map<Integer, MessageProvider> selectedMessages) {
        long[] selectedMessageIds = new long[selectedMessages.size()];
        int i = 0;
        for (Iterator<MessageProvider> it = selectedMessages.values().iterator(); it.hasNext(); ++i) {
            selectedMessageIds[i] = it.next().getId();
        }
        Bundle moveMessagesBundle = new Bundle();
        moveMessagesBundle.putLongArray(MESSAGE_IDS, selectedMessageIds);
        MoveDialogFragment moveDialogFragment = new MoveDialogFragment();
        moveDialogFragment.setArguments(moveMessagesBundle);
        moveDialogFragment.setOnMoveCallback(folderName -> {
            adapter.removeMessages(selectedMessages.values());
            mainThreadExecutor.execute(() -> adapter.setSelectionState(false));
        });
        moveDialogFragment.show(getParentFragmentManager(), "MoveDialogFragment");
    }

    private void showRestoreSnackBar(String message, Runnable undoClick) {
        Snackbar.make(binding.sendButtonLayout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), view -> undoClick.run())
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }

    private void showDeleteSnackBar(String message, Runnable undoClick, Runnable notDismissed) {
        Snackbar.make(binding.sendButtonLayout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), view -> undoClick.run())
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            notDismissed.run();
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
        adapter.clearFilter();
        isLoadingNewMessages = false;
        invalidateOptionsMenu();
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
        adapter.clearFilter();
        isLoadingNewMessages = false;
        invalidateOptionsMenu();
        showResultIfNotEmpty(true);
    }

    private void decryptSubjects(List<MessageProvider> messages) {
        mainModel.decryptSubjects(messages, (message)
                -> mainThreadExecutor.execute(() -> adapter.onItemUpdated(message)));
    }

    private void decryptSubject(MessageProvider message) {
        decryptSubjects(Collections.singletonList(message));
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

    private void showResultIfNotEmpty(boolean isServerSearchResult) {
        if (adapterIsNotEmpty()) {
            showMessagesList();
            return;
        }
        if (searchMessages != null) {
            showFilteredMessagesListEmptyIcon();
            return;
        }
        if (EditTextUtils.isNotEmpty(getSearchViewText()) || isServerSearchResult) {
            showSearchMessagesListEmptyIcon();
            return;
        }
        showMessagesListEmptyIcon();
    }

    private String getSearchViewText() {
        if (searchView == null) {
            return "";
        }
        return searchView.getQuery().toString().trim();
    }

    private void displayFilteredCategories() {
        List<String> filteredBy = new ArrayList<>();
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
