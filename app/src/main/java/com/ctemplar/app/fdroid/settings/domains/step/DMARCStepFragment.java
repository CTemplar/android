package com.ctemplar.app.fdroid.settings.domains.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.databinding.FragmentDomainDmarcStepBinding;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainDTO;
import com.ctemplar.app.fdroid.repository.dto.domains.DomainRecordDTO;
import com.ctemplar.app.fdroid.utils.AppUtils;
import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class DMARCStepFragment extends StepFragment {
    private FragmentDomainDmarcStepBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDomainDmarcStepBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.nextButton.setOnClickListener(v -> next());
        binding.valueDataPointsToTextView.setOnClickListener(v -> AppUtils.setSystemClipboard(
                getActivity(), EditTextUtils.getText(binding.valueDataPointsToTextView)));
    }

    @Override
    protected void onDomain(CustomDomainDTO domain) {
        super.onDomain(domain);
        DomainRecordDTO dmarcRecord = domain.getDmarcRecord();
        binding.typeTextView.setText(dmarcRecord.getType());
        binding.hostNameTextView.setText(dmarcRecord.getHost());
        binding.valueDataPointsToTextView.setText(dmarcRecord.getValue());
    }
}
