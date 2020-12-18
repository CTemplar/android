package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class KeyResponse {
    @SerializedName("encrypt")
    private boolean encrypt;

    @SerializedName("keys")
    private KeyResult[] keyResult;


    public boolean isEncrypt() {
        return encrypt;
    }

    public KeyResult[] getKeyResult() {
        return keyResult;
    }
}
