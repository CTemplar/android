package mobileapp.ctemplar.com.ctemplarapp.contacts;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.main.MainActivityViewModel;

public class ContactFragment extends BaseFragment {

    @BindView(R.id.fragment_contact_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_contact_icon_empty)
    ImageView imgEmpty;

    @BindView(R.id.fragment_contact_title_empty)
    TextView txtEmpty;

    @BindView(R.id.fragment_contact_add_layout)
    FrameLayout frameCompose;

    @BindView(R.id.fragment_contact_search)
    SearchView searchView;

    private ContactAdapter adapter;

    private MainActivityViewModel mainModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainModel.getContacts(200, 0);
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

        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        mainModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mainModel.getContactsResponse()
                .observe(this, new Observer<List<Contact>>() {
                    @Override
                    public void onChanged(@Nullable List<Contact> contactsList) {
                        handleContactsList(contactsList);
                    }
                });
        mainModel.getContacts(200, 0);
    }

    private void handleContactsList(@Nullable List<Contact> contacts) {
        if (contacts == null || contacts.size() == 0) {
            return;
        }

        imgEmpty.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        frameCompose.setVisibility(View.GONE);

        List<Contact> contactsList = new LinkedList<>();
        contactsList.addAll(contacts);

        adapter = new ContactAdapter(contactsList);
        adapter.getOnClickSubject()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
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

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        recyclerView.setAdapter(adapter);
    }

    private void setupSwiperForRecyclerView() {
        RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final ContactAdapter adapter = ContactFragment.this.adapter;
                if (adapter == null) {
                    return;
                }
                final int deletedIndex = viewHolder.getAdapterPosition();
                final Contact deletedContact =  adapter.removeAt(deletedIndex);
                final String name = deletedContact.getName();
                String removedTxt = getResources().getString(R.string.txt_name_removed, name);
                Snackbar snackbar = Snackbar
                        .make(frameCompose, removedTxt, Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(deletedContact, deletedIndex);
                    }
                });
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_contact:
                startAddContactActivity();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @OnClick(R.id.fragment_contact_add_layout)
    public void onClickAdd() {
        startAddContactActivity();
    }

    private void startAddContactActivity() {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        startActivity(intent);
    }
}