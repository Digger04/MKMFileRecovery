package com.fileclean.mkmfilerecovery.Model;

public class InfoFile {
    private String path;
    private String name;
    private String date;
    private String size;
    private boolean isSelected;

    public InfoFile(String path, String name, String date, String size) {
        this.path = path;
        this.name = name;
        this.date = date;
        this.size = size;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
