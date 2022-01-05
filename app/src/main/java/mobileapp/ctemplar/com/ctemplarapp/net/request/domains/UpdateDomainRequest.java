package mobileapp.ctemplar.com.ctemplarapp.net.request.domains;

import com.google.gson.annotations.SerializedName;

public class UpdateDomainRequest {
    @SerializedName("catch_all")
    private Boolean catchAll;

    @SerializedName("catch_all_email")
    private String catchAllEmail;

    public UpdateDomainRequest() {
    }

    public UpdateDomainRequest(boolean catchAll) {
        this.catchAll = catchAll;
    }

    public UpdateDomainRequest(String catchAllEmail) {
        this.catchAllEmail = catchAllEmail;
    }

    public UpdateDomainRequest(boolean catchAll, String catchAllEmail) {
        this.catchAll = catchAll;
        this.catchAllEmail = catchAllEmail;
    }

    public void setCatchAll(boolean catchAll) {
        this.catchAll = catchAll;
    }

    public void setCatchAllEmail(String catchAllEmail) {
        this.catchAllEmail = catchAllEmail;
    }
}
