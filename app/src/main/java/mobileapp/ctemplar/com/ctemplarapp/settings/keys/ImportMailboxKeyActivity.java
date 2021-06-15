package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityMailboxKeyImportBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;

public class ImportMailboxKeyActivity extends AppCompatActivity {
    private ActivityMailboxKeyImportBinding binding;
    private MailboxKeyViewModel mailboxKeyViewModel;

    private Map<MailboxEntity, List<GeneralizedMailboxKey>> mailboxKeyMap;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityMailboxKeyImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mailboxKeyViewModel = new ViewModelProvider(this).get(MailboxKeyViewModel.class);

        mailboxKeyMap = mailboxKeyViewModel.getMailboxKeyMap();
        if (mailboxKeyMap == null || mailboxKeyMap.isEmpty()) {
            onBackPressed();
            return;
        }
        String[] addresses = new String[mailboxKeyMap.size()];
        int iterator = 0;
        for (MailboxEntity mailbox : mailboxKeyMap.keySet()) {
            addresses[iterator++] = mailbox.getEmail();
        }
        SpinnerAdapter addressAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_domain_spinner,
                addresses
        );
        binding.emailSpinner.setAdapter(addressAdapter);
        binding.emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
