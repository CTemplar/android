package com.ctemplar.app.fdroid.services.download;

public class DownloadAttachmentInfo {
    public String url;
    public String name;
    public GpgEncryption gpgEncryption;
    public PgpEncryption pgpEncryption;

    public DownloadAttachmentInfo(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public static class GpgEncryption {
        public String password;
    }

    public static class PgpEncryption {
        public String password;
        public long mailboxId;
    }
}
