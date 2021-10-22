package com.ctemplar.app.fdroid.message;

import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

interface AttachmentDownloader {
    void downloadAttachment(MessageProvider message, AttachmentProvider attachmentProvider);
    void downloadAttachments(MessageProvider message, AttachmentProvider[] attachmentProviders);
}
