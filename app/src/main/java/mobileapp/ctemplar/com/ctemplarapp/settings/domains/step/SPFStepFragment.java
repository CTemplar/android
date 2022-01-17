package mobileapp.ctemplar.com.ctemplarapp.settings.domains.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDomainSpfStepBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.DomainRecordDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class SPFStepFragment extends StepFragment {
    private FragmentDomainSpfStepBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDomainSpfStepBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.verifyButton.setOnClickListener(v -> verify());
        binding.nextButton.setOnClickListener(v -> next());
        binding.valueDataPointsToTextView.setOnClickListener(v -> AppUtils.setSystemClipboard(
                getActivity(), EditTextUtils.getText(binding.valueDataPointsToTextView)));
    }

    @Override
    protected void onDomain(CustomDomainDTO domain) {
        super.onDomain(domain);
        DomainRecordDTO spfRecord = domain.getSpfRecord();
        binding.typeTextView.setText(spfRecord.getType());
        binding.hostNameTextView.setText(spfRecord.getHost());
        binding.valueDataPointsToTextView.setText(spfRecord.getValue());
    }
}
