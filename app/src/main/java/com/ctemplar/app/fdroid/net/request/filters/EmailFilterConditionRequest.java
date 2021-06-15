package com.ctemplar.app.fdroid.net.request.filters;

import com.google.gson.annotations.SerializedName;

public class EmailFilterConditionRequest {
    @SerializedName("parameter")
    private String parameter;

    @SerializedName("condition")
    private String condition;

    @SerializedName("filter_text")
    private String filterText;

    public EmailFilterConditionRequest() {
    }

    public EmailFilterConditionRequest(String parameter, String condition, String filterText) {
        this.parameter = parameter;
        this.condition = condition;
        this.filterText = filterText;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }
}
