package mobileapp.ctemplar.com.ctemplarapp.billing.model;

import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PaymentTransactionDTO;

public class CurrentPlanData {
    private final PlanType planType;
    private final PaymentTransactionDTO paymentTransactionDTO;

    public CurrentPlanData(PlanType planType, PaymentTransactionDTO paymentTransactionDTO) {
        this.planType = planType;
        this.paymentTransactionDTO = paymentTransactionDTO;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public PaymentTransactionDTO getPaymentTransactionDTO() {
        return paymentTransactionDTO;
    }
}
