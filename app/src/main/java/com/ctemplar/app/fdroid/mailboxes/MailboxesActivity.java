package com.ctemplar.app.fdroid.mailboxes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.settings.SettingsActivity;

public class MailboxesActivity extends BaseActivity {
    private MailboxesViewModel mailboxesModel;

    @BindView(R.id.activity_mailboxes_add)
    TextView addMailboxTextView;

    @BindView(R.id.activity_mailboxes_recycler_view)
    RecyclerView mailboxesRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mailboxes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mailboxesModel = new ViewModelProvider(this).get(MailboxesViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            boolean userIsPrime = intent.getBooleanExtra(SettingsActivity.USER_IS_PRIME, false);
            setMailboxAddVisibility(userIsPrime);
        } else {
            setMailboxAddVisibility(false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, layoutManager.getOrientation());
        mailboxesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onResume() {
        List<MailboxEntity> mailboxes = mailboxesModel.getMailboxes();
        MailboxesAdapter mailboxesAdapter = new MailboxesAdapter(mailboxesModel, mailboxes);
        mailboxesRecyclerView.setAdapter(mailboxesAdapter);
        super.onResume();
    }

    @OnClick(R.id.activity_mailboxes_add)
    public void OnClickAddMailbox() {
        Intent addMailboxIntent = new Intent(this, AddMailboxActivity.class);
        startActivity(addMailboxIntent);
    }

    private void setMailboxAddVisibility(boolean state) {
        if (state) {
            addMailboxTextView.setVisibility(View.VISIBLE);
        } else {
            addMailboxTextView.setVisibility(View.GONE);
        }
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
