package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class SubscriptionMobileUpgradeResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("plan_type")
    private String planType;


    public boolean isStatus() {
        return status;
    }

    public String getPlanType() {
        return planType;
    }
}
