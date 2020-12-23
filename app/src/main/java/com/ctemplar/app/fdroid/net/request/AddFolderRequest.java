package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class AddFolderRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    public AddFolderRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
