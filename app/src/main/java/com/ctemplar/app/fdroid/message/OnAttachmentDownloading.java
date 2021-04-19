package com.ctemplar.app.fdroid.message;

import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;
import com.ctemplar.app.fdroid.repository.provider.MessageProvider;

interface OnAttachmentDownloading {
    void onStart(AttachmentProvider attachmentProvider, MessageProvider message);
}
