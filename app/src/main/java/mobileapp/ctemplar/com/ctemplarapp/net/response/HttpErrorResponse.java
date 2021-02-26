package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class HttpErrorResponse {
    @SerializedName("error")
    private ErrorResponse error;

    public ErrorResponse getError() {
        return error;
    }
}
