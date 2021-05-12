package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class CustomFilterRequest {
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
    private boolean markAsRead;

    @SerializedName("mark_as_starred")
    private boolean markAsStarred;

    public CustomFilterRequest() {
    }

    public CustomFilterRequest(String name, String parameter, String condition, String filterText, boolean moveTo, String folder, boolean markAsRead, boolean markAsStarred) {
        this.name = name;
        this.parameter = parameter;
        this.condition = condition;
        this.filterText = filterText;
        this.moveTo = moveTo;
        this.folder = folder;
        this.markAsRead = markAsRead;
        this.markAsStarred = markAsStarred;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public boolean isMoveTo() {
        return moveTo;
    }

    public void setMoveTo(boolean moveTo) {
        this.moveTo = moveTo;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public boolean isMarkAsRead() {
        return markAsRead;
    }

    public void setMarkAsRead(boolean markAsRead) {
        this.markAsRead = markAsRead;
    }

    public boolean isMarkAsStarred() {
        return markAsStarred;
    }

    public void setMarkAsStarred(boolean markAsStarred) {
        this.markAsStarred = markAsStarred;
    }
}
