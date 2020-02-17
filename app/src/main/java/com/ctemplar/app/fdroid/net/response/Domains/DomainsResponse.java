package com.ctemplar.app.fdroid.net.response.Domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DomainsResponse {

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("page_count")
    private int pageCount;

    @SerializedName("next")
    private boolean next;

    @SerializedName("previous")
    private boolean previous;

    @SerializedName("results")
    private List<DomainsResults> domainsResultsList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public boolean isPrevious() {
        return previous;
    }

    public void setPrevious(boolean previous) {
        this.previous = previous;
    }

    public List<DomainsResults> getDomainsResultsList() {
        return domainsResultsList;
    }

    public void setDomainsResultsList(List<DomainsResults> domainsResultsList) {
        this.domainsResultsList = domainsResultsList;
    }
}
