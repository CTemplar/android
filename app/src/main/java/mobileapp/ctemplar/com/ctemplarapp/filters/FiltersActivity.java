package mobileapp.ctemplar.com.ctemplarapp.filters;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.main.RecycleDeleteSwiper;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.FilterResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.FiltersResponse;
import timber.log.Timber;

public class FiltersActivity extends BaseActivity {
    private final FiltersAdapter filtersAdapter = new FiltersAdapter();
    private FiltersViewModel filtersModel;

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
        filtersModel = new ViewModelProvider(this).get(FiltersViewModel.class);

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
        filtersModel.getFiltersResponse().observe(this, filtersResponse -> {
            if (filtersResponse != null) {
                handleFiltersResponse(filtersResponse);
            }
        });
        filtersModel.getDeleteFilterResponseStatus().observe(this, this::handleFilterDeletingStatus);
        addFilterButton.setOnClickListener(v -> addFilter());
        getFilters();
        setupSwiperForFilterList();
    }

    private void handleFilterDeletingStatus(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(getApplicationContext(), R.string.txt_filter_not_deleted, Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            Toast.makeText(getApplicationContext(), R.string.txt_filter_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFiltersResponse(FiltersResponse filtersResponse) {
        List<FilterResult> filterList = filtersResponse.getFilterResultList();
        filtersRecyclerView.setAdapter(filtersAdapter);
        filtersAdapter.setItems(filterList);
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
                    Timber.w("Adapter is null");
                    return;
                }
                final int deletedIndex = viewHolder.getAdapterPosition();
                final FilterResult deletedFilter = adapter.removeAt(deletedIndex);
                new AlertDialog.Builder(FiltersActivity.this)
                        .setTitle(R.string.txt_delete_filter_quest_title)
                        .setMessage(R.string.txt_delete_filter_quest_message)
                        .setPositiveButton(R.string.btn_contact_delete, (dialog, which) -> {
                                    long filterId = deletedFilter.getId();
                                    filtersModel.deleteFilter(filterId);
                                }
                        )
                        .setNeutralButton(R.string.btn_cancel, (dialog, which)
                                -> adapter.restoreItem(deletedIndex, deletedFilter))
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

    public void addFilter() {
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
