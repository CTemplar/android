package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class DefaultMailboxRequest {

    @SerializedName("is_default")
    private boolean isDefault;

    public DefaultMailboxRequest() {
        this.isDefault = true;
    }

    public DefaultMailboxRequest(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
