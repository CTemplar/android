package com.ctemplar.app.fdroid.net.response.folders;

import com.google.gson.annotations.SerializedName;

public class CustomFolderResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    @SerializedName("sort_order")
    private int sortOrder;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
