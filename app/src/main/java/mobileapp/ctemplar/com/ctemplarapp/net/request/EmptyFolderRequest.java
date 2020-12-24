package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class EmptyFolderRequest {
    @SerializedName("folder")
    private String folder;

    public EmptyFolderRequest(String folder) {
        this.folder = folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
