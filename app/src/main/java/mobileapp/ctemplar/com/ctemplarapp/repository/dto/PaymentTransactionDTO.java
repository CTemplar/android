package mobileapp.ctemplar.com.ctemplarapp.repository.dto;

import java.util.Date;

import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.PaymentTransactionResponse;

public class PaymentTransactionDTO {
    private long id;
    private boolean isDeleted;
    private Date deletedAt;
    private String transactionId;
    private String originalTransactionId;
    private long amount;
    private String currency;
    private Object stripePlan;
    private String purchasedPlan;
    private Date created;
    private Date billingCycleEnds;
    private String paymentMethod;
    private String paymentType;
    private boolean isRefund;

    public PaymentTransactionDTO() {
    }

    public PaymentTransactionDTO(long id, boolean isDeleted, Date deletedAt, String transactionId, String originalTransactionId, long amount, String currency, Object stripePlan, String purchasedPlan, Date created, Date billingCycleEnds, String paymentMethod, String paymentType, boolean isRefund) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.transactionId = transactionId;
        this.originalTransactionId = originalTransactionId;
        this.amount = amount;
        this.currency = currency;
        this.stripePlan = stripePlan;
        this.purchasedPlan = purchasedPlan;
        this.created = created;
        this.billingCycleEnds = billingCycleEnds;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.isRefund = isRefund;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Object getStripePlan() {
        return stripePlan;
    }

    public void setStripePlan(Object stripePlan) {
        this.stripePlan = stripePlan;
    }

    public String getPurchasedPlan() {
        return purchasedPlan;
    }

    public void setPurchasedPlan(String purchasedPlan) {
        this.purchasedPlan = purchasedPlan;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getBillingCycleEnds() {
        return billingCycleEnds;
    }

    public void setBillingCycleEnds(Date billingCycleEnds) {
        this.billingCycleEnds = billingCycleEnds;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setRefund(boolean refund) {
        isRefund = refund;
    }

    public static PaymentTransactionDTO fromResponse(PaymentTransactionResponse response) {
        return new PaymentTransactionDTO(
                response.getId(),
                response.isDeleted(),
                response.getDeletedAt(),
                response.getTransactionId(),
                response.getOriginalTransactionId(),
                response.getAmount(),
                response.getCurrency(),
                response.getStripePlan(),
                response.getPurchasedPlan(),
                response.getCreated(),
                response.getBillingCycleEnds(),
                response.getPaymentMethod(),
                response.getPaymentType(),
                response.isRefund()
        );
    }
}
