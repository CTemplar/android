package com.ctemplar.app.fdroid.net.response.domains;

import com.google.gson.annotations.SerializedName;

public class DomainRecordResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("host")
    private String host;

    @SerializedName("value")
    private String value;

    @SerializedName("priority")
    private int priority;


    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public String getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }
}
