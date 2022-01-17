package mobileapp.ctemplar.com.ctemplarapp.settings.domains.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDomainVerifyStepBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.DomainRecordDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

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
