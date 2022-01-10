package com.ctemplar.app.fdroid.net.response.domains;

import com.google.gson.annotations.SerializedName;

public class CustomDomainsResponse {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("page_count")
    private int pageCount;

    @SerializedName("next")
    private boolean next;

    @SerializedName("previous")
    private boolean previous;

    @SerializedName("results")
    private CustomDomainResponse[] results;


    public int getTotalCount() {
        return totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean isNext() {
        return next;
    }

    public boolean isPrevious() {
        return previous;
    }

    public CustomDomainResponse[] getResults() {
        return results;
    }
}
