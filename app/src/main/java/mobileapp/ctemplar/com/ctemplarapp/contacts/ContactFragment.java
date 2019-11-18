package mobileapp.ctemplar.com.ctemplarapp.contacts;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;
import timber.log.Timber;

public class ContactFragment extends BaseFragment {

    private MainActivityViewModel mainModel;
    private ContactAdapter contactAdapter;

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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        setupSwiperForRecyclerView();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (contactAdapter != null) {
                    contactAdapter.filter(newText);
                }
                return false;
            }
        });

        mainModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mainModel.getContactsResponse()
                .observe(this, this::handleContactsList);
        mainModel.getContacts(200, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainModel.getContacts(200, 0);
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

        contactAdapter = new ContactAdapter(contactList);
        contactAdapter.getOnClickSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long id) {
                        Activity activity = getActivity();
                        if (activity != null) {
                            Intent intent = new Intent(activity, EditContactActivity.class);
                            intent.putExtra(EditContactActivity.ARG_ID, id);
                            activity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        recyclerView.setAdapter(contactAdapter);
    }

    private void setupSwiperForRecyclerView() {
        RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final ContactAdapter adapter = ContactFragment.this.contactAdapter;
                if (adapter == null) {
                    return;
                }
                final int deletedIndex = viewHolder.getAdapterPosition();
                final Contact deletedContact =  adapter.removeAt(deletedIndex);
                final String name = deletedContact.getName();
                String removedTxt = getResources().getString(R.string.txt_name_removed, name);
                Snackbar snackbar = Snackbar
                        .make(frameCompose, removedTxt, Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.action_undo), view -> adapter.restoreItem(deletedContact, deletedIndex));
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            mainModel.deleteContact(deletedContact);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_contact) {
            startAddContactActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @OnClick(R.id.fragment_contact_add_layout)
    void onClickAdd() {
        startAddContactActivity();
    }

    private void startAddContactActivity() {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        startActivity(intent);
    }
}
