package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class UpdateReportBugsRequest {
    @SerializedName("is_enable_report_bugs")
    private boolean isEnableReportBugs;

    public UpdateReportBugsRequest(boolean isEnableReportBugs) {
        this.isEnableReportBugs = isEnableReportBugs;
    }

    public void setEnableReportBugs(boolean enableReportBugs) {
        isEnableReportBugs = enableReportBugs;
    }
}
