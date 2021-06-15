package com.ctemplar.app.fdroid.settings.mailboxes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.databinding.ActivityMailboxesBinding;
import com.ctemplar.app.fdroid.settings.SettingsActivity;
import com.ctemplar.app.fdroid.utils.ThemeUtils;

public class MailboxesActivity extends BaseActivity {
    private ActivityMailboxesBinding binding;
    private MailboxesViewModel mailboxesModel;

    private MailboxesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityMailboxesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mailboxesModel = new ViewModelProvider(this).get(MailboxesViewModel.class);
        adapter = new MailboxesAdapter(mailboxesModel);
        binding.mailboxesRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        setMailboxAddVisibility(intent != null
                && intent.getBooleanExtra(SettingsActivity.USER_IS_PRIME, false));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.mailboxesRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.mailboxesAddTextView.setOnClickListener(v -> onClickAddMailbox());
    }

    @Override
    protected void onResume() {
        adapter.setItems(mailboxesModel.getMailboxes());
        super.onResume();
    }

    public void onClickAddMailbox() {
        startActivity(new Intent(this, AddMailboxActivity.class));
    }

    private void setMailboxAddVisibility(boolean state) {
        binding.mailboxesAddTextView.setVisibility(state ? View.VISIBLE : View.GONE);
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
