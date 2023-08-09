package com.fileclean.mkmfilerecovery.Database.DataOTP;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InfoOTP.class},version = 1)
public abstract class DataOTP extends RoomDatabase {

    public static final String database_name = "table_otp";
    public static DataOTP INSTANCE;

    public static synchronized DataOTP getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataOTP.class, database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract InterfaceOTP daoSql();
}

