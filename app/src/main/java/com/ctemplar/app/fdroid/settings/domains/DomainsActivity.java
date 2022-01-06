package com.ctemplar.app.fdroid.settings.domains;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityCustomDomainsBinding;
import com.ctemplar.app.fdroid.net.request.domains.UpdateDomainRequest;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainDTO;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainsDTO;
import com.ctemplar.app.fdroid.utils.ThemeUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;

public class DomainsActivity extends BaseActivity implements DomainsAdapter.ItemClickListener {
    private ActivityCustomDomainsBinding binding;

    private DomainsViewModel domainsViewModel;
    private DomainsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityCustomDomainsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        domainsViewModel = new ViewModelProvider(this).get(DomainsViewModel.class);
        adapter = new DomainsAdapter(domainsViewModel.getAddresses());
        adapter.setItemClickListener(this);
        binding.domainsRecyclerView.setAdapter(adapter);
        domainsViewModel.getCustomDomains().observe(this, this::handleCustomDomains);
        binding.addNewDomainButton.setOnClickListener(v -> {
            startActivity(new Intent(this, DomainActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        customDomainRequest();
    }

    private void handleCustomDomains(DTOResource<CustomDomainsDTO> dtoResource) {
        binding.progressBar.setVisibility(View.GONE);
        if (!dtoResource.isSuccess()) {
            ToastUtils.showToast(this, dtoResource.getError());
            return;
        }
        CustomDomainDTO[] customDomains = dtoResource.getDto().getResults();
        binding.emptyListImageView.setVisibility(customDomains.length > 0 ? View.GONE : View.VISIBLE);
        adapter.setItems(customDomains);
    }

    @Override
    public void onCatchAll(int domainId, boolean catchAll) {
        updateCustomDomain(domainId, new UpdateDomainRequest(catchAll));
    }

    @Override
    public void onCatchAllEmail(int domainId, String catchAllEmail) {
        updateCustomDomain(domainId, new UpdateDomainRequest(catchAllEmail));
    }

    private void updateCustomDomain(int domainId, UpdateDomainRequest request) {
        domainsViewModel.updateCustomDomain(domainId, request)
                .observe(this, domainDTO -> {
                    if (domainDTO != null) {
                        ToastUtils.showToast(this, R.string.saved_successfully);
                    }
                });
    }

    @Override
    public void onDelete(int domainId, String domainName) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_domain_title, domainName))
                .setMessage(R.string.delete_domain_dsecription)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteCustomDomain(domainId))
                .setNeutralButton(R.string.btn_cancel, null)
                .show();
    }

    private void deleteCustomDomain(int domainId) {
        domainsViewModel.deleteCustomDomain(domainId).observe(this, booleanDTOResource -> {
            if (!booleanDTOResource.isSuccess()) {
                ToastUtils.showToast(this, booleanDTOResource.getError());
                return;
            }
            customDomainRequest();
        });
    }

    private void customDomainRequest() {
        domainsViewModel.customDomainsRequest();
        binding.progressBar.setVisibility(View.VISIBLE);
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
