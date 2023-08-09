package com.fileclean.mkmfilerecovery.Database.DataHide;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hide")
public class InfoFileHide {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String path;
    private String name;
    private String date;
    private String size;
    private String type;

    public InfoFileHide(String path, String name, String date, String size, String type) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.date = date;
        this.size = size;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
