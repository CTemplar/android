package mobileapp.ctemplar.com.ctemplarapp.net.response.filters;

import com.google.gson.annotations.SerializedName;

public class EmailFilterOrderResponse {
    @SerializedName("filter_id")
    private long filterId;

    @SerializedName("priority_order")
    private int priorityOrder;


    public long getFilterId() {
        return filterId;
    }

    public int getPriorityOrder() {
        return priorityOrder;
    }
}
