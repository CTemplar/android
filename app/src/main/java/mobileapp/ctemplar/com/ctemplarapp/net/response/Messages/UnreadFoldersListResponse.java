package mobileapp.ctemplar.com.ctemplarapp.net.response.Messages;

import com.google.gson.annotations.SerializedName;

public class UnreadFoldersListResponse {

    @SerializedName("inbox")
    private int inbox;

    @SerializedName("draft")
    private int draft;

    @SerializedName("archive")
    private int archive;

    @SerializedName("spam")
    private int spam;

    @SerializedName("trash")
    private int trash;

    @SerializedName("starred")
    private int starred;

    @SerializedName("outbox_dead_man_counter")
    private int outboxDeadManCounter;

    @SerializedName("outbox_delayed_delivery_counter")
    private int outboxDelayedDeliveryCounter;

    @SerializedName("outbox_self_destruct_counter")
    private int outboxSelfDestructCounter;

    public int getInbox() {
        return inbox;
    }

    public void setInbox(int inbox) {
        this.inbox = inbox;
    }

    public int getDraft() {
        return draft;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public int getSpam() {
        return spam;
    }

    public void setSpam(int spam) {
        this.spam = spam;
    }

    public int getTrash() {
        return trash;
    }

    public void setTrash(int trash) {
        this.trash = trash;
    }

    public int getStarred() {
        return starred;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public int getOutboxDeadManCounter() {
        return outboxDeadManCounter;
    }

    public void setOutboxDeadManCounter(int outboxDeadManCounter) {
        this.outboxDeadManCounter = outboxDeadManCounter;
    }

    public int getOutboxDelayedDeliveryCounter() {
        return outboxDelayedDeliveryCounter;
    }

    public void setOutboxDelayedDeliveryCounter(int outboxDelayedDeliveryCounter) {
        this.outboxDelayedDeliveryCounter = outboxDelayedDeliveryCounter;
    }

    public int getOutboxSelfDestructCounter() {
        return outboxSelfDestructCounter;
    }

    public void setOutboxSelfDestructCounter(int outboxSelfDestructCounter) {
        this.outboxSelfDestructCounter = outboxSelfDestructCounter;
    }
}
