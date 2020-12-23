package com.ctemplar.app.fdroid.net.response.whiteBlackList;

import com.google.gson.annotations.SerializedName;

import com.ctemplar.app.fdroid.net.response.myself.BlackListContact;

public class BlackListResponse {
    @SerializedName("total_count")
    private long totalCount;

    @SerializedName("page_count")
    private long pageCount;

    @SerializedName("results")
    private BlackListContact[] results;

    public long getTotalCount() {
        return totalCount;
    }

    public long getPageCount() {
        return pageCount;
    }

    public BlackListContact[] getResults() {
        return results;
    }
}
