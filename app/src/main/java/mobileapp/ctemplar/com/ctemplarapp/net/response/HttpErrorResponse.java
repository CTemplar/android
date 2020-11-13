package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class HttpErrorResponse {
    @SerializedName("detail")
    private String[] detail;

    @SerializedName("error")
    private ErrorResponse error;

    public String[] getDetail() {
        return detail;
    }

    public ErrorResponse getError() {
        return error;
    }
}
