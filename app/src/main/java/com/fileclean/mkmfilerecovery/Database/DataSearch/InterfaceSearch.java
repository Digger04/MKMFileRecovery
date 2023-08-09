package com.fileclean.mkmfilerecovery.Database.DataSearch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfaceSearch {
    @Query("SELECT * FROM search")
    List<InfoSearch> getall();

    @Insert
    void insert (InfoSearch search);

    @Delete
    void delete (InfoSearch search);
}