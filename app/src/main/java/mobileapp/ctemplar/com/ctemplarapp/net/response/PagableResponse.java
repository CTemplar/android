package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagableResponse<T> {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("page_count")
    private int pageCount;

    @SerializedName("next")
    private boolean next;

    @SerializedName("previous")
    private boolean previous;

    @SerializedName("results")
    private List<T> results;

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

    public List<T> getResults() {
        return results;
    }
}
