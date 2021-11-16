package com.ctemplar.app.fdroid.repository.dto;

public class SearchMessagesDTO {
    private String query;
    private boolean exact;
    private String folder;
    private String sender;
    private String receiver;
    private boolean haveAttachment;
    private String startDate;
    private String endDate;
    private long size;
    private String sizeOperator;

    public SearchMessagesDTO() {
    }

    public SearchMessagesDTO(String query) {
        this.query = query;
    }

    public SearchMessagesDTO(String query, boolean exact, String folder, String sender, String receiver, boolean haveAttachment, String startDate, String endDate, long size, String sizeOperator) {
        this.query = query;
        this.exact = exact;
        this.folder = folder;
        this.sender = sender;
        this.receiver = receiver;
        this.haveAttachment = haveAttachment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.size = size;
        this.sizeOperator = sizeOperator;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isExact() {
        return exact;
    }

    public void setExact(boolean exact) {
        this.exact = exact;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isHaveAttachment() {
        return haveAttachment;
    }

    public void setHaveAttachment(boolean haveAttachment) {
        this.haveAttachment = haveAttachment;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSizeOperator() {
        return sizeOperator;
    }

    public void setSizeOperator(String sizeOperator) {
        this.sizeOperator = sizeOperator;
    }
}
