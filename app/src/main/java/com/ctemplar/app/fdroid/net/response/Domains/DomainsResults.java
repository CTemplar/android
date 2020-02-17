package com.ctemplar.app.fdroid.net.response.Domains;

import com.google.gson.annotations.SerializedName;

public class DomainsResults {

    @SerializedName("id")
    private int id;

    @SerializedName("domain")
    private String domain;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
