package mobileapp.ctemplar.com.ctemplarapp.repository.dto.folders;

public class CustomFolderDTO {
    private int id;
    private String name;
    private String color;
    private int sortOrder;

    public CustomFolderDTO() {
    }

    public CustomFolderDTO(int id, String name, String color, int sortOrder) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sortOrder = sortOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
