package com.ctemplar.app.fdroid.repository.dto.headers;

import java.util.Map;

import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class IncomingHeadersDTO {
    private Map<String, String> map;
    private String envelopeTo;
    private String received;
    private String contentType;
    private String mimeVersion;
    private String subject;
    private String from;
    private String to;
    private String date;
    private String messageId;
    private String receivedSPF;
    private String xSpamReport;
    private String dkimSignature;
    private String listUnsubscribe;
    private String unsubscribeUrl;
    private String mailTo;

    public IncomingHeadersDTO() {
    }

    public IncomingHeadersDTO(Map<String, String> map, String envelopeTo, String received,
                              String contentType, String mimeVersion, String subject, String from,
                              String to, String date, String messageId, String receivedSPF,
                              String xSpamReport, String dkimSignature, String listUnsubscribe) {
        this.map = map;
        this.envelopeTo = envelopeTo;
        this.received = received;
        this.contentType = contentType;
        this.mimeVersion = mimeVersion;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.date = date;
        this.messageId = messageId;
        this.receivedSPF = receivedSPF;
        this.xSpamReport = xSpamReport;
        this.dkimSignature = dkimSignature;
        this.listUnsubscribe = listUnsubscribe;
        if (listUnsubscribe != null) {
            String url = EditTextUtils.extractUnsubscribeUrl(listUnsubscribe);
            if (url.length() > 0) {
                this.unsubscribeUrl = url;
            }
            String address = EditTextUtils.extractAddress(listUnsubscribe);
            if (address.length() > 0) {
                this.mailTo = address;
            }
        }
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getEnvelopeTo() {
        return envelopeTo;
    }

    public void setEnvelopeTo(String envelopeTo) {
        this.envelopeTo = envelopeTo;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMimeVersion() {
        return mimeVersion;
    }

    public void setMimeVersion(String mimeVersion) {
        this.mimeVersion = mimeVersion;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReceivedSPF() {
        return receivedSPF;
    }

    public void setReceivedSPF(String receivedSPF) {
        this.receivedSPF = receivedSPF;
    }

    public String getxSpamReport() {
        return xSpamReport;
    }

    public void setxSpamReport(String xSpamReport) {
        this.xSpamReport = xSpamReport;
    }

    public String getDkimSignature() {
        return dkimSignature;
    }

    public void setDkimSignature(String dkimSignature) {
        this.dkimSignature = dkimSignature;
    }

    public String getListUnsubscribe() {
        return listUnsubscribe;
    }

    public void setListUnsubscribe(String listUnsubscribe) {
        this.listUnsubscribe = listUnsubscribe;
    }

    public String getUnsubscribeUrl() {
        return unsubscribeUrl;
    }

    public void setUnsubscribeUrl(String unsubscribeUrl) {
        this.unsubscribeUrl = unsubscribeUrl;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }
}
