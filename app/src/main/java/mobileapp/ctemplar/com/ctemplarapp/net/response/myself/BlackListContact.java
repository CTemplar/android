package mobileapp.ctemplar.com.ctemplarapp.net.response.myself;

import com.google.gson.annotations.SerializedName;

public class BlackListContact {
    @SerializedName("id")
    public int id;

    @SerializedName("email")
    public String email;

    @SerializedName("name")
    public String name;

    public BlackListContact(){}

    public BlackListContact(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
