package com.ctemplar.app.fdroid.services;

public class ServiceConstants {
    public static final String SEND_MAIL_SERVICE_ACTION = "com.ctemplar.service.mail.send.action";
    public static final String SEND_MAIL_SERVICE_CHANNEL_ID = "com.ctemplar.service.mail.send.sending";

    public static final int NOTIFICATION_SERVICE_FOREGROUND_ID = 101;
    public static final String NOTIFICATION_SERVICE_CHANNEL_ID = "com.ctemplar.emails";
    public static final String NOTIFICATION_SERVICE_FOREGROUND_CHANNEL_ID = "com.ctemplar.notification.foreground";
    public static final String NOTIFICATION_SERVICE_ACTION_START = "com.ctemplar.service.notification.action.start";
    public static final String NOTIFICATION_SERVICE_ACTION_STOP = "com.ctemplar.service.notification.action.stop";
    public static final String NOTIFICATION_SERVICE_ACTION_RESTART = "com.ctemplar.service.notification.action.restart";
    public static final String FROM_NOTIFICATION_SERVICE = "com.ctemplar.service.notification.from";

    public static final int DOWNLOAD_ATTACHMENT_SERVICE_FOREGROUND_ID = 103;
    public static final String DOWNLOAD_ATTACHMENT_SERVICE_FOREGROUND_CHANNEL_ID = "com.ctemplar.service.attachment.download.foreground";
    public static final String DOWNLOAD_ATTACHMENT_SERVICE_TASK_EXTRA_KEY = "com.ctemplar.service.attachment.download.key.extra.task";
    public static final String DOWNLOAD_ATTACHMENT_SERVICE_ADD_TO_QUEUE_ACTION = "com.ctemplar.service.attachment.download.action.queue.add";
    public static final String DOWNLOAD_ATTACHMENT_SERVICE_CANCEL_NOTIFICATION_ACTION = "com.ctemplar.service.attachment.download.action.notification.cancel";
    public static final String DOWNLOAD_ATTACHMENT_SERVICE_CANCEL_TASK_ACTION = "com.ctemplar.service.attachment.download.action.task.cancel";
}
