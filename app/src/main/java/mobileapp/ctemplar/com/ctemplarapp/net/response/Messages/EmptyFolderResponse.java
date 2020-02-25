package mobileapp.ctemplar.com.ctemplarapp.net.response.Messages;

import com.google.gson.annotations.SerializedName;

public class EmptyFolderResponse {

    @SerializedName("detail")
    private String detail;

    @SerializedName("message")
    private String message;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
