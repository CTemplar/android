package mobileapp.ctemplar.com.ctemplarapp.message;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;

interface OnAttachmentDownloading {
    void onStart(AttachmentProvider attachmentProvider, MessageProvider message);
}
