package com.ctemplar.app.fdroid.net.response.myself;

import com.google.gson.annotations.SerializedName;

public class MyselfResponse {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("page_count")
    private int pageCount;

    @SerializedName("results")
    private MyselfResult[] result;


    public int getTotalCount() {
        return totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public MyselfResult[] getResult() {
        return result;
    }
}
