package com.ctemplar.app.fdroid.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import com.ctemplar.app.fdroid.BaseFragment;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.main.RecycleDeleteSwiper;
import com.ctemplar.app.fdroid.repository.entity.Contact;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import timber.log.Timber;

public class ContactsFragment extends BaseFragment {
    @BindView(R.id.fragment_contact_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_contact_add_layout)
    FrameLayout frameCompose;

    @BindView(R.id.fragment_contact_search)
    SearchView searchView;

    @BindView(R.id.fragment_contact_list_empty_layout)
    ConstraintLayout listEmptyLayout;

    @BindView(R.id.fragment_contact_progress_layout)
    ConstraintLayout progressLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    private ContactsViewModel contactsViewModel;
    private ContactsAdapter contactsAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("FragmentActivity is null");
            return;
        }
        setHasOptionsMenu(true);

        contactsViewModel = new ViewModelProvider(activity).get(ContactsViewModel.class);
        contactsAdapter = new ContactsAdapter();
        recyclerView.setAdapter(contactsAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity,
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        setupSwiperForRecyclerView();

        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsAdapter.filter(newText);
                return true;
            }
        });
        frameCompose.setOnClickListener(v -> startAddContactActivity());

        contactsViewModel.getContactsResponse().observe(getViewLifecycleOwner(), this::handleContactsList);
        contactsViewModel.getContacts(200, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsViewModel.getContacts(200, 0);
    }

    private void handleContactsList(@Nullable List<Contact> contactList) {
        if (contactList == null) {
            progressLayout.setVisibility(View.VISIBLE);
            listEmptyLayout.setVisibility(View.GONE);
            return;
        }
        if (contactList.isEmpty()) {
            progressLayout.setVisibility(View.GONE);
            listEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        progressLayout.setVisibility(View.GONE);
        listEmptyLayout.setVisibility(View.GONE);

        contactsAdapter.setItems(contactList, searchView.getQuery().toString());
    }

    private void setupSwiperForRecyclerView() {
        RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int viewType) {
                final int deletedIndex = viewHolder.getBindingAdapterPosition();
                final Contact deletedContact = contactsAdapter.removeAt(deletedIndex);
                Snackbar.make(frameCompose, getString(R.string.txt_name_removed,
                        deletedContact.getName()), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.action_undo), view
                                -> contactsAdapter.restoreItem(deletedContact, deletedIndex))
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    contactsViewModel.deleteContact(deletedContact);
                                }
                            }
                        })
                        .setActionTextColor(Color.YELLOW)
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_contact) {
            startAddContactActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAddContactActivity() {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        startActivity(intent);
    }
}
