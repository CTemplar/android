package com.ctemplar.app.fdroid.net.response.notification;

import com.ctemplar.app.fdroid.net.response.Messages.MessagesResult;
import com.google.gson.annotations.SerializedName;

public class NotificationMessageResponse {
    @SerializedName("mail")
    private MessagesResult mail;

    public MessagesResult getMail() {
        return mail;
    }

    public void setMail(MessagesResult mail) {
        this.mail = mail;
    }
}
