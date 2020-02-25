package mobileapp.ctemplar.com.ctemplarapp.net.response.Myself;

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

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public MyselfResult[] getResult() {
        return result;
    }

    public void setResult(MyselfResult[] result) {
        this.result = result;
    }
}
