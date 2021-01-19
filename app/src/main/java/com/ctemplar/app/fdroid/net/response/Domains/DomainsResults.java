package com.ctemplar.app.fdroid.net.response.domains;

import com.google.gson.annotations.SerializedName;

public class DomainsResults {
    @SerializedName("id")
    private int id;

    @SerializedName("domain")
    private String domain;

    public int getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }
}
