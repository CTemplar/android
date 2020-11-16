package com.ctemplar.app.fdroid.net.response;

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
