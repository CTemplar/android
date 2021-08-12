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

    @SerializedName("amount")
    private long amount;

    @SerializedName("currency")
    private String currency;

    @SerializedName("purchased_plan")
    private String purchasedPlan;

    @SerializedName("created")
    private Date created;

    @SerializedName("billing_cycle_ends")
    private Date billingCycleEnds;

    @SerializedName("bitcoin")
    private String bitcoin;

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

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
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

    public String getBitcoin() {
        return bitcoin;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public boolean isRefund() {
        return isRefund;
    }
}
