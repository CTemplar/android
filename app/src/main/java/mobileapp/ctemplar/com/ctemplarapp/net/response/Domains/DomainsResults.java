package mobileapp.ctemplar.com.ctemplarapp.net.response.domains;

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
