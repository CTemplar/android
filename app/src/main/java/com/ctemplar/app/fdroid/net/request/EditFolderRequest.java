package com.ctemplar.app.fdroid.net.request;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
