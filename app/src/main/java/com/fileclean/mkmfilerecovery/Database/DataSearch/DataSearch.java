package com.fileclean.mkmfilerecovery.Database.DataSearch;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InfoSearch.class},version = 1)
public abstract class DataSearch extends RoomDatabase {
    public static final String database_name = "table_search";
    public static DataSearch INSTANCE;

    public static synchronized DataSearch getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataSearch.class, database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract InterfaceSearch daoSql();
}

