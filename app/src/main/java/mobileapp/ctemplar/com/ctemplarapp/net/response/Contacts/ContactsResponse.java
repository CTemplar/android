package mobileapp.ctemplar.com.ctemplarapp.net.response.contacts;

import com.google.gson.annotations.SerializedName;

public class ContactsResponse {
    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("pageCount")
    private int pageCount;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private ContactData[] results;


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

    public ContactData[] getResults() {
        return results;
    }
}
