package com.ctemplar.app.fdroid.net.response.Contacts;

import com.google.gson.annotations.SerializedName;

public class ContactsResponse {

    @SerializedName("totalCount")
    int totalCount;

    @SerializedName("pageCount")
    int pageCount;

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
