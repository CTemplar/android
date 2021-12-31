package mobileapp.ctemplar.com.ctemplarapp.net.request.domains;

import com.google.gson.annotations.SerializedName;

public class CreateDomainRequest {
    @SerializedName("domain")
    private String domain;

    @SerializedName("ace")
    private String ace;

    @SerializedName("catch_all")
    private boolean catchAll;

    @SerializedName("catch_all_email")
    private String catchAllEmail;

    public CreateDomainRequest(String domain) {
        this.domain = domain;
    }

    public CreateDomainRequest(String domain, String ace, boolean catchAll, String catchAllEmail) {
        this.domain = domain;
        this.ace = ace;
        this.catchAll = catchAll;
        this.catchAllEmail = catchAllEmail;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setAce(String ace) {
        this.ace = ace;
    }

    public void setCatchAll(boolean catchAll) {
        this.catchAll = catchAll;
    }

    public void setCatchAllEmail(String catchAllEmail) {
        this.catchAllEmail = catchAllEmail;
    }
}
