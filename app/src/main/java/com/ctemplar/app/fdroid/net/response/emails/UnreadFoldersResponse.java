package com.ctemplar.app.fdroid.net.response.emails;

import com.google.gson.annotations.SerializedName;

public class UnreadFoldersResponse {
    @SerializedName("inbox")
    private int inbox;

    @SerializedName("draft")
    private int draft;

    @SerializedName("starred")
    private int starred;

    @SerializedName("spam")
    private int spam;

    @SerializedName("outbox_dead_man_counter")
    private int outboxDeadManCounter;

    @SerializedName("outbox_delayed_delivery_counter")
    private int outboxDelayedDeliveryCounter;

    @SerializedName("outbox_self_destruct_counter")
    private int outboxSelfDestructCounter;


    public int getInbox() {
        return inbox;
    }

    public int getDraft() {
        return draft;
    }

    public int getStarred() {
        return starred;
    }

    public int getSpam() {
        return spam;
    }

    public int getOutboxDeadManCounter() {
        return outboxDeadManCounter;
    }

    public int getOutboxDelayedDeliveryCounter() {
        return outboxDelayedDeliveryCounter;
    }

    public int getOutboxSelfDestructCounter() {
        return outboxSelfDestructCounter;
    }
}
