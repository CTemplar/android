package mobileapp.ctemplar.com.ctemplarapp.net.response.filters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailFilterResult {
    @SerializedName("id")
    private long id;

    @SerializedName("conditions")
    private List<EmailFilterConditionResponse> conditions;

    @SerializedName("name")
    private String name;

    @SerializedName("move_to")
    private boolean moveTo;

    @SerializedName("folder")
    private String folder;

    @SerializedName("mark_as_read")
    private boolean MarkAsRead;

    @SerializedName("mark_as_starred")
    private boolean markAsStarred;

    @SerializedName("priority_order")
    private int priorityOrder;

    @SerializedName("delete_msg")
    private boolean deleteMsg;


    public long getId() {
        return id;
    }

    public List<EmailFilterConditionResponse> getConditions() {
        return conditions;
    }

    public String getName() {
        return name;
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

    public int getPriorityOrder() {
        return priorityOrder;
    }

    public boolean isDeleteMsg() {
        return deleteMsg;
    }
}
