package com.ctemplar.app.fdroid.net.request.filters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailFilterRequest {
    @SerializedName("conditions")
    private List<EmailFilterConditionRequest> conditions;

    @SerializedName("name")
    private String name;

    @SerializedName("move_to")
    private boolean moveTo;

    @SerializedName("folder")
    private String folder;

    @SerializedName("mark_as_read")
    private boolean markAsRead;

    @SerializedName("mark_as_starred")
    private boolean markAsStarred;

    public EmailFilterRequest() {
    }

    public EmailFilterRequest(List<EmailFilterConditionRequest> conditions, String name, boolean moveTo, String folder, boolean markAsRead, boolean markAsStarred) {
        this.conditions = conditions;
        this.name = name;
        this.moveTo = moveTo;
        this.folder = folder;
        this.markAsRead = markAsRead;
        this.markAsStarred = markAsStarred;
    }

    public void setConditions(List<EmailFilterConditionRequest> conditions) {
        this.conditions = conditions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoveTo(boolean moveTo) {
        this.moveTo = moveTo;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setMarkAsRead(boolean markAsRead) {
        this.markAsRead = markAsRead;
    }

    public void setMarkAsStarred(boolean markAsStarred) {
        this.markAsStarred = markAsStarred;
    }
}
