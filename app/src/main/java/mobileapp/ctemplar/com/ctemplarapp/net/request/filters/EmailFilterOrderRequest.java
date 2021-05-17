package mobileapp.ctemplar.com.ctemplarapp.net.request.filters;

import com.google.gson.annotations.SerializedName;

public class EmailFilterOrderRequest {
    @SerializedName("filter_id")
    private long filterId;

    @SerializedName("priority_order")
    private int priorityOrder;

    public EmailFilterOrderRequest() {
    }

    public EmailFilterOrderRequest(long filterId, int priorityOrder) {
        this.filterId = filterId;
        this.priorityOrder = priorityOrder;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public void setPriorityOrder(int priorityOrder) {
        this.priorityOrder = priorityOrder;
    }
}
