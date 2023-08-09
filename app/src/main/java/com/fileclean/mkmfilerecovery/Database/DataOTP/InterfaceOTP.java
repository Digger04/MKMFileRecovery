package com.fileclean.mkmfilerecovery.Database.DataOTP;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfaceOTP {
    @Query("SELECT * FROM otp")
    List<InfoOTP> getall();

    @Insert
    void insert (InfoOTP otp);

    @Delete
    void delete (InfoOTP otp);
}
