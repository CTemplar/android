package mobileapp.ctemplar.com.ctemplarapp.message;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;

interface AttachmentDownloader {
    void downloadAttachment(MessageProvider message, AttachmentProvider attachmentProvider);
    void downloadAttachments(MessageProvider message, AttachmentProvider[] attachmentProviders);
}
