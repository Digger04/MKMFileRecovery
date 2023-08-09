package com.fileclean.mkmfilerecovery.Database.DataTimeOpenAgo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeopen")
public class InfoTimeOpen {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;
    private long time;

    public InfoTimeOpen(String type, long time) {
        this.type = type;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

