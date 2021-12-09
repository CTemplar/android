package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class WarnExternalLinkRequest {
    @SerializedName("warn_external_link")
    private boolean warnExternalLink;

    public WarnExternalLinkRequest(boolean warnExternalLink) {
        this.warnExternalLink = warnExternalLink;
    }

    public void setWarnExternalLink(boolean warnExternalLink) {
        this.warnExternalLink = warnExternalLink;
    }
}
