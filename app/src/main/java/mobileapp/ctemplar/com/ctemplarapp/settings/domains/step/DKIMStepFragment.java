package mobileapp.ctemplar.com.ctemplarapp.settings.domains.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDomainDkimStepBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.DomainRecordDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class DKIMStepFragment extends StepFragment {
    private FragmentDomainDkimStepBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDomainDkimStepBinding.inflate(inflater, container, false);
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
        DomainRecordDTO dkimRecord = domain.getDkimRecord();
        binding.typeTextView.setText(dkimRecord.getType());
        binding.hostNameTextView.setText(dkimRecord.getHost());
        binding.valueDataPointsToTextView.setText(dkimRecord.getValue());
    }
}
