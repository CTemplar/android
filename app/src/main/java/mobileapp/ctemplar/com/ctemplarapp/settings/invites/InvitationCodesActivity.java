package mobileapp.ctemplar.com.ctemplarapp.settings.invites;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivityInvitationCodesBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PagableDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.invites.InviteCodeDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;

public class InvitationCodesActivity extends BaseActivity {
    private ActivityInvitationCodesBinding binding;
    private InvitationCodesViewModel viewModel;
    private InvitationCodesAdapter invitationCodesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityInvitationCodesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(InvitationCodesViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        invitationCodesAdapter = new InvitationCodesAdapter();
        binding.invitationCodesRecyclerView.setAdapter(invitationCodesAdapter);
        progressBar(false);
        binding.generateCodeButton.setOnClickListener(v -> generateInviteCode());
        viewModel.getInviteCodesLiveData().observe(this, this::handleInviteCodes);
        requestInviteCodes();
    }

    private void handleInviteCodes(DTOResource<PagableDTO<InviteCodeDTO>> resource) {
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        invitationCodesAdapter.setItems(resource.getDto().getResults());
    }

    private void requestInviteCodes() {
        viewModel.getInviteCodes(10, 0);
    }

    private void generateInviteCode() {
        progressBar(true);
        viewModel.generateInviteCode().observe(this, resource -> {
            progressBar(false);
            if (!resource.isSuccess()) {
                ToastUtils.showToast(getApplicationContext(), resource.getError());
                return;
            }
            requestInviteCodes();
        });
    }

    private void progressBar(boolean state) {
        binding.progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
        binding.generateCodeButton.setEnabled(!state);
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
