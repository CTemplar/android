package mobileapp.ctemplar.com.ctemplarapp.message;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;

interface OnAttachmentDownloading {
    void onStart(AttachmentProvider attachmentProvider);
}
