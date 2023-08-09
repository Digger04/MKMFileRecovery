package com.fileclean.mkmfilerecovery.Database.DataSearch;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search")
public class InfoSearch {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String data;

    public InfoSearch(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
