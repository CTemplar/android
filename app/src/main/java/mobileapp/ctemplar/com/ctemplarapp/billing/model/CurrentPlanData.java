package mobileapp.ctemplar.com.ctemplarapp.billing.model;

import java.util.Date;

public class CurrentPlanData {
    public final PlanType planType;
    public final PaidPlanData paidData;

    public CurrentPlanData(PlanType planType, PaidPlanData paidData) {
        this.planType = planType;
        this.paidData = paidData;
    }

    public static class PaidPlanData {
        public PaidPlanData(Date endsAt, boolean isYearly) {
            this.endsAt = endsAt;
            this.isYearly = isYearly;
        }
        public final Date endsAt;
        public final boolean isYearly;
    }
}