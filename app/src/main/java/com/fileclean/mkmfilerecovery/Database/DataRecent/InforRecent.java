package com.fileclean.mkmfilerecovery.Database.DataRecent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent")
public class InforRecent {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String path;
    private String name;
    private String date;
    private String size;

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

    public InforRecent(String path, String name, String date, String size) {
        this.path = path;
        this.name = name;
        this.date = date;
        this.size = size;
    }
}


