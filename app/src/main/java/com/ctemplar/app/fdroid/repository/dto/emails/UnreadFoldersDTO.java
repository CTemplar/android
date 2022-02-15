package com.ctemplar.app.fdroid.repository.dto.emails;

import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class UnreadFoldersDTO {
    private int inbox;
    private int draft;
    private int starred;
    private int spam;
    private int outboxDeadManCounter;
    private int outboxDelayedDeliveryCounter;
    private int outboxSelfDestructCounter;

    public UnreadFoldersDTO() {
    }

    public UnreadFoldersDTO(int inbox, int draft, int starred, int spam, int outboxDeadManCounter,
                            int outboxDelayedDeliveryCounter, int outboxSelfDestructCounter) {
        this.inbox = inbox;
        this.draft = draft;
        this.starred = starred;
        this.spam = spam;
        this.outboxDeadManCounter = outboxDeadManCounter;
        this.outboxDelayedDeliveryCounter = outboxDelayedDeliveryCounter;
        this.outboxSelfDestructCounter = outboxSelfDestructCounter;
    }

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

    public int getOutbox() {
        return this.outboxDeadManCounter + this.outboxDelayedDeliveryCounter
                + this.outboxSelfDestructCounter;
    }

    public String getInboxString() {
        return EditTextUtils.intToStringPositive(inbox);
    }

    public String getDraftString() {
        return EditTextUtils.intToStringPositive(draft);
    }

    public String getStarredString() {
        return EditTextUtils.intToStringPositive(starred);
    }

    public String getSpamString() {
        return EditTextUtils.intToStringPositive(spam);
    }

    public String getOutboxDeadManCounterString() {
        return EditTextUtils.intToStringPositive(outboxDeadManCounter);
    }

    public String getOutboxDelayedDeliveryCounterString() {
        return EditTextUtils.intToStringPositive(outboxDelayedDeliveryCounter);
    }

    public String getOutboxSelfDestructCounterString() {
        return EditTextUtils.intToStringPositive(outboxSelfDestructCounter);
    }

    public String getOutboxString() {
        return EditTextUtils.intToStringPositive(getOutbox());
    }
}
