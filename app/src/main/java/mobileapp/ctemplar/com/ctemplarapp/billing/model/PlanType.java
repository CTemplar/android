package mobileapp.ctemplar.com.ctemplarapp.billing.model;

import mobileapp.ctemplar.com.ctemplarapp.billing.BillingConstants;

public enum PlanType {
    FREE(null, null),
    PRIME(BillingConstants.PRIME_MONTHLY_SKU, BillingConstants.PRIME_ANNUAL_SKU),
    KNIGHT(BillingConstants.KNIGHT_MONTHLY_SKU, BillingConstants.KNIGHT_ANNUAL_SKU),
    MARSHAL(BillingConstants.MARSHAL_MONTHLY_SKU, BillingConstants.MARSHAL_ANNUAL_SKU),
    CHAMPION(BillingConstants.CHAMPION_MONTHLY_SKU, BillingConstants.CHAMPION_ANNUAL_SKU);

    PlanType(String productIdMonthly, String productIdAnnual) {
        this.productIdMonthly = productIdMonthly;
        this.productIdAnnual = productIdAnnual;
    }

    private final String productIdMonthly;
    private final String productIdAnnual;

    public String getProductIdMonthly() {
        return productIdMonthly;
    }

    public String getProductIdAnnual() {
        return productIdAnnual;
    }

    public PlanType getByProductId(String productId) {
        for (PlanType planType : PlanType.values()) {
            if (planType.productIdMonthly.equals(productId) || planType.productIdAnnual.equals(productId)) {
                return planType;
            }
        }
        return PlanType.FREE;
    }
}
