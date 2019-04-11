package mobileapp.ctemplar.com.ctemplarapp.wbl;

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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.BlackListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.WhiteListContact;

public class WhiteBlackListActivity extends BaseActivity {

    private WhiteBlackListViewModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_black_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.white_black_lists_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        model = ViewModelProviders.of(this).get(WhiteBlackListViewModel.class);

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager pagesContainer = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagesContainer.setAdapter(adapter);
        pagesContainer.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pagesContainer));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public WhiteBlackListViewModel getModel() {
        return model;
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.getWhiteListContacts();
        model.getBlackListContacts();
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                return new WhitelistFragment();
            } else if (i == 1) {
                return new BlacklistFragment();
            } else throw new RuntimeException("Can't find " + i + " fragment for ViewPagerAdapter");
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class WhitelistFragment extends Fragment {
        WhiteBlackListViewModel model;
        WhitelistAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.content_whitelist_tab, container, false);

            final EditText searchView = view.findViewById(R.id.whitelist_search);
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            final RecyclerView recyclerView = view.findViewById(R.id.whitelist_recycler_view);

            WhitelistAdapter adapter = new WhitelistAdapter(new WhiteListContact[]{});
            recyclerView.setAdapter(adapter);
            this.adapter = adapter;

            final Activity activity = getActivity();
            if (activity == null) {
                return view;
            }

            model = ((WhiteBlackListActivity) activity).getModel();
            model.getWhitelistResponse().observe(this, new Observer<WhiteListContact[]>() {
                @Override
                public void onChanged(@Nullable final WhiteListContact[] whiteListContacts) {
                    if (whiteListContacts != null) {
                        WhitelistAdapter adapter = new WhitelistAdapter(whiteListContacts);
                        recyclerView.setAdapter(adapter);
                        WhitelistFragment.this.adapter = adapter;
                        adapter.filter(searchView.getText().toString());
                    }
                }
            });

            setupSwiperForRecyclerView(recyclerView);

            Button addContactToWhiteListButton = view.findViewById(R.id.buttonAddWhitelistContact);
            addContactToWhiteListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddWhitelistContactActivity.class);
                    startActivity(intent);
                }
            });


            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            model.whiteListIsReady();
        }

        private void setupSwiperForRecyclerView(RecyclerView recyclerView) {
            RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(getActivity()) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    final WhitelistAdapter adapter = WhitelistFragment.this.adapter;
                    if (adapter == null) {
                        return;
                    }
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    final WhiteListContact deletedContact = adapter.removeAt(deletedIndex);
                    final String name = deletedContact.name;

                    View view = getView();
                    if(view == null) return;

                    Snackbar snackbar = Snackbar
                            .make(view, name + " removed!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.restoreItem(deletedContact, deletedIndex);
                        }
                    });
                    snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != DISMISS_EVENT_ACTION) {
                                model.deleteWhitelistContact(deletedContact);
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
    }

    public static class BlacklistFragment extends Fragment {
        WhiteBlackListViewModel model;
        BlacklistAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.content_blacklist_tab, container, false);

            final EditText searchView = view.findViewById(R.id.blacklist_search);
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            final RecyclerView recyclerView = view.findViewById(R.id.blacklist_recycler_view);
            BlacklistAdapter adapter = new BlacklistAdapter(new BlackListContact[]{});
            recyclerView.setAdapter(adapter);
            this.adapter = adapter;

            final Activity activity = getActivity();
            if (activity == null) {
                return view;
            }

            model = ((WhiteBlackListActivity) activity).getModel();
            model.getBlacklistResponse().observe(this, new Observer<BlackListContact[]>() {
                @Override
                public void onChanged(@Nullable final BlackListContact[] blackListContacts) {
                    if (blackListContacts != null) {
                        BlacklistAdapter adapter = new BlacklistAdapter(blackListContacts);
                        recyclerView.setAdapter(adapter);
                        BlacklistFragment.this.adapter = adapter;
                        adapter.filter(searchView.getText().toString());
                    }
                }
            });

            setupSwiperForRecyclerView(recyclerView);

            Button addContactToBlackListButton = view.findViewById(R.id.buttonAddBlacklistContact);
            addContactToBlackListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddBlacklistContactActivity.class);
                    startActivity(intent);
                }
            });

            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            model.blackListIsReady();
        }

        private void setupSwiperForRecyclerView(RecyclerView recyclerView) {
            RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(getActivity()) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    final BlacklistAdapter adapter = BlacklistFragment.this.adapter;
                    if (adapter == null) {
                        return;
                    }
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    final BlackListContact deletedContact = adapter.removeAt(deletedIndex);
                    final String name = deletedContact.name;

                    View view = getView();
                    if(view == null) return;

                    Snackbar snackbar = Snackbar
                            .make(view, name + " removed!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.restoreItem(deletedContact, deletedIndex);
                        }
                    });
                    snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != DISMISS_EVENT_ACTION) {
                                model.deleteBlacklistContact(deletedContact);
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
    }
}