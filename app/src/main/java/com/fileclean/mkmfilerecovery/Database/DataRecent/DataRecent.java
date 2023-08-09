package com.fileclean.mkmfilerecovery.Database.DataRecent;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InforRecent.class},version = 1)
public abstract class DataRecent extends RoomDatabase {

    public static final String database_name = "table_recent";
    public static DataRecent INSTANCE;

    public static synchronized DataRecent getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataRecent.class, database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract InterfaceRecent daoSql();
}
