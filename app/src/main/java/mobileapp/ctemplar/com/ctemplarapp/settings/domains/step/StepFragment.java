package mobileapp.ctemplar.com.ctemplarapp.settings.domains.step;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;

public abstract class StepFragment extends Fragment {
    protected CustomDomainDTO domain;
    private StepActionListener listener;

    public void setListener(StepActionListener listener) {
        this.listener = listener;
    }

    public final void setDomain(CustomDomainDTO domain) {
        this.domain = domain;
        if (getView() != null) {
            onDomain(domain);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (domain != null) {
            onDomain(domain);
        }
    }

    protected void onDomain(CustomDomainDTO domain) {

    }

    public void verify() {
        if (listener != null) {
            listener.onVerifyStepClick();
        }
    }

    public void next() {
        if (listener != null) {
            listener.onNextStepClick();
        }
    }

    public void domainCreated(CustomDomainDTO domainDTO) {
        if (listener != null) {
            listener.onDomainCreated(domainDTO);
        }
    }

    public interface StepActionListener {
        void onVerifyStepClick();
        void onNextStepClick();
        void onDomainCreated(CustomDomainDTO domainDTO);
    }
}
