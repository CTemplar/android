package mobileapp.ctemplar.com.ctemplarapp.settings.domains.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDomainNameStepBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.settings.domains.DomainsViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class DomainNameStepFragment extends StepFragment {
    private FragmentDomainNameStepBinding binding;

    private DomainsViewModel domainsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDomainNameStepBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        domainsViewModel = new ViewModelProvider(this).get(DomainsViewModel.class);
        binding.nextButton.setOnClickListener(v -> {
            domainsViewModel.createCustomDomain(EditTextUtils.getText(binding.domainNameEditText))
                    .observe(getActivity(), this::handleDomainCreateResponse);
            showProgressBar(true);
        });
        binding.failedDomainNameTextView.setVisibility(View.GONE);
    }

    private void handleDomainCreateResponse(DTOResource<CustomDomainDTO> domainDTOResource) {
        showProgressBar(false);
        if (!domainDTOResource.isSuccess()) {
            binding.failedDomainNameTextView.setText(domainDTOResource.getError());
            binding.failedDomainNameTextView.setVisibility(View.VISIBLE);
            return;
        }
        binding.domainNameEditText.setText(null);
        binding.failedDomainNameTextView.setVisibility(View.GONE);
        domainCreated(domainDTOResource.getDto());
    }

    private void showProgressBar(boolean active) {
        binding.emptySpaceView.setVisibility(active ? View.GONE : View.VISIBLE);
        binding.progressBarLayout.setVisibility(active ? View.VISIBLE : View.GONE);
    }
}
