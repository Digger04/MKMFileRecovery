package com.fileclean.mkmfilerecovery.Database.DataHide;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InfoFileHide.class},version = 1)
public abstract class DataHide extends RoomDatabase {
    public static final String database_name = "table_hide";
    public static DataHide INSTANCE;

    public static synchronized DataHide getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataHide.class, database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract InterfaceHide daoSql();
}

