package mobileapp.ctemplar.com.ctemplarapp.net.response.WhiteBlackLists;

import com.google.gson.annotations.SerializedName;

import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.WhiteListContact;

public class WhiteListResponse {

    @SerializedName("total_count")
    private long totalCount;

    @SerializedName("page_count")
    private long pageCount;

    @SerializedName("results")
    private WhiteListContact[] results;

    public long getTotalCount() {
        return totalCount;
    }

    public long getPageCount() {
        return pageCount;
    }

    public WhiteListContact[] getResults() {
        return results;
    }
}
