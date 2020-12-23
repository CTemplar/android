package com.ctemplar.app.fdroid.net.entity;

import java.io.Serializable;
import java.util.List;

import com.ctemplar.app.fdroid.repository.provider.AttachmentProvider;

public class AttachmentsEntity implements Serializable {
    private List<AttachmentProvider> attachmentProviderList;

    public AttachmentsEntity(List<AttachmentProvider> attachmentProviderList) {
        this.attachmentProviderList = attachmentProviderList;
    }

    public AttachmentsEntity() { }

    public List<AttachmentProvider> getAttachmentProviderList() {
        return attachmentProviderList;
    }

    public void setAttachmentProviderList(List<AttachmentProvider> attachmentProviderList) {
        this.attachmentProviderList = attachmentProviderList;
    }
}
