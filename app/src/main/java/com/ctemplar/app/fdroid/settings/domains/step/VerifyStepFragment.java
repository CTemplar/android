package com.ctemplar.app.fdroid.settings.domains.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.databinding.FragmentDomainVerifyStepBinding;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainDTO;
import com.ctemplar.app.fdroid.repository.dto.domains.DomainRecordDTO;
import com.ctemplar.app.fdroid.utils.AppUtils;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class VerifyStepFragment extends StepFragment {
    private FragmentDomainVerifyStepBinding binding;
    private boolean domainVerified = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDomainVerifyStepBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.nextButton.setOnClickListener(v -> {
            if (domainVerified) {
                next();
            } else {
                verify();
            }
            binding.failedDomainVerificationTextView.setVisibility(domainVerified ? View.GONE : View.VISIBLE);
        });
        binding.valueDataPointsToTextView.setOnClickListener(v -> AppUtils.setSystemClipboard(
                getActivity(), EditTextUtils.getText(binding.valueDataPointsToTextView)));
        binding.failedDomainVerificationTextView.setVisibility(View.GONE);
    }

    @Override
    protected void onDomain(CustomDomainDTO domain) {
        super.onDomain(domain);
        DomainRecordDTO verificationRecord = domain.getVerificationRecord();
        domainVerified = domain.isDomainVerified();
        binding.typeTextView.setText(verificationRecord.getType());
        binding.hostNameTextView.setText(verificationRecord.getHost());
        binding.valueDataPointsToTextView.setText(verificationRecord.getValue());
    }
}
