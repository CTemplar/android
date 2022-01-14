package com.ctemplar.app.fdroid.net.response.filters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailFilterResponse {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("page_count")
    private int pageCount;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<EmailFilterResult> results;


    public int getTotalCount() {
        return totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<EmailFilterResult> getResults() {
        return results;
    }
}