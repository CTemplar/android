package mobileapp.ctemplar.com.ctemplarapp.net.request.folders;

import com.google.gson.annotations.SerializedName;

public class EditFolderRequest {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    public EditFolderRequest(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
