package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

public class MyselfResponse {

    @SerializedName("total_count")
    public int totalCount;

    @SerializedName("page_count")
    public int pageCount;

    @SerializedName("results")
    public MyselfResult[] result;

}
