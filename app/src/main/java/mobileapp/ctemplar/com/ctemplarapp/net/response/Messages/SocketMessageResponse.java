package mobileapp.ctemplar.com.ctemplarapp.net.response.messages;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class SocketMessageResponse {
    @SerializedName("id")
    private long id;

    // to handle an empty string
    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("folder")
    private String folder;

    @SerializedName("folders")
    private String[] folders;

    @SerializedName("unread_count")
    private Map<String, Integer> unreadCount;

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("mail")
    private MessagesResult mail;


    public long getId() {
        return id;
    }

    public long getParentId() {
        try {
            return Long.parseLong(parentId);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getFolder() {
        return folder;
    }

    public String[] getFolders() {
        return folders;
    }

    public Map<String, Integer> getUnreadCount() {
        return unreadCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public MessagesResult getMail() {
        return mail;
    }
}
