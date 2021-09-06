package mobileapp.ctemplar.com.ctemplarapp.billing.view;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.billing.BillingConstants;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.CurrentPlanData;
import mobileapp.ctemplar.com.ctemplarapp.databinding.MySubscriptionLayoutBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PaymentTransactionDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;

public class MySubscriptionDialog extends DialogFragment {
    public static final String CURRENT_PLAN_DATA = "current_plan_data_key";

    private MySubscriptionLayoutBinding binding;

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MySubscriptionLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundleArguments = getArguments();
        if (bundleArguments == null) {
            dismiss();
            return;
        }
        String currentPlanDataString = bundleArguments.getString(CURRENT_PLAN_DATA);
        if (currentPlanDataString == null) {
            dismiss();
            return;
        }
        CurrentPlanData currentPlanData = GENERAL_GSON.fromJson(currentPlanDataString, CurrentPlanData.class);
        PaymentTransactionDTO paymentTransactionDTO = currentPlanData.getPaymentTransactionDTO();
//
        binding.plansValueTextView.setText(currentPlanData.getPlanType().name());
        binding.billingCycleValueTextView.setText(paymentTransactionDTO.getPaymentType()
                .equals(BillingConstants.MONTHLY) ? R.string.monthly : R.string.yearly);
        binding.paymentTypeValueTextView.setText(paymentTransactionDTO.getPaymentMethod());
        binding.billingCycleEndValueTextView.setText(DateUtils.simpleDate(
                paymentTransactionDTO.getBillingCycleEnds()));
        binding.recurringBillingValueTextView.setText(DateUtils.simpleDate(
                paymentTransactionDTO.getBillingCycleEnds()));
        binding.closeImageView.setOnClickListener(v -> dismiss());
    }
}
