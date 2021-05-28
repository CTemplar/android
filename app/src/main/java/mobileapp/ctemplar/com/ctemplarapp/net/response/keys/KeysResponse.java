package mobileapp.ctemplar.com.ctemplarapp.net.response.keys;

import com.google.gson.annotations.SerializedName;

public class KeysResponse {
    @SerializedName("encrypt")
    private boolean encrypt;

    @SerializedName("keys")
    private KeyResponse[] keys;


    public boolean isEncrypt() {
        return encrypt;
    }

    public KeyResponse[] getKeys() {
        return keys;
    }
}
