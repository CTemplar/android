package mobileapp.ctemplar.com.ctemplarapp.net.response.Folders;

import com.google.gson.annotations.SerializedName;

public class FoldersResult {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
