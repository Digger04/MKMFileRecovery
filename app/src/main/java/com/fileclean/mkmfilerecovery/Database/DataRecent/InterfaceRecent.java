package com.fileclean.mkmfilerecovery.Database.DataRecent;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfaceRecent {
    @Query("SELECT * FROM recent")
    List<InforRecent> getall();

    @Insert
    void insert (InforRecent history);

    @Delete
    void delete (InforRecent history);
}