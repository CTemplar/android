package mobileapp.ctemplar.com.ctemplarapp.net.response.Filters;

import com.google.gson.annotations.SerializedName;

public class FilterResult {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("parameter")
    private String parameter;

    @SerializedName("condition")
    private String condition;

    @SerializedName("filter_text")
    private String filterText;

    @SerializedName("move_to")
    private boolean moveTo;

    @SerializedName("folder")
    private String folder;

    @SerializedName("mark_as_read")
    private boolean MarkAsRead;

    @SerializedName("mark_as_starred")
    private boolean markAsStarred;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public boolean isMoveTo() {
        return moveTo;
    }

    public String getFolder() {
        return folder;
    }

    public boolean isMarkAsRead() {
        return MarkAsRead;
    }

    public boolean isMarkAsStarred() {
        return markAsStarred;
    }
}
