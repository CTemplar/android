package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FilterResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FiltersResponse;

public class FiltersActivity extends BaseActivity {
    private FiltersViewModel filtersModel;
    private FiltersAdapter filtersAdapter;

    @BindView(R.id.activity_filters_recycler_view)
    RecyclerView filtersRecyclerView;

    @BindView(R.id.filters_footer_btn)
    Button addFilterButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_filters;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filtersModel = ViewModelProviders.of(this).get(FiltersViewModel.class);

        Toolbar toolbar = findViewById(R.id.activity_filters_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, layoutManager.getOrientation());
        filtersRecyclerView.addItemDecoration(dividerItemDecoration);
        filtersModel.getFiltersResponse().observe(this, new Observer<FiltersResponse>() {
            @Override
            public void onChanged(@Nullable FiltersResponse filtersResponse) {
                handleFiltersResponse(filtersResponse);
            }
        });
        filtersModel.getDeleteFilterResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                handleFilterDeletingStatus(responseStatus);
            }
        });
        getFilters();
        setupSwiperForFilterList();
    }

    private void handleFilterDeletingStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_filter_not_deleted), Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_filter_deleted), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFiltersResponse(FiltersResponse filtersResponse) {
        if (filtersResponse == null) {
            return;
        }
        List<FilterResult> filterList = filtersResponse.getFilterResultList();
        filtersAdapter = new FiltersAdapter(filterList);
        filtersRecyclerView.setAdapter(filtersAdapter);
    }

    private void getFilters() {
        filtersModel.getFilters();
    }

    private void setupSwiperForFilterList() {
        RecycleDeleteSwiper swipeHandler = new RecycleDeleteSwiper(this) {
            @Override
            public void onSwiped(final @NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final FiltersAdapter adapter = filtersAdapter;
                if (adapter == null) {
                    return;
                }

                final int deletedIndex = viewHolder.getAdapterPosition();
                final FilterResult deletedFilter = adapter.removeAt(deletedIndex);
                new AlertDialog.Builder(FiltersActivity.this)
                        .setTitle(getString(R.string.txt_delete_filter_quest_title))
                        .setMessage(getString(R.string.txt_delete_filter_quest_message))
                        .setPositiveButton(getString(R.string.btn_contact_delete), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long filterId = deletedFilter.getId();
                                        filtersModel.deleteFilter(filterId);
                                    }
                                }
                        )
                        .setNeutralButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.restoreItem(deletedIndex, deletedFilter);
                            }
                        })
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(filtersRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFilters();
    }

    @OnClick(R.id.filters_footer_btn)
    public void onClickAddFilter() {
        Intent addFilterIntent = new Intent(this, AddFilterActivity.class);
        startActivity(addFilterIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
