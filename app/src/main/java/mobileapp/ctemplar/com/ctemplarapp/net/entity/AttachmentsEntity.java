package mobileapp.ctemplar.com.ctemplarapp.net.entity;

import java.io.Serializable;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;

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
