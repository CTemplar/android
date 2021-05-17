package com.ctemplar.app.fdroid.net.request.filters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailFilterOrderListRequest {
    @SerializedName("filter_list")
    private List<EmailFilterOrderRequest> filterList;

    public EmailFilterOrderListRequest() {
    }

    public EmailFilterOrderListRequest(List<EmailFilterOrderRequest> filterList) {
        this.filterList = filterList;
    }

    public void setFilterList(List<EmailFilterOrderRequest> filterList) {
        this.filterList = filterList;
    }
}
