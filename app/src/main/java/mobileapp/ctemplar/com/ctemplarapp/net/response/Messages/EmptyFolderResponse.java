package mobileapp.ctemplar.com.ctemplarapp.net.response.messages;

import com.google.gson.annotations.SerializedName;

public class EmptyFolderResponse {
    @SerializedName("detail")
    private String detail;

    @SerializedName("message")
    private String message;

    public String getDetail() {
        return detail;
    }

    public String getMessage() {
        return message;
    }
}
