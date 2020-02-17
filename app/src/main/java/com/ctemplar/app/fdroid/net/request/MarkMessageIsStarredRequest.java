package com.ctemplar.app.fdroid.net.request;

public class MarkMessageIsStarredRequest {
    private boolean starred;

    public MarkMessageIsStarredRequest(boolean starred) {
        this.starred = starred;
    }


    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}
