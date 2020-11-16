package com.ctemplar.app.fdroid.net.response;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("status_code")
    private String statusCode;

    @SerializedName("status_description")
    private String statusDescription;

    @SerializedName("status")
    private boolean status;

    @SerializedName("error")
    private String error;

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public boolean isStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
