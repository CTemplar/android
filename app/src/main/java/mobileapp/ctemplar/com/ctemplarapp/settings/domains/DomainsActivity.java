package mobileapp.ctemplar.com.ctemplarapp.settings.domains;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityDomainsBinding;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityMailboxesBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainsDTO;
import mobileapp.ctemplar.com.ctemplarapp.settings.mailboxes.MailboxesViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;

public class DomainsActivity extends BaseActivity {
    private ActivityDomainsBinding binding;

    private DomainsViewModel domainsViewModel;
    private DomainsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityDomainsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        domainsViewModel = new ViewModelProvider(this).get(DomainsViewModel.class);
        adapter = new DomainsAdapter();
        binding.domainsRecyclerView.setAdapter(adapter);
        domainsViewModel.customDomainsRequest();
        domainsViewModel.getCustomDomains().observe(this,
                dto -> adapter.setItems(dto.getResults()));
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
