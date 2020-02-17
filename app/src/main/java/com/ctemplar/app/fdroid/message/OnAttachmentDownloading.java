package com.ctemplar.app.fdroid.message;

import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;

interface OnAttachmentDownloading {
    void onStart(AttachmentProvider attachmentProvider);
}
