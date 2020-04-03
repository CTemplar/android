package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class SignatureRequest {

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("signature")
    private String signature;

    public SignatureRequest(String displayName, String signature) {
        this.displayName = displayName;
        this.signature = signature;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
