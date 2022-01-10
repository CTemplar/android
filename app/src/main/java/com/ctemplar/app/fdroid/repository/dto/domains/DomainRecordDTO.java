package com.ctemplar.app.fdroid.repository.dto.domains;

import com.ctemplar.app.fdroid.net.response.domains.DomainRecordResponse;

public class DomainRecordDTO {
    private String type;
    private String host;
    private String value;
    private int priority;

    public DomainRecordDTO() {
    }

    public DomainRecordDTO(String type, String host, String value, int priority) {
        this.type = type;
        this.host = host;
        this.value = value;
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static DomainRecordDTO get(DomainRecordResponse response) {
        return new DomainRecordDTO(
                response.getType(),
                response.getHost(),
                response.getValue(),
                response.getPriority()
        );
    }
}
