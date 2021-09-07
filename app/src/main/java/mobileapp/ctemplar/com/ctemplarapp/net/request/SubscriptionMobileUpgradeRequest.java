package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class SubscriptionMobileUpgradeRequest {
    @SerializedName("customer_identifier")
    private String customerIdentifier;

    @SerializedName("subscription_id")
    private String subscriptionId;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_type")
    private String paymentType;

    @SerializedName("plan_type")
    private String planType;

    public SubscriptionMobileUpgradeRequest() {
    }

    public SubscriptionMobileUpgradeRequest(String customerIdentifier, String subscriptionId, String paymentMethod, String paymentType, String planType) {
        this.customerIdentifier = customerIdentifier;
        this.subscriptionId = subscriptionId;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.planType = planType;
    }

    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }
}
