package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class NotificationEmailRequest {
    @SerializedName("notification_email")
    private String notificationEmail;

    public NotificationEmailRequest(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }
}
