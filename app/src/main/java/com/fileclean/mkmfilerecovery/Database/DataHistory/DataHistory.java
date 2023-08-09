package com.fileclean.mkmfilerecovery.Database.DataHistory;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InfofileHistory.class},version = 1)
public abstract class DataHistory extends RoomDatabase {

    public static final String database_name = "table_history";
    public static DataHistory INSTANCE;

    public static synchronized DataHistory getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataHistory.class, database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract InterfaceHistory daoSql();
}

