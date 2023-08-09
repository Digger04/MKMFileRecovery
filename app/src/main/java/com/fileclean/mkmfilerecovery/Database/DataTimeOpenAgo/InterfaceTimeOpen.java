package com.fileclean.mkmfilerecovery.Database.DataTimeOpenAgo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfaceTimeOpen {
    @Query("SELECT * FROM timeopen")
    List<InfoTimeOpen> getall();

    @Insert
    void insert (InfoTimeOpen timeOpen);

    @Delete
    void delete (InfoTimeOpen timeOpen);
}
