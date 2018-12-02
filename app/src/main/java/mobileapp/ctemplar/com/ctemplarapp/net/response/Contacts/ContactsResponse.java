package mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts;

import com.google.gson.annotations.SerializedName;

public class ContactsResponse {
    @SerializedName("total_count")
    int total_count;
    @SerializedName("page_count")
    int page_count;
    @SerializedName("next")
    String next;
    @SerializedName("previous")
    String previous;
    @SerializedName("results")
    private ContactData[] results;

    public ContactData[] getResults() {
        return results;
    }
}
