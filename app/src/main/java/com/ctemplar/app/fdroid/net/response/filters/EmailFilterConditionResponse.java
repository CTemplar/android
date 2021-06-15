package com.ctemplar.app.fdroid.net.response.filters;

import com.google.gson.annotations.SerializedName;

public class EmailFilterConditionResponse {
    @SerializedName("id")
    private long id;

    @SerializedName("parameter")
    private String parameter;

    @SerializedName("condition")
    private String condition;

    @SerializedName("filter_text")
    private String filterText;


    public long getId() {
        return id;
    }

    public String getParameter() {
        return parameter;
    }

    public String getCondition() {
        return condition;
    }

    public String getFilterText() {
        return filterText;
    }
}
