package com.fileclean.mkmfilerecovery.Database.DataHide;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfaceHide {
    @Query("SELECT * FROM hide")
    List<InfoFileHide> getall();

    @Insert
    void insert (InfoFileHide hide);

    @Delete
    void delete (InfoFileHide hide);
}
