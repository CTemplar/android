package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PaymentTransactionResponse {
    @SerializedName("id")
    private long id;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("deleted_at")
    private Date deletedAt;

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("original_transaction_id")
    private String originalTransactionId;

    @SerializedName("amount")
    private long amount;

    @SerializedName("currency")
    private String currency;

    @SerializedName("stripe_plan")
    private Object stripePlan;

    @SerializedName("purchased_plan")
    private String purchasedPlan;

    @SerializedName("created")
    private Date created;

    @SerializedName("billing_cycle_ends")
    private Date billingCycleEnds;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_type")
    private String paymentType;

    @SerializedName("is_refund")
    private boolean isRefund;


    public long getId() {
        return id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Object getStripePlan() {
        return stripePlan;
    }

    public String getPurchasedPlan() {
        return purchasedPlan;
    }

    public Date getCreated() {
        return created;
    }

    public Date getBillingCycleEnds() {
        return billingCycleEnds;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public boolean isRefund() {
        return isRefund;
    }
}
