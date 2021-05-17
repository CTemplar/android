package mobileapp.ctemplar.com.ctemplarapp.net.request.filters;

import com.google.gson.annotations.SerializedName;

public class EmailFilterConditionRequest {
    @SerializedName("id")
    private long id;

    @SerializedName("parameter")
    private String parameter;

    @SerializedName("condition")
    private String condition;

    @SerializedName("filter_text")
    private String filterText;

    public EmailFilterConditionRequest() {
    }

    public EmailFilterConditionRequest(long id, String parameter, String condition, String filterText) {
        this.id = id;
        this.parameter = parameter;
        this.condition = condition;
        this.filterText = filterText;
    }

    public void setId(long id) {
        this.id = id;
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
