package com.ctemplar.app.fdroid.net.response.Myself;

import com.google.gson.annotations.SerializedName;

public class WhiteListContact {
    @SerializedName("id")
    public int id;

    @SerializedName("email")
    public String email;

    @SerializedName("name")
    public String name;

    public WhiteListContact() {}

    public WhiteListContact(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
