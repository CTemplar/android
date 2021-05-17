package mobileapp.ctemplar.com.ctemplarapp.net.response.filters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailFilterOrderListResponse {
    @SerializedName("filter_list")
    private List<EmailFilterOrderResponse> filterList;


    public List<EmailFilterOrderResponse> getFilterList() {
        return filterList;
    }
}
