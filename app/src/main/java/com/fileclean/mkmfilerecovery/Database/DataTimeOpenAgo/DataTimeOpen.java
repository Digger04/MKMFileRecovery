package com.fileclean.mkmfilerecovery.Database.DataTimeOpenAgo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InfoTimeOpen.class},version = 1)
public abstract class DataTimeOpen extends RoomDatabase {

    public static final String database_name = "table_timeopen";
    public static DataTimeOpen INSTANCE;

    public static synchronized DataTimeOpen getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataTimeOpen.class, database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract InterfaceTimeOpen daoSql();

}
