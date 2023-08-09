package com.fileclean.mkmfilerecovery.Database.DataHistory;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfaceHistory {
    @Query("SELECT * FROM history")
    List<InfofileHistory> getall();

    @Insert
    void insert (InfofileHistory history);

    @Delete
    void delete (InfofileHistory history);
}

