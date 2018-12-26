package mobileapp.ctemplar.com.ctemplarapp.net.request;

public class MoveToFolderRequest {
    private String folder;

    public MoveToFolderRequest(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
